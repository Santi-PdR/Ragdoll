package dev.santi.ragdoll;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class RagdollEvents {
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.getAmount() < RagdollConfig.MIN_HIT_DAMAGE.get()) return;
        Vec3 direction = entity.getLookAngle().scale(-1.0);
        if (event.getSource().getEntity() != null) {
            direction = entity.position().subtract(event.getSource().getEntity().position()).normalize();
        }
        double force = Math.min(1.8, 0.35 + event.getAmount() * 0.08);
        RagdollManager.start(entity, direction.scale(force).add(0, 0.22, 0));
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.getDistance() < RagdollConfig.MIN_FALL_DISTANCE.get()) return;
        RagdollManager.start(entity, new Vec3(0.0, 0.18, 0.0));
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (event.getSource().is(DamageTypes.EXPLOSION)
                || event.getSource().is(DamageTypes.PLAYER_EXPLOSION)
                || event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            Vec3 impulse = entity.getDeltaMovement().normalize().scale(0.8).add(0, 0.25, 0);
            RagdollManager.start(entity, impulse);
        }
    }

    @SubscribeEvent
    public void onTick(LivingEvent.LivingTickEvent event) {
        RagdollManager.sampleCrash(event.getEntity());
        RagdollManager.tick(event.getEntity());
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        RagdollManager.stop(event.getEntity());
    }
}
