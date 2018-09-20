package com.rojel.wesv;

import java.util.UUID;

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
		super();

		this.plugin = plugin;

		runTaskTimer(this.plugin, 1, plugin.getCustomConfig().getUpdateParticlesInterval());
	}

	@Override
	public void run() {
		final int particleDistance = plugin.getCustomConfig().getParticleDistance();
		for (final UUID uuid : plugin.getPlayerParticleMap().keySet()) {
			final Player player = this.plugin.getServer().getPlayer(uuid);
			final Location loc = player.getLocation();

			for (final Vector vec : this.plugin.getPlayerParticleMap().get(uuid)) {
				if (distance(loc, vec) > particleDistance * particleDistance) {
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
