package dev.leo.sableplayerragdoll.neoforge;

import dev.leo.sableplayerragdoll.block.RagdollPartBlock;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.entity.RagdollDollEntity;
import dev.leo.sableplayerragdoll.entity.RagdollSeatEntity;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollCameraHelper;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollDollEntityRenderer;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollControlsHint;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollControlsHud;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollInputClient;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollKeybinds;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollPartBlockEntityRenderer;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollSeatEntityRenderer;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollGrabClient;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollBlockInteractClient;
import dev.leo.sableplayerragdoll.neoforge.config.RagdollClientConfig;
import dev.leo.sableplayerragdoll.mob.MobRagdollBlocks;
import dev.leo.sableplayerragdoll.mob.block.MobRagdollPartBlock;
import dev.leo.sableplayerragdoll.mob.client.MobRagdollPartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.gui.ConfigurationScreen;
import net.minecraftforge.client.gui.IConfigScreenFactory;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterGuiLayersEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(value = "sable_player_ragdoll", dist = {Dist.CLIENT})
public final class SablePlayerRagdollNeoForgeClient {
   @SuppressWarnings("unchecked")
   public SablePlayerRagdollNeoForgeClient(ModContainer container, IEventBus modBus) {
      container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory) ConfigurationScreen::new);
      RagdollClientConfig.register(container);
      RagdollCameraHelper.init();
      RagdollKeybinds.init(modBus);
      RagdollInputClient.init();
      RagdollControlsHint.init();
      RagdollGrabClient.init();
      RagdollBlockInteractClient.init();
      modBus.addListener(SablePlayerRagdollNeoForgeClient::registerEntityRenderers);
      modBus.addListener(SablePlayerRagdollNeoForgeClient::registerGuiLayers);
      MinecraftForge.EVENT_BUS.addListener(SablePlayerRagdollNeoForgeClient::onRenderHighlight);
   }

   private static void onRenderHighlight(RenderHighlightEvent.Block event) {
      Level level = Minecraft.getInstance().level;
      if (level == null) {
         return;
      }
      var block = level.getBlockState(event.getTarget().getBlockPos()).getBlock();
      if (block instanceof RagdollPartBlock || block instanceof MobRagdollPartBlock) {
         event.setCanceled(true);
      }
   }

   private static void registerGuiLayers(RegisterGuiLayersEvent event) {
      event.registerAboveAll(
         new ResourceLocation("sable_player_ragdoll", "ragdoll_controls"),
         RagdollControlsHud::render
      );
   }

   @SuppressWarnings("unchecked")
   private static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType<RagdollSeatEntity>) RagdollBlockRegistration.RAGDOLL_SEAT_ENTITY.get(), RagdollSeatEntityRenderer::new);
      event.registerEntityRenderer((EntityType<RagdollDollEntity>) RagdollBlockRegistration.RAGDOLL_DOLL_ENTITY.get(), RagdollDollEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType<RagdollPartBlockEntity>) RagdollBlockRegistration.RAGDOLL_PART_BLOCK_ENTITY.get(), RagdollPartBlockEntityRenderer::new);
      event.registerBlockEntityRenderer(MobRagdollBlocks.MOB_RAGDOLL_PART_ENTITY.get(), MobRagdollPartBlockEntityRenderer::new);
   }
}
