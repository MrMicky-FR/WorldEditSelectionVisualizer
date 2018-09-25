package com.rojel.wesv;

import java.util.Collection;

import com.sk89q.worldedit.Vector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;

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
			final Collection<Vector> vectors = plugin.getPlayerParticleMap().get(player.getUniqueId());

			if (vectors == null || vectors.isEmpty()) {
				continue;
			}

			for (final Vector vec : vectors) {
				if (distance(loc, vec) > NumberConversions.square(particleDistance)) {
					continue;
				}

				final ParticleType particle = plugin.getCustomConfig().getParticle();
				final Object particleData = plugin.getCustomConfig().getParticleData();

				FastParticle.spawnParticle(player, particle, vec.getX(), vec.getY(), vec.getZ(), 1, 0.0, 0.0, 0.0, 0.0, particleData);
			}
		}
	}

	private double distance(Location l, Vector v) {
		return NumberConversions.square(l.getX() - v.getX()) + NumberConversions.square(l.getY() - v.getY()) + NumberConversions.square(l.getZ() - v.getZ());
	}
}
