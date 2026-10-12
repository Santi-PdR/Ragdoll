package dev.leo.ragdollreactions.neoforge;

import dev.leo.ragdollreactions.neoforge.client.ClientMotionSampler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;

public final class RagdollReactionsNeoForgeClient {
   public RagdollReactionsNeoForgeClient(ModContainer container) {
      ClientMotionSampler.init();
   }
}
