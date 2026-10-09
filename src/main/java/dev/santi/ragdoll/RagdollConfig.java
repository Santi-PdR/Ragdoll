package dev.santi.ragdoll;

import net.minecraftforge.common.ForgeConfigSpec;

public final class RagdollConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue AFFECT_PLAYERS;
    public static final ForgeConfigSpec.BooleanValue AFFECT_MOBS;
    public static final ForgeConfigSpec.DoubleValue MIN_HIT_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue MIN_FALL_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue IMPULSE_SCALE;
    public static final ForgeConfigSpec.IntValue DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue COOLDOWN_TICKS;
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("general");
        ENABLED = BUILDER.define("enabled", true);
        AFFECT_PLAYERS = BUILDER.define("affectPlayers", true);
        AFFECT_MOBS = BUILDER.define("affectMobs", true);
        MIN_HIT_DAMAGE = BUILDER.defineInRange("minimumHitDamage", 5.0, 0.0, 1000.0);
        MIN_FALL_DISTANCE = BUILDER.defineInRange("minimumFallDistance", 5.0, 0.0, 256.0);
        IMPULSE_SCALE = BUILDER.defineInRange("impulseScale", 0.8, 0.0, 8.0);
        DURATION_TICKS = BUILDER.defineInRange("durationTicks", 80, 10, 1200);
        COOLDOWN_TICKS = BUILDER.defineInRange("cooldownTicks", 40, 0, 1200);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private RagdollConfig() {}
}
