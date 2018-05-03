/*
 * Decompiled with CFR 0_110.
 */

package com.rojel.wesv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sk89q.worldedit.WorldEdit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.regions.Region;

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

		for (final Player player : getServer().getOnlinePlayers()) {
			addPlayer(player);
		}

		new CustomMetrics(this, this.config).initMetrics();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (args.length == 0 || !args[0].equalsIgnoreCase("reload") || !sender.hasPermission("wesv.reloadconfig")) {
			if (sender instanceof Player) {

				final Player player = (Player) sender;
				final boolean isEnabled = !this.config.isEnabled(player);
				this.config.setEnabled(player, isEnabled);

				if (isEnabled) {
					player.sendMessage(ChatColor.GREEN + this.config.getLangVisualizerEnabled());
					if (this.shouldShowSelection(player)) {
						this.showSelection(player);
					}
				} else {
					player.sendMessage(ChatColor.RED + this.config.getLangVisualizerDisabled());
					this.hideSelection(player);
				}
			} else {
				sender.sendMessage(this.config.getLangPlayersOnly());
			}
		} else {
			this.config.reloadConfig();
			sender.sendMessage(this.config.getConfigReloaded());
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean isSelectionItem(final ItemStack item) {
		return item != null && item.getTypeId() == WorldEdit.getInstance().getConfiguration().wandItem;
	}

	@SuppressWarnings("deprecation")
	public boolean isHoldingSelectionItem(final Player player) {
		return isSelectionItem(player.getItemInHand());
	}

	public boolean isSelectionShown(final Player player) {
		return this.shown.contains(player.getUniqueId()) && this.shouldShowSelection(player);
	}

	public boolean shouldShowSelection(final Player player) {
		return this.config.isEnabled(player) && (!this.config.isCheckForAxeEnabled()
				|| this.config.isCheckForAxeEnabled() && this.isHoldingSelectionItem(player));
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
				final int id = this.getServer().getScheduler().runTaskLater(this, new Runnable() {

					@Override
					public void run() {
						WorldEditSelectionVisualizer.this.fadeOutTasks.remove(player.getUniqueId());
						WorldEditSelectionVisualizer.this.playerParticleMap.remove(player.getUniqueId());
					}
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
