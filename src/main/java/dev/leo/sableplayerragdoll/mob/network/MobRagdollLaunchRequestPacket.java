package dev.leo.sableplayerragdoll.mob.network;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.mob.client.MobRagdollClientExtractor;
import net.minecraft.network.FriendlyByteBuf;

public record MobRagdollLaunchRequestPacket(int entityId) {
    public static final Type<MobRagdollLaunchRequestPacket> TYPE = new Type<>(
            new ResourceLocation(SablePlayerRagdoll.MOD_ID, "mob_ragdoll_launch_request")
    );
     @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buffer, MobRagdollLaunchRequestPacket packet) {
        buffer.writeVarInt(packet.entityId());
    }

    public static MobRagdollLaunchRequestPacket decode(FriendlyByteBuf buffer) {
        return new MobRagdollLaunchRequestPacket(buffer.readVarInt());
    }

    public static void handle(MobRagdollLaunchRequestPacket packet, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> context) {
      net.minecraftforge.network.NetworkEvent.Context networkContext = context.get();
        networkContext.enqueueWork(() -> MobRagdollClientExtractor.extractAndSend(packet.entityId()));
    }
}
