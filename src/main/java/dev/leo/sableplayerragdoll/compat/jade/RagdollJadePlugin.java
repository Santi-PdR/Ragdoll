package dev.leo.sableplayerragdoll.compat.jade;

import dev.leo.sableplayerragdoll.block.RagdollPartBlock;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import dev.leo.sableplayerragdoll.mob.block.MobRagdollPartBlock;
import dev.leo.sableplayerragdoll.mob.block.entity.MobRagdollPartBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class RagdollJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(RagdollPartServerDataProvider.INSTANCE, RagdollPartBlockEntity.class);
        registration.registerBlockDataProvider(MobRagdollPartServerDataProvider.INSTANCE, MobRagdollPartBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RagdollPartTooltipProvider.INSTANCE, RagdollPartBlock.class);
        registration.registerBlockComponent(MobRagdollPartTooltipProvider.INSTANCE, MobRagdollPartBlock.class);
    }
}