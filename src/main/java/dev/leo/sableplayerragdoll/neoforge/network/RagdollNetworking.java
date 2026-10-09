package dev.leo.sableplayerragdoll.neoforge.network;

import dev.leo.sableplayerragdoll.mob.network.MobRagdollDespawnPacket;
import dev.leo.sableplayerragdoll.mob.network.MobRagdollLaunchRequestPacket;
import dev.leo.sableplayerragdoll.mob.network.MobRagdollSpawnPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;

public final class RagdollNetworking {
   public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("sable_player_ragdoll", "main"), () -> "1", "1"::equals, "1"::equals);
   private static boolean registered;
   private static final ResourceLocation GRAB_SLOWDOWN_ID = new ResourceLocation("sable_player_ragdoll", "grab_slowdown");
   private RagdollNetworking() {}
   public static synchronized void register() {
      if (registered) return;
      registered=true;
      CHANNEL.registerMessage(0,RagdollGrabSyncPacket.class,(p,b)->RagdollGrabSyncPacket.encode(b,p),RagdollGrabSyncPacket::decode,RagdollGrabSyncPacket::handle,Optional.of(NetworkDirection.PLAY_TO_CLIENT));
      CHANNEL.registerMessage(1,RagdollRequestPosePacket.class,(p,b)->RagdollRequestPosePacket.encode(b,p),RagdollRequestPosePacket::decode,RagdollRequestPosePacket::handle,Optional.of(NetworkDirection.PLAY_TO_CLIENT));
      CHANNEL.registerMessage(2,RagdollTriggerPacket.class,(p,b)->RagdollTriggerPacket.encode(b,p),RagdollTriggerPacket::decode,RagdollTriggerPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(3,RagdollGrabPacket.class,(p,b)->RagdollGrabPacket.encode(b,p),RagdollGrabPacket::decode,RagdollGrabPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(4,RagdollInputPacket.class,(p,b)->RagdollInputPacket.encode(b,p),RagdollInputPacket::decode,RagdollInputPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(5,RagdollPoseResponsePacket.class,(p,b)->RagdollPoseResponsePacket.encode(b,p),RagdollPoseResponsePacket::decode,RagdollPoseResponsePacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(6,MobRagdollSpawnPacket.class,(p,b)->MobRagdollSpawnPacket.encode(b,p),MobRagdollSpawnPacket::decode,MobRagdollSpawnPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(7,MobRagdollDespawnPacket.class,(p,b)->MobRagdollDespawnPacket.encode(b,p),MobRagdollDespawnPacket::decode,MobRagdollDespawnPacket::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(8,MobRagdollLaunchRequestPacket.class,(p,b)->MobRagdollLaunchRequestPacket.encode(b,p),MobRagdollLaunchRequestPacket::decode,MobRagdollLaunchRequestPacket::handle,Optional.of(NetworkDirection.PLAY_TO_CLIENT));
   }
   public static void notifyReleased(ServerPlayer player) { player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(player.getId(),player.getDeltaMovement())); }
   public static void notifyRequestPose(ServerPlayer player,long requestId) { CHANNEL.send(PacketDistributor.PLAYER.with(()->player),new RagdollRequestPosePacket(requestId)); }
   public static void notifyGrabStarted(ServerPlayer player) { applySlowdown(player); CHANNEL.send(PacketDistributor.ALL.noArg(),new RagdollGrabSyncPacket(player.getUUID(),true)); }
   public static void notifyGrabEnded(ServerPlayer player) { removeSlowdown(player); CHANNEL.send(PacketDistributor.ALL.noArg(),new RagdollGrabSyncPacket(player.getUUID(),false)); }
   private static void applySlowdown(ServerPlayer player) {
      AttributeInstance attr=player.getAttribute(Attributes.MOVEMENT_SPEED); if(attr==null)return;
      attr.removeModifier(GRAB_SLOWDOWN_ID); attr.addTransientModifier(new AttributeModifier(GRAB_SLOWDOWN_ID,-0.5,AttributeModifier.Operation.MULTIPLY_TOTAL));
   }
   private static void removeSlowdown(ServerPlayer player) { AttributeInstance attr=player.getAttribute(Attributes.MOVEMENT_SPEED); if(attr!=null)attr.removeModifier(GRAB_SLOWDOWN_ID); }
}
