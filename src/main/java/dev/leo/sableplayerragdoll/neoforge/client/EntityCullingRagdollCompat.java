package dev.leo.sableplayerragdoll.neoforge.client;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.entity.RagdollDollEntity;
import dev.leo.sableplayerragdoll.entity.RagdollSeatEntity;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.mob.block.entity.MobRagdollPartBlockEntity;
import java.lang.reflect.Method;
import java.util.function.Function;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.common.MinecraftForge;

/**
 * Keeps this mod optional while adapting its rendered parts to EntityCulling.
 */
public final class EntityCullingRagdollCompat {
   private static boolean registered;
   private static boolean warned;

   private EntityCullingRagdollCompat() {
   }

   public static void init() {
      MinecraftForge.EVENT_BUS.addListener(EntityCullingRagdollCompat::onClientTick);
   }

   private static void onClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase != TickEvent.Phase.END || registered) {
         return;
      }

      if (!ModList.get().isLoaded("entityculling")) {
         registered = true;
         return;
      }

      try {
         Class<?> api = Class.forName("dev.tr7zw.entityculling.EntityCullingModBase");
         Object instance = api.getField("instance").get(null);
         if (instance == null) {
            return;
         }

         Method addEntities = api.getMethod("addDynamicEntityWhitelist", Function.class);
         Method addBlockEntities = api.getMethod("addDynamicBlockEntityWhitelist", Function.class);
         Function<Object, Boolean> ragdollEntities =
               entity -> entity instanceof RagdollDollEntity || entity instanceof RagdollSeatEntity;
         Function<Object, Boolean> ragdollParts =
               blockEntity -> blockEntity instanceof RagdollPartBlockEntity
                     || blockEntity instanceof MobRagdollPartBlockEntity;

         addEntities.invoke(instance, ragdollEntities);
         addBlockEntities.invoke(instance, ragdollParts);
         registered = true;
         SablePlayerRagdoll.LOGGER.info("Registered ragdoll entities and parts with EntityCulling's dynamic whitelist");
      } catch (ReflectiveOperationException | LinkageError exception) {
         registered = true;
         if (!warned) {
            warned = true;
            SablePlayerRagdoll.LOGGER.warn("EntityCulling is installed, but its dynamic whitelist API was unavailable; ragdolls may be culled", exception);
         }
      }
   }
}
