# WorldEditSelectionVisualizer

[![Java CI](https://github.com/MrMicky-FR/WorldEditSelectionVisualizer/actions/workflows/build.yml/badge.svg)](https://github.com/MrMicky-FR/WorldEditSelectionVisualizer/actions/workflows/build.yml)
[![Language grade](https://img.shields.io/lgtm/grade/java/g/MrMicky-FR/WorldEditSelectionVisualizer.svg?label=code%20quality&logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/MrMicky-FR/WorldEditSelectionVisualizer/context:java)
[![Discord](https://img.shields.io/discord/390919659874156560.svg?colorB=5865f2&label=Discord&logo=discord&logoColor=white)](https://discord.gg/q9UwaBT)

WorldEditSelectionVisualizer (WESV) is essentially the famous [WorldEditCUI](http://www.minecraftforum.net/topic/2171206-172-worldeditcui/) mod in the form of a bukkit plugin, which means that players don't need to install anything on their client.

## Features

- Spawns particles around WorldEdit selections so players can see them
- No client mod required
- Supports cuboid, sphere, ellipsoid, cylinder, polygon and convex selections
- Can display the current clipboard
- Use `/wesv toggle` to toggle the visualizer
- Option to only show the selection when holding the selection item
- Configurable particle effect and view distance
- Prevent players from spawning too many particles when selecting huge regions
- Highly customizable for the performance of your server
- Great compatibility: works with 1.7.10 to 1.19.x servers, with WorldEdit 6 and 7, and with or without FastAsyncWorldEdit
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) support (you can use the placeholders `%wesv_toggled_selection%` and `%wesv_toggled_clipboard%`)

## Downloads

You can download releases and find more information on [SpigotMC](https://www.spigotmc.org/resources/worldeditselectionvisualizer.17311/).

## Commands
* `/wesv toggle` and `/wesv toggle clipboard`: toggle the selection/clipboard visualization
* `/wesv lock`: lock/unlock the origin of the clipboard visualization to your current location
* `/wesv lock tp`: teleport to the lock location (when clipboard visualization lock is enabled)
* `/wesv reload`: reload the plugin config

## Screenshots

| Cuboid selection                                     | Cylinder selection                                     | Convex selection                                     |
|------------------------------------------------------|--------------------------------------------------------|------------------------------------------------------|
| ![Cuboid selection](https://i.imgur.com/jGVVpgx.png) | ![Cylinder selection](https://i.imgur.com/XLprNDA.png) | ![Convex selection](https://i.imgur.com/XKalgCn.png) |

[More screenshots](https://imgur.com/a/CEwIxaV)

## Credits

WorldEditSelectionVisualizer was originally made by [Rojetto](https://dev.bukkit.org/projects/worldedit-selection-visualizer/),
and then was updated and maintained by [martinambrus](https://github.com/martinambrus/).

### YourKit

[![YourKit](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com/)

YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/),
[YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/),
and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/).
