package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.api.RagdollAsyncPoseRequests;
import dev.leo.sableplayerragdoll.api.RagdollLimbConfig;
import dev.leo.sableplayerragdoll.api.RagdollLimbOptions;
import dev.leo.sableplayerragdoll.api.RagdollPoseSnapshot;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity.BodyPart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record RagdollPoseResponsePacket(long requestId, RagdollLimbOptions pose, float bodyYaw) {
   public static void handle(RagdollPoseResponsePacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         ServerPlayer player = networkContext.getSender();
         if (player != null) {
            RagdollAsyncPoseRequests.resolve(player, packet.requestId(), new RagdollPoseSnapshot(packet.pose(), packet.bodyYaw()));
         }
      });

      networkContext.setPacketHandled(true);
   }

   public static void encode(FriendlyByteBuf buffer, RagdollPoseResponsePacket packet) {
      buffer.writeLong(packet.requestId());
      buffer.writeFloat(packet.bodyYaw());
      for (BodyPart part : BodyPart.values()) {
         RagdollLimbConfig config = packet.pose().get(part);
         buffer.writeBoolean(config != null);
         if (config != null) {
            buffer.writeDouble(config.rightOffset().orElse(0.0));
            buffer.writeDouble(config.upOffset().orElse(0.0));
            buffer.writeDouble(config.forwardOffset().orElse(0.0));
            buffer.writeDouble(config.initialPitchDegrees().orElse(0.0));
            buffer.writeDouble(config.initialYawDegrees().orElse(0.0));
            buffer.writeDouble(config.initialRollDegrees().orElse(0.0));
         }
      }
   }

   public static RagdollPoseResponsePacket decode(FriendlyByteBuf buffer) {
      long requestId = buffer.readLong();
      float bodyYaw = buffer.readFloat();
      RagdollLimbOptions.Builder builder = RagdollLimbOptions.builder();
      for (BodyPart part : BodyPart.values()) {
         if (buffer.readBoolean()) {
            builder.limb(part, RagdollLimbConfig.builder()
               .offset(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
               .initialRotation(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
         }
      }
      return new RagdollPoseResponsePacket(requestId, builder.build(), bodyYaw);
   }
}
