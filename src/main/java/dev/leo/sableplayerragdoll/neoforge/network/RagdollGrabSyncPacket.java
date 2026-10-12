package dev.leo.sableplayerragdoll.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;


import dev.leo.sableplayerragdoll.neoforge.client.RagdollGrabClient;
import dev.leo.sableplayerragdoll.neoforge.client.RagdollGrabState;
import net.minecraft.client.Minecraft;
import java.util.UUID;

public record RagdollGrabSyncPacket(UUID playerId, boolean grabbing) {
   public static void encode(FriendlyByteBuf b, RagdollGrabSyncPacket p) { b.writeUUID(p.playerId()); b.writeBoolean(p.grabbing()); }
   public static RagdollGrabSyncPacket decode(FriendlyByteBuf b) { return new RagdollGrabSyncPacket(b.readUUID(), b.readBoolean()); }

   public static void handle(RagdollGrabSyncPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         if (packet.grabbing()) {
            RagdollGrabState.add(packet.playerId());
         } else {
            RagdollGrabState.remove(packet.playerId());
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(packet.playerId())) {
               RagdollGrabClient.clearActive();
            }
         }
      });
      networkContext.setPacketHandled(true);

      networkContext.setPacketHandled(true);
   }
}
