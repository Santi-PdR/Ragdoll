package dev.leo.ragdollreactions.sound;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.bus.api.IEventBus;
import net.minecraftforge.neoforge.registries.DeferredRegister;

public final class ReactionSounds {
   private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "ragdoll_reactions");

   public static final Supplier<SoundEvent> PUNCH = SOUND_EVENTS.register(
      "punch", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ragdoll_reactions", "punch"))
   );

   private ReactionSounds() {
   }

   public static void register(IEventBus modBus) {
      SOUND_EVENTS.register(modBus);
   }
}
