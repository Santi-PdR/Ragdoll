package dev.leo.sableplayerragdoll.mob.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.mob.MobRagdollAssembly;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record MobRagdollDespawnPacket(int entityId) {
         public static void encode(FriendlyByteBuf buffer, MobRagdollDespawnPacket packet) {
        buffer.writeVarInt(packet.entityId());
    }

    public static MobRagdollDespawnPacket decode(FriendlyByteBuf buffer) {
        return new MobRagdollDespawnPacket(buffer.readVarInt());
    }

    public static void handle(MobRagdollDespawnPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
        networkContext.enqueueWork(() -> {
            if (!(networkContext.getSender() instanceof ServerPlayer player)) {
                return;
            }
            Entity target = player.level().getEntity(packet.entityId());
            if (!(target instanceof LivingEntity livingEntity) || target == player) {
                return;
            }
            MobRagdollAssembly.despawn(player.serverLevel(), livingEntity);
        });
      networkContext.setPacketHandled(true);
    }
}
