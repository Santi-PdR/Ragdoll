package dev.leo.sableplayerragdoll.mob.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.TickEvent;

/** Tracks only mob ragdoll launches announced by the server to this client. */
public final class MobRagdollClientState {
    private static final long STARTUP_GRACE_TICKS = 40L;
    private static final Map<Integer, LaunchMark> LAUNCHES = new ConcurrentHashMap<>();
    private static net.minecraft.client.multiplayer.ClientLevel trackedLevel;

    private MobRagdollClientState() {
    }

    public static void markLaunch(Mob mob) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || mob.level() != minecraft.level) {
            return;
        }
        switchLevel(minecraft.level);
        LAUNCHES.put(mob.getId(), new LaunchMark(mob.getUUID(), minecraft.level.getGameTime()));
    }

    public static boolean isRagdollSource(Mob mob) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || mob.level() != minecraft.level) {
            return false;
        }
        switchLevel(minecraft.level);
        LaunchMark mark = LAUNCHES.get(mob.getId());
        if (mark == null || !mark.entityId().equals(mob.getUUID())) {
            return false;
        }
        long age = minecraft.level.getGameTime() - mark.startedAt();
        if (age < 0L) {
            LAUNCHES.remove(mob.getId(), mark);
            return false;
        }
        if (mob.isInvisible() && mob.isNoAi()) {
            return true;
        }
        if (age <= STARTUP_GRACE_TICKS) {
            return true;
        }
        LAUNCHES.remove(mob.getId(), mark);
        return false;
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            LAUNCHES.clear();
            trackedLevel = null;
            return;
        }
        switchLevel(minecraft.level);
        long now = minecraft.level.getGameTime();
        for (Map.Entry<Integer, LaunchMark> entry : LAUNCHES.entrySet()) {
            var entity = minecraft.level.getEntity(entry.getKey());
            long age = now - entry.getValue().startedAt();
            if (entity == null || !entity.getUUID().equals(entry.getValue().entityId()) || age < 0L
                    || (age > STARTUP_GRACE_TICKS
                    && (!(entity instanceof Mob mob) || !mob.isInvisible() || !mob.isNoAi()))) {
                LAUNCHES.remove(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void switchLevel(net.minecraft.client.multiplayer.ClientLevel level) {
        if (trackedLevel != level) {
            LAUNCHES.clear();
            trackedLevel = level;
        }
    }

    private record LaunchMark(java.util.UUID entityId, long startedAt) {
    }
}
