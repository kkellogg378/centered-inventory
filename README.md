# Centered Inventory

A Fabric mod for Minecraft `26.1.X` and `26.2` that centers the player inventory screen when the recipe book is open

## Compatible Mods
[Cloth Config](https://modrinth.com/mod/cloth-config) - Not required, but allows enabling/disabling the mod via [Mod Menu](https://modrinth.com/mod/modmenu). Alternatively, just edit `config/centered_inventory.json`

## What it does

- Centers the player inventory screen when the recipe book is visible.
- Preserves normal layout behavior when the recipe book is hidden or the window is too narrow.
- Applies the adjustment on the client side via a Fabric mixin.

## 1.21.11 Version?

User 4Ply has made a very nice port of the mod for 1.21.11, check it out [here](https://github.com/4Ply/centered-inventory)!

## Build Instructions

1. Open a terminal in the project root: `c:\repos\centered_inventory`
2. Run the Fabric Gradle build:
   - `./gradlew.bat build --console=plain`
3. If you only need to validate Java compilation, run:
   - `./gradlew.bat compileJava --console=plain`
   - `./gradlew.bat compileClientJava --console=plain`

## Installation

1. Build the mod JAR using the Gradle `build` task.
2. Copy the generated JAR from `build/libs/` into your Fabric `mods/` folder.
3. Run Minecraft with Fabric Loader and Fabric API for `26.1.X`.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
