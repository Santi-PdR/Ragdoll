package dev.leo.sableplayerragdoll.physics;

import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
final class RagdollAccessoriesEquipmentHelper {
 private RagdollAccessoriesEquipmentHelper() {}
 static void applyToPart(RagdollPartBlockEntity part, Player player) {}
 static void applyFrom(ServerLevel level, UUID rootId, Player player) {}
 static Map<String,List<ItemStack>> capture(Player player) { return Map.of(); }
 static long accessoriesSignature(Player player) { return 0L; }
}
