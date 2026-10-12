package dev.leo.sableplayerragdoll.neoforge;

import dev.leo.sableplayerragdoll.block.RagdollPartBlock;
import dev.leo.sableplayerragdoll.block.RagdollSeatBlock;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.entity.RagdollDollEntity;
import dev.leo.sableplayerragdoll.entity.RagdollSeatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RagdollBlockRegistration {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "sable_player_ragdoll");
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "sable_player_ragdoll");
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "sable_player_ragdoll");
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "sable_player_ragdoll");

   public static final RegistryObject<RagdollSeatBlock> RAGDOLL_SEAT = BLOCKS.register(
      "ragdoll_seat", () -> new RagdollSeatBlock(Properties.of().mapColor(MapColor.NONE).strength(2.0F).noOcclusion().noLootTable())
   );
   public static final RegistryObject<RagdollPartBlock> RAGDOLL_PART = BLOCKS.register(
      "ragdoll_part", () -> new RagdollPartBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).strength(-1.0F, 3600000.0F).sound(SoundType.EMPTY).noOcclusion().noLootTable())
   );
   public static final RegistryObject<BlockEntityType<RagdollPartBlockEntity>> RAGDOLL_PART_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
      "ragdoll_part", () -> BlockEntityType.Builder.of(RagdollPartBlockEntity::new, RAGDOLL_PART.get()).build(null)
   );
   public static final RegistryObject<EntityType<RagdollSeatEntity>> RAGDOLL_SEAT_ENTITY = ENTITY_TYPES.register(
      "ragdoll_seat",
      () -> Builder.<RagdollSeatEntity>of(RagdollSeatEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.35F)
            
            .setShouldReceiveVelocityUpdates(false)
            .build("sable_player_ragdoll:ragdoll_seat")
   );
   public static final RegistryObject<EntityType<RagdollDollEntity>> RAGDOLL_DOLL_ENTITY = ENTITY_TYPES.register(
      "ragdoll_doll",
      () -> Builder.<RagdollDollEntity>of(RagdollDollEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.8F)
            .clientTrackingRange(10)
            .updateInterval(3)
            .build("sable_player_ragdoll:ragdoll_doll")
   );
   public static final RegistryObject<SoundEvent> RAGDOLL_IMPACT_SOUND = SOUND_EVENTS.register(
      "ragdoll_impact",
      () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("sable_player_ragdoll", "ragdoll_impact"))
   );
   public static final RegistryObject<SoundEvent> RAGDOLL_SMALL_IMPACT_SOUND = SOUND_EVENTS.register(
      "ragdoll_small_impact",
      () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("sable_player_ragdoll", "ragdoll_small_impact"))
   );

   private RagdollBlockRegistration() {
   }

   public static void register(IEventBus modBus) {
      BLOCKS.register(modBus);
      ENTITY_TYPES.register(modBus);
      BLOCK_ENTITY_TYPES.register(modBus);
      SOUND_EVENTS.register(modBus);
   }
}
