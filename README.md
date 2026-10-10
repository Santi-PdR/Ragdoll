Forge 1.20.1 port of **Sable Player Ragdoll 0.7.2** and **Ragdoll Reactions 0.7.0**. The original Sable-backed ragdoll and reaction systems are combined into one mod JAR with both mod IDs.

## Requirements

- Minecraft 1.20.1
- Forge 47.x for Minecraft 1.20.1
- Java 17
- Sable Forge 1.20.1, version 2.0.5-port.4 (the Ghouls compatibility build)
- Veil Forge 1.0.0.296 slim build for the Sable renderer

The compatibility build keeps Sable's Rapier physics engine and disables only Sable's Create 6 / Flywheel 1.0 integration mixins, so Forge can load the Create 0.5.1 version used by Ghouls. Sable keeps vanilla Entity methods available to Canary and Brutality. Its movement-state mixin applies sublevel block checks at method return; its fluid mixin uses Sable's original full calculation only while an entity intersects a Sable sublevel. Sable's moving-contraption integration with Create is unavailable in this build.

When EntityCulling is installed, this mod registers ragdoll dolls, seats, and player/mob ragdoll parts with EntityCulling's dynamic whitelist. EntityCulling remains optional. Other Sable sublevel entities may still need their own compatibility handling.

Sable and Veil remain external runtime dependencies. Use the official Veil slim JAR; the full Forge 1.20.1 Veil JAR bundles LWJGL classes that conflict with Minecraft's LWJGL modules in the Ghouls profile. The slim artifact omits those bundled classes and the CI checks its SHA-256 and contents. The combined JAR contains the two requested ragdoll mods; it does not embed either dependency.

## Build

Run `gradle jar --no-daemon`. The ForgeGradle output is in `build/libs/`.

The source snapshots are pinned to the commits matching the attached versions: `Leo-T22/sable-player-ragdoll@5500881f2ffe4329c6b9e1bf8e283997a620997d` and `Leo-T22/ragdoll-reactions@54ef8cc68b14eebb68b3e96ffca50b47adf48a6a`. The Sable Forge dependency build starts from `NotAsher999/sable-1.20.1@e4d2ab3724a9f5820e758646447d3a571761db11` and applies the tracked compatibility patch script. At that pinned revision, 50 of 51 Sable tests pass; the remaining unrelated `PoweredShaftLinkageCompatibilityContractTest` fails, so the compatibility build uses Sable's documented `quick` mode to compile/package without its failing test task or test-only tools.

Original source licenses and notices are retained in the repository. Sable and Veil are separately authored dependencies and are not bundled.
