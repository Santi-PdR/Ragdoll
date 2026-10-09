package dev.leo.ragdollreactions.neoforge;

import dev.leo.ragdollreactions.RagdollReactionsBootstrap;
import dev.leo.ragdollreactions.neoforge.config.ReactionConfig;
import dev.leo.ragdollreactions.neoforge.network.ReactionNetworking;
import dev.leo.ragdollreactions.physics.CrashReactionHandler;
import dev.leo.ragdollreactions.physics.ExplosionReactionHandler;
import dev.leo.ragdollreactions.physics.FallReactionHandler;
import dev.leo.ragdollreactions.physics.HitReactionHandler;
import dev.leo.ragdollreactions.physics.ImpactReactionHandler;
import dev.leo.ragdollreactions.physics.LightningReactionHandler;
import dev.leo.ragdollreactions.physics.MobDamageReactionHandler;
import dev.leo.ragdollreactions.physics.MobExplosionReactionHandler;
import dev.leo.ragdollreactions.physics.MobFallReactionHandler;
import dev.leo.ragdollreactions.physics.MobImpactReactionHandler;
import dev.leo.ragdollreactions.physics.ReactionLauncher;
import dev.leo.ragdollreactions.physics.ReactionMobLauncher;
import dev.leo.ragdollreactions.sound.ReactionSounds;
import dev.leo.sableplayerragdoll.api.RagdollEndEvent;
import dev.leo.sableplayerragdoll.mob.api.MobRagdollEndEvent;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;

@Mod("ragdoll_reactions")
public final class RagdollReactionsNeoForge {
   private static final String CREATE_BIG_CANNONS_PACKAGE = "rbasamoyai.createbigcannons.";

   public RagdollReactionsNeoForge() {
      IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
      ModContainer modContainer = net.minecraftforge.fml.ModList.get().getModContainerById("ragdoll_reactions").orElseThrow();
      DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> new RagdollReactionsNeoForgeClient(modContainer));
      modBus.addListener(ReactionConfig::onLoad);
      modBus.addListener(ReactionConfig::onReload);
      ReactionNetworking.register();
      ReactionConfig.register(modContainer);
      ReactionSounds.register(modBus);
      modBus.addListener(RagdollReactionsNeoForge::onCommonSetup);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onRagdollEnd);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onMobRagdollEnd);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onExplosionDetonate);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onLivingDamagePre);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onLivingDamagePost);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onEntityStruckByLightning);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onEntityTeleport);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onPlayerChangedDimension);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onPlayerRespawn);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onPlayerLoggedOut);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onEntityTickPost);
      MinecraftForge.EVENT_BUS.addListener(RagdollReactionsNeoForge::onServerStopped);
   }

   private static void onCommonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(RagdollReactionsBootstrap::init);
   }

   private static void onRagdollEnd(RagdollEndEvent event) {
      ReactionLauncher.onPlayerReleased(event.player());
      ImpactReactionHandler.onPlayerReleased(event.player());
   }

   private static void onMobRagdollEnd(MobRagdollEndEvent event) {
      if (event.entity().level() instanceof ServerLevel level) {
         ReactionMobLauncher.onMobReleased(event.entity(), level);
      }
   }

   private static void onExplosionDetonate(ExplosionEvent.Detonate event) {
      if (!(event.getLevel() instanceof ServerLevel level)) {
         return;
      }

      Explosion explosion = event.getExplosion();
      if (isCreateBigCannonsExplosion(explosion)) {
         ExplosionReactionHandler.onCannonExplosion(level, explosion.getPosition(), explosionPower(explosion), entityRadius(explosion));
      } else {
         ExplosionReactionHandler.onVanillaExplosion(level, explosion);
      }
      MobExplosionReactionHandler.onExplosion(level, explosion.getPosition(), explosionPower(explosion));
   }

   private static void onLivingDamagePre(LivingHurtEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         CrashReactionHandler.onPlayerDamaged(player, event.getSource(), event.getAmount(), event.getAmount());
         FallReactionHandler.onPlayerDamaged(player, event.getSource(), event.getAmount());
      } else if (event.getEntity() instanceof Mob mob) {
         MobFallReactionHandler.onMobDamaged(mob, event.getSource(), event.getAmount());
      }
   }

   private static void onLivingDamagePost(LivingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         HitReactionHandler.onPlayerDamaged(player, event.getSource(), event.getAmount());
      } else if (event.getEntity() instanceof Mob mob) {
         MobDamageReactionHandler.onMobDamaged(mob, event.getSource(), event.getAmount());
      }
   }

   private static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         LightningReactionHandler.onLightningStrike(player);
      }
   }

   private static void onEntityTeleport(EntityTeleportEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ImpactReactionHandler.onPlayerDisplaced(player);
      }
   }

   private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ImpactReactionHandler.onPlayerDisplaced(player);
      }
   }

   private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ImpactReactionHandler.onPlayerDisplaced(player);
      }
   }

   private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ImpactReactionHandler.onPlayerLoggedOut(player);
         ReactionLauncher.onPlayerLoggedOut(player);
      }
   }

   private static boolean isCreateBigCannonsExplosion(Explosion explosion) {
      return explosion.getClass().getName().startsWith(CREATE_BIG_CANNONS_PACKAGE);
   }

   public static double explosionPower(Explosion explosion) {
      try {
         java.lang.reflect.Field field = Explosion.class.getDeclaredField("radius");
         field.setAccessible(true);
         return ((Number) field.get(explosion)).doubleValue();
      } catch (ReflectiveOperationException ignored) {
         return 4.0;
      }
   }

   private static double entityRadius(Explosion explosion) {
      try {
         Object value = explosion.getClass().getMethod("getEntityRadius").invoke(explosion);
         if (value instanceof Number number) {
            return number.doubleValue();
         }
      } catch (ReflectiveOperationException ignored) {
      }
      return explosionPower(explosion);
   }

   private static void onEntityTickPost(LivingTickEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         MobImpactReactionHandler.onMobTick(mob);
      }
   }

   private static void onServerStopped(ServerStoppedEvent event) {
      ReactionLauncher.resetState();
      ReactionMobLauncher.resetState();
      ImpactReactionHandler.resetState();
   }
}
