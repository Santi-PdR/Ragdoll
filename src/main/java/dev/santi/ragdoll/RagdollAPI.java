package dev.santi.ragdoll;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Public entry point for other Forge mods to launch or end a ragdoll. */
public final class RagdollAPI {
    private RagdollAPI() {}

    public static boolean launch(LivingEntity entity, Vec3 impulse) {
        return RagdollManager.start(entity, impulse);
    }

    public static void end(LivingEntity entity) {
        RagdollManager.stop(entity);
    }
}
