Forge 1.20.1 port of **Sable Player Ragdoll 0.7.2** and **Ragdoll Reactions 0.7.0**. The original Sable-backed ragdoll and reaction systems are combined into one mod JAR with both mod IDs.

## Requirements

- Minecraft 1.20.1
- Forge 47.x for Minecraft 1.20.1
- Java 17
- Sable for Forge 1.20.1, version 2.0.5-port.1
- Veil 1.0.0 for the Sable renderer

Sable and Veil remain external runtime dependencies. The combined JAR contains the two requested ragdoll mods; it does not embed either dependency.

## Build

Run `gradle jar --no-daemon`. The ForgeGradle output is in `build/libs/`.

The source snapshots are pinned to the commits matching the attached versions: `Leo-T22/sable-player-ragdoll@5500881f2ffe4329c6b9e1bf8e283997a620997d` and `Leo-T22/ragdoll-reactions@54ef8cc68b14eebb68b3e96ffca50b47adf48a6a`.

Original source licenses and notices are retained in the repository. Sable and Veil are separately authored dependencies and are not bundled.