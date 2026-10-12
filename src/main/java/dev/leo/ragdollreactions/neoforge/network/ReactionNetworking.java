package dev.leo.ragdollreactions.neoforge.network;

import dev.leo.ragdollreactions.RagdollReactions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;

public final class ReactionNetworking {
   public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(RagdollReactions.MOD_ID, "main"), () -> "1", "1"::equals, "1"::equals);
   private static boolean registered;
   private ReactionNetworking() {}
   public static synchronized void register() {
      if(registered)return; registered=true;
      CHANNEL.registerMessage(0,ClientMotionPacket.class,(p,b)->ClientMotionPacket.encode(b,p),ClientMotionPacket::decode,ClientMotionPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
   }
}
