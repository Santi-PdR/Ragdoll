# Ragdoll Physics (Forge 1.20.1)

Forge-native reimplementation of the player and mob ragdoll reactions represented by the supplied Sable Ragdolls and Ragdoll Reactions mods.

## Build

Requires JDK 17. Run `gradle build`; the distributable JAR is written to `build/libs/`.

## Current port scope

This standalone Forge mod combines the core ragdoll trigger path and reaction triggers in one JAR. It uses vanilla entity movement and a client-side body roll, so it does not reproduce Sable's articulated sub-level physics, corpse parts, grabbing, or every original API. Reactions currently include hard hits, falls, explosions, and lightning. Server behavior is configurable in `config/ragdoll-server.toml`.

The original Sable Ragdolls and Ragdoll Reactions sources are separately licensed projects; this repository contains a Forge-native implementation and does not bundle Sable.
