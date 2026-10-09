package dev.santi.ragdoll;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RagdollStatePacket(int entityId, float angle, int ticks, boolean active) {
    public static void encode(RagdollStatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeFloat(packet.angle);
        buffer.writeVarInt(packet.ticks);
        buffer.writeBoolean(packet.active);
    }

    public static RagdollStatePacket decode(FriendlyByteBuf buffer) {
        return new RagdollStatePacket(buffer.readVarInt(), buffer.readFloat(),
                buffer.readVarInt(), buffer.readBoolean());
    }

    public static void handle(RagdollStatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> dev.santi.ragdoll.client.ClientRagdollState.accept(packet)));
        context.setPacketHandled(true);
    }
}
