[![Coverage Status](https://coveralls.io/repos/github/martinambrus/WorldEdit-ServerSide-Visualizer/badge.svg)](https://coveralls.io/github/martinambrus/WorldEdit-ServerSide-Visualizer) [![codecov](https://codecov.io/gh/martinambrus/WorldEdit-ServerSide-Visualizer/branch/master/graph/badge.svg)](https://codecov.io/gh/martinambrus/WorldEdit-ServerSide-Visualizer)
 <a href="https://codeclimate.com/github/martinambrus/WorldEdit-ServerSide-Visualizer/coverage"><img src="https://codeclimate.com/github/martinambrus/WorldEdit-ServerSide-Visualizer/badges/coverage.svg" /></a> <a href="https://codeclimate.com/github/martinambrus/WorldEdit-ServerSide-Visualizer"><img src="https://codeclimate.com/github/martinambrus/WorldEdit-ServerSide-Visualizer/badges/issue_count.svg" /></a> [![Gitter Chat](http://img.shields.io/badge/chat-online-brightgreen.svg)](https://gitter.im/WorldEdit-ServerSide-Visualizer/Lobby)

Overview

WorldEditSelectionVisualizer (WESV) is essentially the famous WorldEditCUI mod (http://www.minecraftforum.net/topic/2171206-172-worldeditcui/) in the form of a bukkit plugin, which means that players don't need to install anything on their client.

Features

- Spawns particles around WorldEdit selections so players can see them
- No client mod required
- Supports cuboid, sphere, ellipsoid, cylinder and polygon selections
- Use /wesv to toggle the visualizer
- Only show the selection when holding the selection item
- Configurable particle effect
- Force large particle distances to see your selection from far away (requires ProtocolLib)
- Prevent players from spawning too many particles when selecting huge regions
- Highly customizable for the performance of your server
- Documentation

Detailed documentation about the available permissions and configuration options is available on the documentation page (http://dev.bukkit.org/bukkit-plugins/worldedit-selection-visualizer/pages/documentation/)

ProtocolLib

This plugin has optional support for ProtocolLib (http://dev.bukkit.org/bukkit-plugins/protocollib/). If you want to see particles from distances > 16, you need to have it installed and enabled in the configuration (see Documentation - http://dev.bukkit.org/bukkit-plugins/worldedit-selection-visualizer/pages/documentation/).

Screenshots

Cuboid selection Polygon selection Ellipsoid selection Customizable particle effect, large particle distance

Credits and Metrics

This plugin uses darkblade12's ParticleEffect library (https://forums.bukkit.org/threads/lib-1-7-particleeffect-v1-4.154406/) to spawn the particles. It also anonymously collects statistics via MCStats Plugin Metrics. You can read about the data that is being collected here: https://forums.bukkit.org/threads/mcstats-plugin-metrics-r7-easy-advanced-plugin-statistics.77352/. You can see the statistics here: http://mcstats.org/plugin/WorldEditSelectionVisualizer
