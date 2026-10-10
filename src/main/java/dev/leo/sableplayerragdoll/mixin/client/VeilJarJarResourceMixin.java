package dev.leo.sableplayerragdoll.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.ext.PackResourcesExtension.PackResourceConsumer;
import foundry.veil.impl.resource.VeilResourceManagerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Forge 1.20.1's Jar-in-Jar PathFS can expose paths backed by different file systems
 * while Veil recursively enumerates a pack. Catch only that known traversal failure
 * so Veil does not fall back to Forge's invalid empty-prefix resource scan.
 */
@Mixin(value = VeilResourceManagerImpl.class, remap = false)
public abstract class VeilJarJarResourceMixin {
    @WrapOperation(
            method = "loadPack",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lfoundry/veil/ext/PackResourcesExtension;veil$listResources(Lfoundry/veil/ext/PackResourcesExtension$PackResourceConsumer;)V",
                    remap = false
            )
    )
    private void ragdoll$handleJarJarFilesystem(
            PackResourcesExtension resources,
            PackResourceConsumer consumer,
            Operation<Void> original
    ) {
        try {
            original.call(resources, consumer);
        } catch (IllegalArgumentException exception) {
            if (!"Wrong filesystem".equals(exception.getMessage())) {
                throw exception;
            }
        }
    }
}
