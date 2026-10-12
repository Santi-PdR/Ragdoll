package dev.leo.sableplayerragdoll.mob.item;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MobRagdollItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SablePlayerRagdoll.MOD_ID);

    public static final RegistryObject<MobRagdollDebugItem> MOB_RAGDOLL_DEBUG_STICK =
            ITEMS.register("mob_ragdoll_debug_stick", () -> new MobRagdollDebugItem(new Item.Properties().stacksTo(1)));

    private MobRagdollItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
