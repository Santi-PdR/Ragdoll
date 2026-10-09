package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.neoforge.client.RagdollClientPoseCapture;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.PacketDistributor;

public record RagdollRequestPosePacket(long requestId) {
   public static void encode(FriendlyByteBuf b, RagdollRequestPosePacket p) { b.writeLong(p.requestId()); }
   public static RagdollRequestPosePacket decode(FriendlyByteBuf b) { return new RagdollRequestPosePacket(b.readLong()); }

   public static void handle(RagdollRequestPosePacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         if (Minecraft.getInstance().player == null) return;
         dev.leo.sableplayerragdoll.neoforge.network.RagdollNetworking.CHANNEL.sendToServer(new RagdollPoseResponsePacket(
            packet.requestId(),
            RagdollClientPoseCapture.capture(),
            Minecraft.getInstance().player.yBodyRot
         ));
      });
      networkContext.setPacketHandled(true);

      networkContext.setPacketHandled(true);
   }
}
