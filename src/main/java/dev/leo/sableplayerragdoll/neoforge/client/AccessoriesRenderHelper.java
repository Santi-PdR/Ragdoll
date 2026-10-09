package dev.leo.sableplayerragdoll.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity.BodyPart;
import dev.leo.sableplayerragdoll.entity.RagdollDollEntity;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class AccessoriesRenderHelper {
    private AccessoriesRenderHelper() {}
    @Nullable static ItemStack storedCosmeticArmorOverride(RagdollPartBlockEntity entity, EquipmentSlot slot) { return null; }
    static void renderFromStored(BodyPart part, Map<String, List<ItemStack>> items, LivingEntity entity,
        RenderLayerParent<RagdollDollEntity, PlayerModel<RagdollDollEntity>> parent, PoseStack pose,
        MultiBufferSource buffer, int light, float partialTick) {}
    static void render(BodyPart part, LivingEntity entity,
        RenderLayerParent<RagdollDollEntity, PlayerModel<RagdollDollEntity>> parent, PoseStack pose,
        MultiBufferSource buffer, int light, float partialTick) {}
}
