package dev.santi.ragdoll;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(RagdollMod.MOD_ID)
public final class RagdollMod {
    public static final String MOD_ID = "ragdoll";

    public RagdollMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RagdollConfig.SPEC);
        RagdollNetwork.register();
        MinecraftForge.EVENT_BUS.register(new RagdollEvents());
    }
}
