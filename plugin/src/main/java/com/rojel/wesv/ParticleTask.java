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

    public ParticleTask(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 1, plugin.getCustomConfig().getUpdateParticlesInterval());
    }

    @Override
    public void run() {
        final int particleDistance = plugin.getCustomConfig().getParticleDistance();
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            final Location loc = player.getLocation();
            final Collection<ImmutableVector> vectors = plugin.getPlayerParticleMap().get(player.getUniqueId());

            if (vectors == null || vectors.isEmpty()) {
                continue;
            }

            for (final ImmutableVector vec : vectors) {
                if (vec.distanceSquared(loc.getX(), loc.getY(), loc.getZ()) > NumberConversions.square(particleDistance)) {
                    continue;
                }

                final ParticleType particle = plugin.getCustomConfig().getParticle();
                final Object particleData = plugin.getCustomConfig().getParticleData();

                FastParticle.spawnParticle(player, particle, vec.getX(), vec.getY(), vec.getZ(), 1, 0.0, 0.0, 0.0, 0.0, particleData);
            }
        }
    }
}
