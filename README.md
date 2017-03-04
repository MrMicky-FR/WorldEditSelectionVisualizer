[![Stories in Ready](https://badge.waffle.io/martinambrus/WorldEdit-ServerSide-Visualizer.png?label=ready&title=Ready)](https://waffle.io/martinambrus/WorldEdit-ServerSide-Visualizer)
[![Build Status](https://travis-ci.org/martinambrus/WorldEdit-ServerSide-Visualizer.svg?branch=master)](https://travis-ci.org/martinambrus/WorldEdit-ServerSide-Visualizer) [![codecov](https://codecov.io/gh/martinambrus/WorldEdit-ServerSide-Visualizer/branch/master/graph/badge.svg)](https://codecov.io/gh/martinambrus/WorldEdit-ServerSide-Visualizer)
 [![Codacy Badge](https://api.codacy.com/project/badge/Grade/9e43833af9e1428580111b7ea25c8e32)](https://www.codacy.com/app/martinambrus/WorldEdit-ServerSide-Visualizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=martinambrus/WorldEdit-ServerSide-Visualizer&amp;utm_campaign=Badge_Grade) [![Gitter Chat](http://img.shields.io/badge/chat-online-brightgreen.svg)](https://gitter.im/WorldEdit-ServerSide-Visualizer/Lobby)

**Overview**

WorldEditSelectionVisualizer (WESV) is essentially the famous WorldEditCUI mod (http://www.minecraftforum.net/topic/2171206-172-worldeditcui/) in the form of a bukkit plugin, which means that players don't need to install anything on their client.

**Features**

- Spawns particles around WorldEdit selections so players can see them
- No client mod required
- Supports cuboid, sphere, ellipsoid, cylinder and polygon selections
- Use /wesv to toggle the visualizer
- Only show the selection when holding the selection item
- Configurable particle effect
- Force large particle distances to see your selection from far away (requires ProtocolLib)
- Prevent players from spawning too many particles when selecting huge regions
- Highly customizable for the performance of your server
- [Documentation](http://martinambrus.github.io/WorldEdit-ServerSide-Visualizer/javadoc)

Detailed documentation about the available permissions and configuration options is available on the documentation page (http://dev.bukkit.org/bukkit-plugins/worldedit-selection-visualizer/pages/documentation/)

**ProtocolLib**

This plugin has optional support for ProtocolLib (http://dev.bukkit.org/bukkit-plugins/protocollib/). If you want to see particles from distances > 16, you need to have it installed and enabled in the configuration (see Documentation - http://dev.bukkit.org/bukkit-plugins/worldedit-selection-visualizer/pages/documentation/).

**Downloads**

This plugin can be downloaded from the [official page](https://www.spigotmc.org/resources/worldeditselectionvisualizer.17311/) of the unofficial 1.9 - 1.1x updates from ZathrusWriter or directly from the [Releases page](https://github.com/martinambrus/WorldEdit-ServerSide-Visualizer/releases) here on GitHub.

**Documentation**

A [JavaDoc documentation](http://martinambrus.github.io/WorldEdit-ServerSide-Visualizer/javadoc) is available, in case you'd feel the need to hack into the plugin :-P

**Screenshots**

![Cuboid selection](https://proxy.spigotmc.org/2d092800fc87fc4cae09cb4191207971ef8a002a?url=http%3A%2F%2Fi.imgur.com%2F0MAcN3o.png) ![Polygon selection](https://proxy.spigotmc.org/5a6ad5f03fe42e43b289d84a16c429d369868451?url=http%3A%2F%2Fi.imgur.com%2FOqSQQr7.png) ![Ellipsoid selection](https://proxy.spigotmc.org/814c230f1f30bd9b4a20743c473074ade8b7510d?url=http%3A%2F%2Fi.imgur.com%2FpOwYY62.png) ![Customizable particle effect, large particle distance](https://proxy.spigotmc.org/a3a2b2fe96a312f3a5d8bdfe8b36c07134037e97?url=http%3A%2F%2Fi.imgur.com%2FVcR0IMA.png)

**Credits and Metrics**

This plugin uses darkblade12's ParticleEffect library (https://forums.bukkit.org/threads/lib-1-7-particleeffect-v1-4.154406/) to spawn the particles. It also anonymously collects statistics via MCStats Plugin Metrics. You can read about the data that is being collected here: https://forums.bukkit.org/threads/mcstats-plugin-metrics-r7-easy-advanced-plugin-statistics.77352/. You can see the statistics here: http://mcstats.org/plugin/WorldEditSelectionVisualizer
