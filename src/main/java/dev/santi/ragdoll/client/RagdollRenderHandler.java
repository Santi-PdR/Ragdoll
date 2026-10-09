package dev.santi.ragdoll.client;

import com.mojang.math.Axis;
import dev.santi.ragdoll.RagdollMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = RagdollMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RagdollRenderHandler {
    private static final Set<Integer> POSE_STACKS = ConcurrentHashMap.newKeySet();
    private static final Map<Integer, HumanoidPose> POSES = new ConcurrentHashMap<>();

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
        if (!ClientRagdollState.isActive(id)) return;

        if (event.getRenderer().getModel() instanceof HumanoidModel<?> model) {
            POSES.put(id, HumanoidPose.capture(model));
            float phase = (float) Math.toRadians(angle);
            float flail = (float) Math.sin(phase);
            model.rightArm.xRot += 0.8f + flail * 0.7f;
            model.leftArm.xRot -= 0.8f + flail * 0.7f;
            model.rightLeg.xRot -= 0.35f + flail * 0.45f;
            model.leftLeg.xRot += 0.35f + flail * 0.45f;
            model.body.zRot += flail * 0.18f;
        }

        if (angle != 0.0f) {
            event.getPoseStack().pushPose();
            POSE_STACKS.add(id);
            event.getPoseStack().translate(0.0, event.getEntity().getBbHeight() * 0.5, 0.0);
            event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(angle));
            event.getPoseStack().translate(0.0, -event.getEntity().getBbHeight() * 0.5, 0.0);
        }
    }

    @SubscribeEvent
    public static void afterRender(RenderLivingEvent.Post<?, ?> event) {
        int id = event.getEntity().getId();
        HumanoidPose pose = POSES.remove(id);
        if (pose != null && event.getRenderer().getModel() instanceof HumanoidModel<?> model) {
            pose.restore(model);
        }
        if (POSE_STACKS.remove(id)) event.getPoseStack().popPose();
    }

    private record Part(float x, float y, float z) {
        static Part capture(ModelPart part) {
            return new Part(part.xRot, part.yRot, part.zRot);
        }
        void restore(ModelPart part) {
            part.xRot = x;
            part.yRot = y;
            part.zRot = z;
        }
    }

    private record HumanoidPose(Part head, Part body, Part rightArm, Part leftArm,
                                Part rightLeg, Part leftLeg) {
        static HumanoidPose capture(HumanoidModel<?> model) {
            return new HumanoidPose(Part.capture(model.head), Part.capture(model.body),
                    Part.capture(model.rightArm), Part.capture(model.leftArm),
                    Part.capture(model.rightLeg), Part.capture(model.leftLeg));
        }
        void restore(HumanoidModel<?> model) {
            head.restore(model.head);
            body.restore(model.body);
            rightArm.restore(model.rightArm);
            leftArm.restore(model.leftArm);
            rightLeg.restore(model.rightLeg);
            leftLeg.restore(model.leftLeg);
        }
    }
}
