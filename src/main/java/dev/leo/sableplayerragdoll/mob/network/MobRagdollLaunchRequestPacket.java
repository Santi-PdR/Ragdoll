package dev.leo.sableplayerragdoll.mob.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.mob.client.MobRagdollClientExtractor;

public record MobRagdollLaunchRequestPacket(int entityId) {
         public static void encode(FriendlyByteBuf buffer, MobRagdollLaunchRequestPacket packet) {
        buffer.writeVarInt(packet.entityId());
    }

    public static MobRagdollLaunchRequestPacket decode(FriendlyByteBuf buffer) {
        return new MobRagdollLaunchRequestPacket(buffer.readVarInt());
    }

    public static void handle(MobRagdollLaunchRequestPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
        networkContext.enqueueWork(() -> MobRagdollClientExtractor.extractAndSend(packet.entityId()));
        networkContext.setPacketHandled(true);
    }
}
