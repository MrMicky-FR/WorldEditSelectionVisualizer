/*
 * Decompiled with CFR 0_110.
 */

package com.rojel.wesv;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;

public class WorldEditHelper extends BukkitRunnable {

	private final WorldEditSelectionVisualizer plugin;
	private final WorldEditPlugin we;

	public WorldEditHelper(final WorldEditSelectionVisualizer plugin) {
		super();

		this.plugin = plugin;
		this.we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");

		runTaskTimer(plugin, 0, plugin.getCustomConfig().getUpdateSelectionInterval());
	}

	@Override
	public void run() {
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (!plugin.getCustomConfig().isEnabled(player) || !player.hasPermission("wesv.use")) {
				continue;
			}

			final Region currentRegion = WorldEditHelper.this.getSelectedRegion(player);

			if (!WorldEditHelper.this.compareRegion(plugin.getLastSelectedRegions().get(player.getUniqueId()),
					currentRegion)) {
				if (currentRegion != null) {
					plugin.getLastSelectedRegions().put(player.getUniqueId(), currentRegion.clone());
				} else {
					plugin.getLastSelectedRegions().remove(player.getUniqueId());
				}

				plugin.getServer().getPluginManager()
						.callEvent(new WorldEditSelectionChangeEvent(player, currentRegion));
			}
		}
	}

	public Region getSelectedRegion(final Player player) {
		RegionSelector selector;
		final LocalSession session = this.we.getWorldEdit().getSessionManager().findByName(player.getName());

		if (session != null && session.getSelectionWorld() != null
				&& (selector = session.getRegionSelector(session.getSelectionWorld())).isDefined()) {
			try {
				return selector.getRegion();
			} catch (final IncompleteRegionException e) {
				this.plugin.getLogger().warning("Region still incomplete.");
			}
		}
		return null;
	}

	public boolean compareRegion(final Region r1, final Region r2) {
		if (r1 == null && r2 == null) {
			return true;
		}

		if (r1 == null || r2 == null) {
			return false;
		}

		final boolean points = r1.getMinimumPoint().equals(r2.getMinimumPoint())
				&& r1.getMaximumPoint().equals(r2.getMaximumPoint()) && r1.getCenter().equals(r2.getCenter());
		final boolean worlds = r1.getWorld() != null ? r1.getWorld().equals(r2.getWorld()) : r2.getWorld() == null;
		final boolean dimensions = r1.getWidth() == r2.getWidth() && r1.getHeight() == r2.getHeight()
				&& r1.getLength() == r2.getLength();
		final boolean type = r1.getClass().equals(r2.getClass());
		boolean polyPoints = true;

		if (r1 instanceof Polygonal2DRegion && r2 instanceof Polygonal2DRegion) {
			final Polygonal2DRegion r1Poly = (Polygonal2DRegion) r1;
			final Polygonal2DRegion r2Poly = (Polygonal2DRegion) r2;

			if (r1Poly.getPoints().size() != r2Poly.getPoints().size()) {
				polyPoints = false;
			} else {
				for (int i = 0; i < r1Poly.getPoints().size(); ++i) {
					if (r1Poly.getPoints().get(i).equals(r2Poly.getPoints().get(i))) {
						continue;
					}
					polyPoints = false;
				}
			}
		}
		return points && worlds && dimensions && type && polyPoints;
	}
}
