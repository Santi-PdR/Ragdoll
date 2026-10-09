# Ragdoll Physics (Forge 1.20.1)

Forge-native reimplementation of the player and mob ragdoll reactions represented by the supplied Sable Ragdolls and Ragdoll Reactions mods.

## Build

Requires JDK 17. Run `gradle build`; the distributable JAR is written to `build/libs/`.

## Features

- One JAR combines the core ragdoll and reactions systems.
- Applies to players and mobs, with server config toggles.
- Starts on hard hits, falls, sudden speed changes, explosions, and lightning.
- Humanoid models tumble with animated arms and legs; player movement is locked until recovery.
- `/ragdoll` ragdolls the command source; operators can use `/ragdoll <target>`.
- Other Forge mods can use `RagdollAPI.launch(LivingEntity, Vec3)`.

The standalone physics uses vanilla entity movement and a client-side body roll. It does not reproduce Sable's articulated sub-level physics, persistent corpse parts, grabbing, or every original API. The original Sable Ragdolls and Ragdoll Reactions sources are separately licensed projects; this repository contains a Forge-native implementation and does not bundle Sable.
