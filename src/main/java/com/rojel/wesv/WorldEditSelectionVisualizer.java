/*
 * Decompiled with CFR 0_110.
 */

package com.rojel.wesv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.HandSide;

public class WorldEditSelectionVisualizer extends JavaPlugin {

	private Configuration config;
	private WorldEditHelper worldEditHelper;
	private ShapeHelper shapeHelper;

	private final List<UUID> shown = new ArrayList<>();
	private final List<UUID> lastSelectionTooLarge = new ArrayList<>();
	private final Map<UUID, Region> lastSelectedRegions = new HashMap<>();
	private final Map<UUID, Integer> fadeOutTasks = new HashMap<>();
	private final Map<UUID, Collection<Location>> playerParticleMap = new HashMap<>();

	@Override
	public void onEnable() {
		this.config = new Configuration(this);
		this.config.load();
		this.worldEditHelper = new WorldEditHelper(this);
		this.shapeHelper = new ShapeHelper(this.config);

		new ParticleTask(this);

		this.getServer().getPluginManager().registerEvents(new WesvListener(this), this);
		getCommand("wesv").setExecutor(new CommandWesv(this));

		for (final Player player : this.getServer().getOnlinePlayers()) {
			addPlayer(player);
		}

		MetricsUtils.register(this);
	}

	@SuppressWarnings("deprecation")
	public boolean isHoldingSelectionItem(final Player player) {
		// return isSelectionItem(player.getItemInHand());
		final ItemStack item = player.getItemInHand();
		if (item != null) {
			try {
				final Field wandItemField = LocalConfiguration.class.getDeclaredField("wandItem");

				if (wandItemField.getType() == int.class) {
					return item.getType().getId() == wandItemField.getInt(WorldEdit.getInstance().getConfiguration());
				} else if (wandItemField.getType() == String.class) {
					final String itemTypeId = BukkitAdapter.adapt(player).getItemInHand(HandSide.MAIN_HAND).getType().getId();
					return itemTypeId.equals(wandItemField.get(WorldEdit.getInstance().getConfiguration()));
				}
			} catch (ReflectiveOperationException e) {
				this.getLogger().log(Level.WARNING, "An error occured on isSelectionItem", e);
			}
		}

		return false;
	}

	public boolean isSelectionShown(final Player player) {
		return this.shown.contains(player.getUniqueId()) && this.shouldShowSelection(player);
	}

	public boolean shouldShowSelection(final Player player) {
		return this.config.isEnabled(player)
				&& (!this.config.isCheckForAxeEnabled() || this.isHoldingSelectionItem(player));
	}

	public void showSelection(final Player player) {
		if (!player.hasPermission("wesv.use")) {
			return;
		}

		final Region region = this.worldEditHelper.getSelectedRegion(player);
		final UUID uuid = player.getUniqueId();

		if (region != null && region.getArea() > this.config.getMaxSize()) {
			this.setParticlesForPlayer(player, null);

			if (!this.lastSelectionTooLarge.contains(uuid)) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + this.config.getLangSelectionSizeOf()
						+ this.config.getMaxSize() + this.config.getLangBlocks());
				this.lastSelectionTooLarge.add(uuid);
			}
		} else {
			this.lastSelectionTooLarge.remove(player.getUniqueId());
			this.setParticlesForPlayer(player, this.shapeHelper.getLocationsFromRegion(region));
		}
		this.shown.add(player.getUniqueId());
	}

	public void hideSelection(final Player player) {
		this.shown.remove(player.getUniqueId());
		this.playerParticleMap.remove(player.getUniqueId());
		this.cancelAndRemoveFadeOutTask(player.getUniqueId());
	}

	public void setParticlesForPlayer(final Player player, final Collection<Location> locations) {
		this.cancelAndRemoveFadeOutTask(player.getUniqueId());

		if (locations == null || locations.isEmpty()) {
			this.playerParticleMap.remove(player.getUniqueId());
		} else {
			this.playerParticleMap.put(player.getUniqueId(), locations);

			final int fade = config.getParticleFadeDelay();

			if (fade > 0) {
				final int id = this.getServer().getScheduler().runTaskLater(this, () -> {

					this.fadeOutTasks.remove(player.getUniqueId());
					this.playerParticleMap.remove(player.getUniqueId());
				}, fade).getTaskId();

				this.fadeOutTasks.put(player.getUniqueId(), id);
			}
		}
	}

	private void cancelAndRemoveFadeOutTask(final UUID uuid) {
		if (this.fadeOutTasks.containsKey(uuid)) {
			this.getServer().getScheduler().cancelTask(fadeOutTasks.get(uuid));
			this.fadeOutTasks.remove(uuid);
		}
	}

	public void addPlayer(final Player player) {
		if (this.shouldShowSelection(player)) {
			this.showSelection(player);
		}
	}

	public void removePlayer(final Player player) {
		final UUID uuid = player.getUniqueId();
		this.shown.remove(uuid);
		this.lastSelectionTooLarge.remove(uuid);
		this.lastSelectedRegions.remove(uuid);
		this.playerParticleMap.remove(uuid);

		this.cancelAndRemoveFadeOutTask(uuid);
	}

	public Configuration getCustomConfig() {
		return this.config;
	}

	public Map<UUID, Region> getLastSelectedRegions() {
		return this.lastSelectedRegions;
	}

	public Map<UUID, Collection<Location>> getPlayerParticleMap() {
		return this.playerParticleMap;
	}
}
