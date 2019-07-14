package com.rojel.wesv;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.Collection;

public class ParticleTask extends BukkitRunnable {

    private final WorldEditSelectionVisualizer plugin;

    public ParticleTask(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 1, plugin.getCustomConfig().getUpdateParticlesInterval());
    }

    @Override
    public void run() {
        double particleDistanceSquared = NumberConversions.square(plugin.getCustomConfig().getParticleDistance());

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Location loc = player.getLocation();
            Collection<ImmutableVector> vectors = plugin.getPlayerParticleMap().get(player.getUniqueId());

            if (vectors == null || vectors.isEmpty()) {
                continue;
            }

            ParticleType particle = plugin.getCustomConfig().getParticle();
            Object particleData = plugin.getCustomConfig().getParticleData();

            for (ImmutableVector vec : vectors) {
                if (vec.distanceSquared(loc.getX(), loc.getY(), loc.getZ()) > particleDistanceSquared) {
                    continue;
                }

                FastParticle.spawnParticle(player, particle, vec.getX(), vec.getY(), vec.getZ(), 1, 0.0, 0.0, 0.0, 0.0, particleData);
            }
        }
    }
}
