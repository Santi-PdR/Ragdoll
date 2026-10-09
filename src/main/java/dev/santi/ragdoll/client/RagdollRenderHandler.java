package dev.santi.ragdoll.client;

import com.mojang.math.Axis;
import dev.santi.ragdoll.RagdollMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RagdollMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RagdollRenderHandler {
    private RagdollRenderHandler() {}

    @SubscribeEvent
    public static void beforeRender(RenderLivingEvent.Pre<?, ?> event) {
        float angle = ClientRagdollState.angle(event.getEntity().getId());
        if (angle == 0.0f) return;
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(0.0, event.getEntity().getBbHeight() * 0.5, 0.0);
        event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(angle));
        event.getPoseStack().translate(0.0, -event.getEntity().getBbHeight() * 0.5, 0.0);
    }

    @SubscribeEvent
    public static void afterRender(RenderLivingEvent.Post<?, ?> event) {
        if (ClientRagdollState.angle(event.getEntity().getId()) != 0.0f) {
            event.getPoseStack().popPose();
        }
    }
}
