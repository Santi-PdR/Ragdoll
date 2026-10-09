package dev.santi.ragdoll;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public final class RagdollManager {
    private static final Map<LivingEntity, State> ACTIVE = new WeakHashMap<>();
    private static final Map<LivingEntity, Integer> COOLDOWNS = new WeakHashMap<>();
    private static final Map<LivingEntity, Vec3> LAST_MOTION = new WeakHashMap<>();

    private RagdollManager() {}

    public static boolean start(LivingEntity entity, Vec3 impulse) {
        if (!RagdollConfig.ENABLED.get() || entity.level().isClientSide || !eligible(entity)) return false;
        Integer cooldown = COOLDOWNS.get(entity);
        if (cooldown != null && cooldown > 0) return false;

        State prior = ACTIVE.get(entity);
        if (prior == null) {
            boolean noGravity = entity.isNoGravity();
            boolean noAi = entity instanceof Mob mob && mob.isNoAi();
            prior = new State(noGravity, noAi, 0.0f, 7.0f);
            ACTIVE.put(entity, prior);
        } else {
            prior.ticksLeft = RagdollConfig.DURATION_TICKS.get();
            prior.spin = Math.min(18.0f, prior.spin + 3.0f);
        }

        Vec3 scaled = impulse.scale(RagdollConfig.IMPULSE_SCALE.get());
        entity.setDeltaMovement(entity.getDeltaMovement().add(scaled));
        entity.hasImpulse = true;
        entity.setNoGravity(true);
        if (entity instanceof Mob mob) mob.setNoAi(true);
        prior.ticksLeft = RagdollConfig.DURATION_TICKS.get();
        RagdollNetwork.sync(entity, prior.angle, prior.ticksLeft, true);
        return true;
    }

    public static void sampleCrash(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        Vec3 current = entity.getDeltaMovement();
        Vec3 previous = LAST_MOTION.put(entity, current);
        if (previous == null || ACTIVE.containsKey(entity)) return;
        double oldSpeed = Math.sqrt(previous.horizontalDistanceSqr());
        double change = Math.sqrt(previous.subtract(current).horizontalDistanceSqr());
        if (oldSpeed < RagdollConfig.CRASH_MIN_SPEED.get()
                || change < RagdollConfig.CRASH_SPEED_CHANGE.get()) return;
        Vec3 knockdown = previous.normalize().scale(-Math.min(1.4, oldSpeed * 0.8))
                .add(0.0, 0.20, 0.0);
        start(entity, knockdown);
    }

    public static void tick(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        Integer cooldown = COOLDOWNS.get(entity);
        if (cooldown != null) {
            if (cooldown <= 1) COOLDOWNS.remove(entity);
            else COOLDOWNS.put(entity, cooldown - 1);
        }

        State state = ACTIVE.get(entity);
        if (state == null) return;
        state.ticksLeft--;
        Vec3 velocity = entity.getDeltaMovement();
        entity.setDeltaMovement(velocity.x * 0.94, velocity.y - 0.035, velocity.z * 0.94);
        entity.hasImpulse = true;
        state.angle += state.spin;
        state.spin *= 0.94f;
        if (Math.abs(entity.getDeltaMovement().y) < 0.025) state.spin *= 0.90f;

        if (state.ticksLeft <= 0 || !entity.isAlive()) {
            stop(entity, state);
        } else if ((state.ticksLeft & 3) == 0) {
            RagdollNetwork.sync(entity, state.angle, state.ticksLeft, true);
        }
    }

    public static void stop(LivingEntity entity) {
        State state = ACTIVE.get(entity);
        if (state != null) stop(entity, state);
    }

    private static void stop(LivingEntity entity, State state) {
        ACTIVE.remove(entity);
        COOLDOWNS.put(entity, RagdollConfig.COOLDOWN_TICKS.get());
        entity.setNoGravity(state.noGravity);
        if (entity instanceof Mob mob) mob.setNoAi(state.noAi);
        RagdollNetwork.sync(entity, state.angle, 0, false);
    }

    private static boolean eligible(LivingEntity entity) {
        return entity instanceof net.minecraft.world.entity.player.Player
                ? RagdollConfig.AFFECT_PLAYERS.get()
                : RagdollConfig.AFFECT_MOBS.get();
    }

    public static final class State {
        public final boolean noGravity;
        public final boolean noAi;
        public float angle;
        public float spin;
        public int ticksLeft;

        private State(boolean noGravity, boolean noAi, float angle, float spin) {
            this.noGravity = noGravity;
            this.noAi = noAi;
            this.angle = angle;
            this.spin = spin;
        }
    }
}
