package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.api.RagdollLimbConfig;
import dev.leo.sableplayerragdoll.api.RagdollLimbOptions;
import dev.leo.sableplayerragdoll.api.RagdollPoseSnapshot;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity.BodyPart;
import dev.leo.sableplayerragdoll.physics.RagdollRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record RagdollTriggerPacket(RagdollLimbOptions pose, float bodyYaw) {
   public RagdollTriggerPacket() {
      this(RagdollLimbOptions.defaults(), Float.NaN);
   }

   public static void handle(RagdollTriggerPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
      networkContext.enqueueWork(() -> {
         if (networkContext.getSender() instanceof ServerPlayer player) {
            RagdollRegistry.triggerManual(player, new RagdollPoseSnapshot(packet.pose(), packet.bodyYaw()));
         }
      });

      networkContext.setPacketHandled(true);
   }

   private static void write(FriendlyByteBuf buffer, RagdollTriggerPacket packet) {
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

   private static RagdollTriggerPacket read(FriendlyByteBuf buffer) {
      float bodyYaw = buffer.readFloat();
      RagdollLimbOptions.Builder builder = RagdollLimbOptions.builder();
      for (BodyPart part : BodyPart.values()) {
         if (buffer.readBoolean()) {
            builder.limb(part, RagdollLimbConfig.builder()
               .offset(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
               .initialRotation(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
         }
      }
      return new RagdollTriggerPacket(builder.build(), bodyYaw);
   }
}
