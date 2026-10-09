package dev.leo.ragdollreactions.neoforge;

import dev.leo.ragdollreactions.neoforge.client.ClientMotionSampler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.gui.ConfigurationScreen;
import net.minecraftforge.client.gui.IConfigScreenFactory;

@Mod(value = "ragdoll_reactions", dist = {Dist.CLIENT})
public final class RagdollReactionsNeoForgeClient {
   public RagdollReactionsNeoForgeClient(ModContainer container) {
      container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory) ConfigurationScreen::new);
      ClientMotionSampler.init();
   }
}
