# WorldEditSelectionVisualizer
[![Build Status](https://travis-ci.org/MrMicky-FR/WorldEditSelectionVisualizer.svg?branch=master)](https://travis-ci.org/MrMicky-FR/WorldEditSelectionVisualizer) 
[![codecov](https://codecov.io/gh/MrMicky-FR/WorldEditSelectionVisualizer/branch/master/graph/badge.svg)](https://codecov.io/gh/MrMicky-FR/WorldEditSelectionVisualizer)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a9fd5bd3cfa3443cac965a7c89ebbccb)](https://www.codacy.com/app/MrMicky-FR/WorldEditSelectionVisualizer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=MrMicky-FR/WorldEditSelectionVisualizer&amp;utm_campaign=Badge_Grade) 
[![Maintenance](https://img.shields.io/maintenance/yes/2018.svg)]()

WorldEditSelectionVisualizer (WESV) is essentially the famous [WorldEditCUI](http://www.minecraftforum.net/topic/2171206-172-worldeditcui/) mod in the form of a bukkit plugin, which means that players don't need to install anything on their client.

## Features

- Spawns particles around WorldEdit selections so players can see them
- No client mod required
- Supports cuboid, sphere, ellipsoid, cylinder, polygon and convex selections
- Use `/wesv` to toggle the visualizer
- Only show the selection when holding the selection item
- Configurable particle effect
- Force large particle distances to see your selection from far away
- Prevent players from spawning too many particles when selecting huge regions
- Highly customizable for the performance of your server
- [Documentation](http://martinambrus.github.io/WorldEdit-ServerSide-Visualizer/javadoc)

## Downloads

You can download releases and find more information on SpigotMC: https://www.spigotmc.org/resources/worldeditselectionvisualizer.17311/

## Screenshots

![Cuboid selection](https://proxy.spigotmc.org/2d092800fc87fc4cae09cb4191207971ef8a002a?url=http%3A%2F%2Fi.imgur.com%2F0MAcN3o.png) ![Polygon selection](https://proxy.spigotmc.org/5a6ad5f03fe42e43b289d84a16c429d369868451?url=http%3A%2F%2Fi.imgur.com%2FOqSQQr7.png) ![Ellipsoid selection](https://proxy.spigotmc.org/814c230f1f30bd9b4a20743c473074ade8b7510d?url=http%3A%2F%2Fi.imgur.com%2FpOwYY62.png) ![Customizable particle effect, large particle distance](https://proxy.spigotmc.org/a3a2b2fe96a312f3a5d8bdfe8b36c07134037e97?url=http%3A%2F%2Fi.imgur.com%2FVcR0IMA.png)

## Metrics

WorldEditSelectionVisualizer collects statistics anonymously via [bStats](https://bstats.org/). You can see the statistics here: https://bstats.org/plugin/bukkit/WorldEditSelectionVisualizer
If you want to disable bStats, you just need to set `enabled` to `false` in `plugins/bStats/config.yml`, but statistics are really useful for developers.
