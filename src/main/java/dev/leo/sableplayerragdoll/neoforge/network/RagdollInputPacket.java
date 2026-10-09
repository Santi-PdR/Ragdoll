package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.physics.RagdollControlHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record RagdollInputPacket(float strafe, float forward) {
   public static void encode(FriendlyByteBuf b, RagdollInputPacket p) { b.writeFloat(p.strafe()); b.writeFloat(p.forward()); }
   public static RagdollInputPacket decode(FriendlyByteBuf b) { return new RagdollInputPacket(b.readFloat(), b.readFloat()); }

   public static void handle(RagdollInputPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         ServerPlayer player = networkContext.getSender();
         if (player != null) {
            RagdollControlHelper.updateInput(player, packet.strafe(), packet.forward());
         }
      });

      networkContext.setPacketHandled(true);
   }
}
