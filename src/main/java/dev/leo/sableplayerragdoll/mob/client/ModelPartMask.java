package dev.leo.sableplayerragdoll.mob.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

final class ModelPartMask {
    private static final Field MODEL_PART_CUBES = field(ModelPart.class, "f_104212_");

    private ModelPartMask() {
    }

    static SavedPart save(ModelPart part) {
        return new SavedPart(part, part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.visible, cubes(part));
    }

    static void hideCubes(ModelPart part) {
        setCubes(part, List.of());
    }

    static void resetToInitialPose(ModelPart part) {
        var initial = part.getInitialPose();
        part.x = initial.x;
        part.y = initial.y;
        part.z = initial.z;
        part.xRot = initial.xRot;
        part.yRot = initial.yRot;
        part.zRot = initial.zRot;
    }

    @SuppressWarnings("unchecked")
    private static List<ModelPart.Cube> cubes(ModelPart part) {
        try {
            return (List<ModelPart.Cube>) MODEL_PART_CUBES.get(part);
        } catch (IllegalAccessException ignored) {
            return new ArrayList<>();
        }
    }

    private static void setCubes(ModelPart part, List<ModelPart.Cube> cubes) {
        try {
            MODEL_PART_CUBES.set(part, cubes);
        } catch (IllegalAccessException ignored) {
        }
    }

    private static Field field(Class<?> type, String name) {
        return ObfuscationReflectionHelper.findField(type, name);
    }

    record SavedPart(ModelPart part, float x, float y, float z,
                     float xRot, float yRot, float zRot,
                     boolean visible, List<ModelPart.Cube> cubes) {
        void restore() {
            this.part.x = this.x;
            this.part.y = this.y;
            this.part.z = this.z;
            this.part.xRot = this.xRot;
            this.part.yRot = this.yRot;
            this.part.zRot = this.zRot;
            this.part.visible = this.visible;
            setCubes(this.part, this.cubes);
        }
    }
}
