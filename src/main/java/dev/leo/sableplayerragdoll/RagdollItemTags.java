package dev.leo.sableplayerragdoll;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class RagdollItemTags {
   private static final String TEST_MARKER_KEY = "sable_player_ragdoll_ragdoll_on_hit";
   private static final String TEST_MARKER_CRIT_KEY = "sable_player_ragdoll_critical_only";
   public static final TagKey<Item> RAGDOLL_ON_HIT = TagKey.create(Registries.ITEM, new ResourceLocation(SablePlayerRagdoll.MOD_ID, "ragdoll_on_hit"));
   public static final TagKey<Item> RAGDOLL_ON_CRITICAL_HIT = TagKey.create(Registries.ITEM, new ResourceLocation(SablePlayerRagdoll.MOD_ID, "ragdoll_on_critical_hit"));
   private RagdollItemTags() {}
   public static boolean canRagdollOnHit(ItemStack stack) { return stack.is(RAGDOLL_ON_HIT) || stack.is(RAGDOLL_ON_CRITICAL_HIT) || hasMarker(stack, TEST_MARKER_KEY); }
   public static boolean requiresCriticalHit(ItemStack stack) { return stack.is(RAGDOLL_ON_CRITICAL_HIT) || hasMarker(stack, TEST_MARKER_CRIT_KEY); }
   public static void markTestItem(ItemStack stack) { stack.getOrCreateTag().putBoolean(TEST_MARKER_KEY, true); }
   public static void markTestItemCritOnly(ItemStack stack) { CompoundTag tag=stack.getOrCreateTag(); tag.putBoolean(TEST_MARKER_KEY,true); tag.putBoolean(TEST_MARKER_CRIT_KEY,true); }
   private static boolean hasMarker(ItemStack stack,String key) { CompoundTag tag=stack.getTag(); return tag!=null && tag.getBoolean(key); }
}
