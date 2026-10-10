package dev.leo.sableplayerragdoll.mob;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.mob.block.MobRagdollPartBlock;
import dev.leo.sableplayerragdoll.mob.block.entity.MobRagdollPartBlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MobRagdollBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SablePlayerRagdoll.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SablePlayerRagdoll.MOD_ID);

    public static final RegistryObject<MobRagdollPartBlock> MOB_RAGDOLL_PART = BLOCKS.register(
            "mob_ragdoll_part",
            () -> new MobRagdollPartBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(-1.0F, 0.0F)
                    .sound(SoundType.EMPTY)
                    .noOcclusion()
                    .noLootTable()
                    )
    );
    public static final RegistryObject<BlockEntityType<MobRagdollPartBlockEntity>> MOB_RAGDOLL_PART_ENTITY = BLOCK_ENTITY_TYPES.register(
            "mob_ragdoll_part",
            () -> BlockEntityType.Builder.of(MobRagdollPartBlockEntity::new, MOB_RAGDOLL_PART.get()).build(null)
    );

    private MobRagdollBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
