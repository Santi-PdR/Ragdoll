package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.RagdollGrabCallbacks;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.config.RagdollSettings;
import dev.leo.sableplayerragdoll.mob.block.entity.MobRagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.physics.RagdollRegistry;
import dev.leo.sableplayerragdoll.physics.RagdollSessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record RagdollGrabPacket(BlockPos pos, boolean release) {
   public static void encode(FriendlyByteBuf b, RagdollGrabPacket p) { b.writeBlockPos(p.pos()); b.writeBoolean(p.release()); }
   public static RagdollGrabPacket decode(FriendlyByteBuf b) { return new RagdollGrabPacket(b.readBlockPos(), b.readBoolean()); }

   public static void handle(RagdollGrabPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         ServerPlayer player = networkContext.getSender();
         if (player == null) return;
         BlockEntity blockEntity = player.level().getBlockEntity(packet.pos());
         if (blockEntity instanceof RagdollPartBlockEntity ragdollPart) {
            if (packet.release()) {
               ragdollPart.stopGrab(player.getUUID());
               RagdollGrabCallbacks.notifyReleased(player);
            } else {
               if (!RagdollSettings.grabEnabled()) return;
               if (RagdollSessionManager.activeRagdollForPlayer(player.serverLevel(), player.getUUID()) != null) return;
               if (RagdollRegistry.isGrabDisabledAt(player.serverLevel(), packet.pos())) return;
               ragdollPart.startGrab(player.getUUID());
               RagdollGrabCallbacks.notifyGrabbed(player);
            }
         } else if (blockEntity instanceof MobRagdollPartBlockEntity mobRagdollPart) {
            if (packet.release()) {
               mobRagdollPart.stopGrab(player.getUUID());
               RagdollGrabCallbacks.notifyReleased(player);
            } else {
               if (!RagdollSettings.grabEnabled()) return;
               if (RagdollSessionManager.activeRagdollForPlayer(player.serverLevel(), player.getUUID()) != null) return;
               if (RagdollRegistry.isGrabDisabledAt(player.serverLevel(), packet.pos())) return;
               mobRagdollPart.startGrab(player.getUUID());
               RagdollGrabCallbacks.notifyGrabbed(player);
            }
         }
      });

      networkContext.setPacketHandled(true);
   }
}
