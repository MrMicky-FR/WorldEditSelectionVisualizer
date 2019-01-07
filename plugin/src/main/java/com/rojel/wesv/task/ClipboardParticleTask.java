package com.rojel.wesv.task;

import com.rojel.wesv.ImmutableVector;
import com.rojel.wesv.WorldEditSelectionVisualizer;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Collection;

public class ClipboardParticleTask extends BukkitRunnable {

    private final WorldEditSelectionVisualizer plugin;

    public ClipboardParticleTask(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 1, plugin.getCustomConfig().getUpdateClipboardParticlesInterval());
    }

    @Override
    public void run() {
        final int particleDistance = plugin.getCustomConfig().getParticleDistance();

        final ArrayList<Collection<ImmutableVector>> allParticles = new ArrayList<>();

        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            final Location loc = player.getLocation();

            /** Handles clipboard particles */
            final Collection<ImmutableVector> clipboardVectors = plugin.getPlayerClipboardParticleMap().get(player.getUniqueId());

            if (clipboardVectors == null || clipboardVectors.isEmpty()) {
                continue;
            }

            if (plugin.getCustomConfig().isShowForAllPlayersEnabled()) {
                allParticles.add(clipboardVectors);
            } else {
                for (final ImmutableVector vec : clipboardVectors) {
                    if (vec.distanceSquared(loc.getX(), loc.getY(), loc.getZ()) > NumberConversions.square(particleDistance)) {
                        continue;
                    }

                    final ParticleType particle = plugin.getCustomConfig().getClipboardParticle();
                    final Object particleData = plugin.getCustomConfig().getClipboardParticleData();

                    FastParticle.spawnParticle(player, particle, vec.getX(), vec.getY(), vec.getZ(), 1, 0.0, 0.0, 0.0, 0.0, particleData);
                }
            }
        }

        if (plugin.getCustomConfig().isShowForAllPlayersEnabled() && allParticles != null && !allParticles.isEmpty()) {
            for (final Player player : plugin.getServer().getOnlinePlayers()) {
                final Location loc = player.getLocation();

                for (final Collection<ImmutableVector> vectors : allParticles) {
                    for (final ImmutableVector vec : vectors) {
                        if (vec.distanceSquared(loc.getX(), loc.getY(), loc.getZ()) > NumberConversions.square(particleDistance)) {
                            continue;
                        }

                        final ParticleType particle = plugin.getCustomConfig().getClipboardParticle();
                        final Object particleData = plugin.getCustomConfig().getClipboardParticleData();

                        FastParticle.spawnParticle(player, particle, vec.getX(), vec.getY(), vec.getZ(), 1, 0.0, 0.0, 0.0, 0.0, particleData);
                    }
                }
            }
        }
    }
}
