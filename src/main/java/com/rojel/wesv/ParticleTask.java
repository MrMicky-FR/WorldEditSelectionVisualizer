/*
 * Decompiled with CFR 0_110.
 */

package com.rojel.wesv;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.darkblade12.particleeffect.ParticleEffect;
import com.darkblade12.particleeffect.ParticleEffect.ParticleColor;
import com.darkblade12.particleeffect.ParticleEffect.ParticleData;

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
			final Player player = ParticleTask.this.plugin.getServer().getPlayer(uuid);
			for (final Location loc : plugin.getPlayerParticleMap().get(uuid)) {
				if (!loc.getWorld().equals(player.getLocation().getWorld())
						|| loc.distanceSquared(player.getLocation()) > particleDistance * particleDistance) {
					continue;
				}

				final ParticleEffect particle = plugin.getCustomConfig().getParticle();
				final Object particleData = plugin.getCustomConfig().getParticleData();

				if (particleData instanceof ParticleColor) {
					plugin.getCustomConfig().getParticle().display((ParticleColor) particleData, loc, player);
				} else if (particleData instanceof ParticleData) {
					particle.display((ParticleData) particleData, 0.0f, 0.0f, 0.0f, 0.0f, 1, loc, player);
				} else {
					particle.display(0.0f, 0.0f, 0.0f, 0.0f, 1, loc, player);
				}
			}
		}
	}
}
