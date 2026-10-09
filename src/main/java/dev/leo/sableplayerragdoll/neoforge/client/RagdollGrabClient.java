package dev.leo.sableplayerragdoll.neoforge.client;

import dev.leo.sableplayerragdoll.neoforge.network.RagdollNetworking;

import dev.leo.sableplayerragdoll.RagdollCollisionRules;
import dev.leo.sableplayerragdoll.config.RagdollSettings;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.mob.block.entity.MobRagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.neoforge.network.RagdollGrabPacket;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.ClientTickEvent.Post;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;

public final class RagdollGrabClient {
   private static final int COLLISION_GRACE_TICKS = 20;
   @Nullable
   private static BlockPos activePos;
   private static int collisionGraceTicks;

   private RagdollGrabClient() {
   }

   public static void init() {
      MinecraftForge.EVENT_BUS.addListener(RagdollGrabClient::onClientTick);
      MinecraftForge.EVENT_BUS.addListener(RagdollGrabClient::onScroll);
      RagdollCollisionRules.setLocalGrabActive(RagdollGrabClient::isGrabbing);
   }

   public static boolean isGrabbing() {
      return activePos != null || collisionGraceTicks > 0;
   }

   private static void onClientTick(Post event) {
      if (activePos == null && collisionGraceTicks > 0) {
         collisionGraceTicks--;
      }

      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || minecraft.level == null || player.isSpectator()) { stopGrab(); return; }
      if (!RagdollSettings.grabEnabled()) { stopGrab(); return; }
      if (player.isPassenger()) { stopGrab(); return; }
      if (!minecraft.options.keyUse.isDown()) { stopGrab(); return; }
      if (!player.getMainHandItem().isEmpty()) { stopGrab(); return; }

      if (activePos != null) {
         player.setSprinting(false);
         if (!minecraft.gameRenderer.getMainCamera().isDetached()) {
            player.swingTime = 0;
            player.swinging = true;
            player.swingingArm = InteractionHand.MAIN_HAND;
         }
         return;
      }

      BlockPos partPos = targetedPart(minecraft);
      if (partPos == null) return;

      activePos = partPos;
      RagdollNetworking.CHANNEL.sendToServer(new RagdollGrabPacket(partPos, false));
      player.swing(InteractionHand.MAIN_HAND);
   }

   private static void onScroll(InputEvent.MouseScrollingEvent event) {
      if (activePos != null) event.setCanceled(true);
   }

   private static void stopGrab() {
      if (activePos != null) {
         RagdollNetworking.CHANNEL.sendToServer(new RagdollGrabPacket(activePos, true));
         activePos = null;
         collisionGraceTicks = COLLISION_GRACE_TICKS;
      }
   }

   public static void clearActive() {
      activePos = null;
      collisionGraceTicks = COLLISION_GRACE_TICKS;
   }

   @Nullable
   private static BlockPos targetedPart(Minecraft minecraft) {
      if (!(minecraft.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS || minecraft.level == null) return null;
      BlockPos pos = blockHit.getBlockPos();
      BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
      return blockEntity instanceof RagdollPartBlockEntity || blockEntity instanceof MobRagdollPartBlockEntity ? pos : null;
   }
}
