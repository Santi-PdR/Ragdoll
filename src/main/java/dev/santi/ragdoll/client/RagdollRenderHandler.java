package dev.santi.ragdoll.client;

import com.mojang.math.Axis;
import dev.santi.ragdoll.RagdollMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = RagdollMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RagdollRenderHandler {
    private static final Set<Integer> POSE_STACKS = ConcurrentHashMap.newKeySet();

    private RagdollRenderHandler() {}

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!ClientRagdollState.isActive(event.getEntity().getId())) return;
        event.getInput().forwardImpulse = 0.0f;
        event.getInput().leftImpulse = 0.0f;
        event.getInput().jumping = false;
        event.getInput().shiftKeyDown = false;
    }

    @SubscribeEvent
    public static void beforeRender(RenderLivingEvent.Pre<?, ?> event) {
        int id = event.getEntity().getId();
        float angle = ClientRagdollState.angle(id);
        if (!ClientRagdollState.isActive(id) || angle == 0.0f) return;
        event.getPoseStack().pushPose();
        POSE_STACKS.add(id);
        event.getPoseStack().translate(0.0, event.getEntity().getBbHeight() * 0.5, 0.0);
        event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(angle));
        event.getPoseStack().translate(0.0, -event.getEntity().getBbHeight() * 0.5, 0.0);
    }

    @SubscribeEvent
    public static void afterRender(RenderLivingEvent.Post<?, ?> event) {
        if (POSE_STACKS.remove(event.getEntity().getId())) {
            event.getPoseStack().popPose();
        }
    }
}
