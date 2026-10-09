package dev.santi.ragdoll.client;

import dev.santi.ragdoll.RagdollStatePacket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientRagdollState {
    private static final Map<Integer, Float> ANGLES = new ConcurrentHashMap<>();
    private static final Set<Integer> ACTIVE = ConcurrentHashMap.newKeySet();

    private ClientRagdollState() {}

    public static void accept(RagdollStatePacket packet) {
        if (packet.active()) {
            ACTIVE.add(packet.entityId());
            ANGLES.put(packet.entityId(), packet.angle());
        } else {
            ACTIVE.remove(packet.entityId());
            ANGLES.remove(packet.entityId());
        }
    }

    public static boolean isActive(int entityId) {
        return ACTIVE.contains(entityId);
    }

    public static float angle(int entityId) {
        return ANGLES.getOrDefault(entityId, 0.0f);
    }
}
