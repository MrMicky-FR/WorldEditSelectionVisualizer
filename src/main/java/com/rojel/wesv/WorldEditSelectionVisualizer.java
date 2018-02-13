/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  com.sk89q.worldedit.regions.Region
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */

package com.rojel.wesv;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.regions.Region;

public class WorldEditSelectionVisualizer extends JavaPlugin implements Listener {
	private Configuration config;
	private WorldEditHelper worldEditHelper;
	private ShapeHelper shapeHelper;
	private ParticleSender particleSender;
	private Map<UUID, Boolean> shown;
	private Map<UUID, Boolean> lastSelectionTooLarge;

	@Override
	public void onEnable() {
		this.shown = new HashMap<UUID, Boolean>();
		this.lastSelectionTooLarge = new HashMap<UUID, Boolean>();
		this.config = new Configuration(this);
		this.config.load();
		this.worldEditHelper = new WorldEditHelper(this, this.config);
		this.shapeHelper = new ShapeHelper(this.config);
		this.particleSender = new ParticleSender(this, this.config);
		this.getServer().getPluginManager().registerEvents(this, this);
		new CustomMetrics(this, this.config).initMetrics();

	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (sender instanceof Player && "wesv".equals(label)) {
			final Player player = (Player) sender;
			if ("wesv".equals(label) && player.hasPermission("wesv.toggle")) {
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
				return true;
			}
		} else if ("wesv_reload".equals(label) && sender.hasPermission("wesv.reloadconfig")) {
			this.config.reloadConfig();
			sender.sendMessage(this.config.getConfigReloaded());
			return true;
		} else {
			sender.sendMessage(this.config.getLangPlayersOnly());
			return true;
		}
		return false;
	}

	@EventHandler
	private void onWorldEditSelectionChange(final WorldEditSelectionChangeEvent event) {
		final Player player = event.getPlayer();
		if (this.isSelectionShown(player)) {
			this.showSelection(player);
		}
	}

	@EventHandler
	private void onItemChange(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		if (this.config.isCheckForAxeEnabled() && this.config.isEnabled(player)) {
			final ItemStack item = player.getInventory().getItem(event.getNewSlot());
			if (item != null && item.getType() == this.config.getSelectionItemConfigValue()) {
				this.showSelection(player);
			} else {
				this.hideSelection(player);
			}
		}
	}

	@EventHandler
	private void onPlayerQuit(final PlayerQuitEvent event) {
		this.shown.remove(event.getPlayer().getUniqueId());
		this.lastSelectionTooLarge.remove(event.getPlayer().getUniqueId());
	}

	public boolean holdsSelectionItem(final Player player) {
		@SuppressWarnings("deprecation")
		final ItemStack item = player.getItemInHand();
		return item != null && item.getType() == this.config.getSelectionItemConfigValue();
	}

	public boolean isSelectionShown(final Player player) {
		return this.shown.containsKey(player.getUniqueId()) ? this.shown.get(player.getUniqueId()).booleanValue()
				: this.shouldShowSelection(player);
	}

	public boolean shouldShowSelection(final Player player) {
		return this.config.isEnabled(player) && (!this.config.isCheckForAxeEnabled()
				|| this.config.isCheckForAxeEnabled() && this.holdsSelectionItem(player));
	}

	public void showSelection(final Player player) {
		if (!player.hasPermission("wesv.use")) {
			return;
		}
		final Region region = this.worldEditHelper.getSelectedRegion(player);
		if (region != null && region.getArea() > this.config.getMaxSize()) {
			this.particleSender.setParticlesForPlayer(player, null);
			final UUID uniqueId = player.getUniqueId();
			if (this.lastSelectionTooLarge.containsKey(uniqueId)
					&& !this.lastSelectionTooLarge.get(uniqueId).booleanValue()) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + this.config.getLangSelectionSizeOf()
						+ this.config.getMaxSize() + this.config.getLangBlocks());
			}
			this.lastSelectionTooLarge.put(player.getUniqueId(), true);
		} else {
			this.lastSelectionTooLarge.put(player.getUniqueId(), false);
			this.particleSender.setParticlesForPlayer(player, this.shapeHelper.getLocationsFromRegion(region));
		}
		this.shown.put(player.getUniqueId(), true);
	}

	public void hideSelection(final Player player) {
		this.shown.put(player.getUniqueId(), false);
		this.particleSender.setParticlesForPlayer(player, null);
	}
}
