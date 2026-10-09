package dev.santi.ragdoll.client;

import dev.santi.ragdoll.RagdollStatePacket;
import net.minecraft.client.Minecraft;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientRagdollState {
    private static final Map<Integer, Float> ANGLES = new ConcurrentHashMap<>();

    private ClientRagdollState() {}

    public static void accept(RagdollStatePacket packet) {
        if (packet.active()) ANGLES.put(packet.entityId(), packet.angle());
        else ANGLES.remove(packet.entityId());
    }

    public static float angle(int entityId) {
        return ANGLES.getOrDefault(entityId, 0.0f);
    }

    public static void clear() {
        if (Minecraft.getInstance().level == null) ANGLES.clear();
    }
}
