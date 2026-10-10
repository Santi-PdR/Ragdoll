package dev.leo.ragdollreactions.neoforge.network;

import dev.leo.ragdollreactions.RagdollReactions;
import dev.leo.ragdollreactions.physics.ClientMotionTelemetry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ClientMotionPacket(float horizontalAccelMetersPerSecond, float horizontalSpeedMetersPerSecond) {
   public static void encode(FriendlyByteBuf b, ClientMotionPacket p) { b.writeFloat(p.horizontalAccelMetersPerSecond()); b.writeFloat(p.horizontalSpeedMetersPerSecond()); }
   public static ClientMotionPacket decode(FriendlyByteBuf b) { return new ClientMotionPacket(b.readFloat(), b.readFloat()); }

   public static void handle(ClientMotionPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         ServerPlayer player = networkContext.getSender();
         if (player != null) {
            ClientMotionTelemetry.update(player, packet.horizontalAccelMetersPerSecond(), packet.horizontalSpeedMetersPerSecond());
         }
      });

      networkContext.setPacketHandled(true);
   }
}
