package dev.santi.ragdoll;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.world.entity.LivingEntity;

public final class RagdollNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(RagdollMod.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();
    private static boolean registered;

    private RagdollNetwork() {}

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.messageBuilder(RagdollStatePacket.class, 0)
                .encoder(RagdollStatePacket::encode)
                .decoder(RagdollStatePacket::decode)
                .consumerMainThread(RagdollStatePacket::handle)
                .add();
    }

    public static void sync(LivingEntity entity, float angle, int ticks, boolean active) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                new RagdollStatePacket(entity.getId(), angle, ticks, active));
    }
}
