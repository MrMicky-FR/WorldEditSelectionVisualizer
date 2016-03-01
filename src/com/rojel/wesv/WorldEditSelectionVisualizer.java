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

public class WorldEditSelectionVisualizer
extends JavaPlugin
implements Listener {
    private Configuration config;
    private WorldEditHelper worldEditHelper;
    private ProtocolLibHelper protocolLibHelper;
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
        this.protocolLibHelper = new ProtocolLibHelper(this, this.config);
        this.shapeHelper = new ShapeHelper(this.config);
        this.particleSender = new ParticleSender(this, this.config, this.protocolLibHelper);
        new CustomMetrics(this, this.config).initMetrics();
        this.getServer().getPluginManager().registerEvents(this, this);
        if (this.config.useProtocolLib() && !this.protocolLibHelper.isProtocolLibInstalled()) {
            this.getLogger().info("ProtocolLib is enabled in the config but not installed. You need to install ProtocolLib if you want to use certain features.");
        }
        if (this.config.particleDistance() > 16 && !this.protocolLibHelper.canUseProtocolLib()) {
            this.getLogger().info("Particle distances > 16 only work with ProtocolLib. Set \"useProtocolLib\" in the config to \"true\" and/or install ProtocolLib.");
        }
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (label.equals("wesv")) {
                if (player.hasPermission("wesv.toggle")) {
                    boolean isEnabled = !this.config.isEnabled(player);
                    this.config.setEnabled(player, isEnabled);
                    if (isEnabled) {
                        player.sendMessage(ChatColor.GREEN + "Your visualizer has been enabled.");
                        if (this.shouldShowSelection(player)) {
                            this.showSelection(player);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Your visualizer has been disabled.");
                        this.hideSelection(player);
                    }
                }
                return true;
            }
        } else {
            sender.sendMessage("Only a player can toggle his visualizer.");
            return true;
        }
        return false;
    }

    @EventHandler
    private void onWorldEditSelectionChange(WorldEditSelectionChangeEvent event) {
        Player player = event.getPlayer();
        if (this.isSelectionShown(player)) {
            this.showSelection(player);
        }
    }

    @EventHandler
    private void onItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (this.config.checkForAxe() && this.config.isEnabled(player)) {
            ItemStack item = player.getInventory().getItem(event.getNewSlot());
            if (item != null && item.getType() == this.config.selectionItem()) {
                this.showSelection(player);
            } else {
                this.hideSelection(player);
            }
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.shown.remove(event.getPlayer().getUniqueId());
        this.lastSelectionTooLarge.remove(event.getPlayer().getUniqueId());
    }

    @SuppressWarnings("deprecation")
	public boolean holdsSelectionItem(Player player) {
        ItemStack item = player.getItemInHand();
        return item != null && item.getType() == this.config.selectionItem();
    }

    public boolean isSelectionShown(Player player) {
        return this.shown.containsKey(player.getUniqueId()) ? this.shown.get(player.getUniqueId()).booleanValue() : this.shouldShowSelection(player);
    }

    public boolean shouldShowSelection(Player player) {
        return this.config.isEnabled(player) && (!this.config.checkForAxe() || this.config.checkForAxe() && this.holdsSelectionItem(player));
    }

    public void showSelection(Player player) {
        if (!player.hasPermission("wesv.use")) {
            return;
        }
        Region region = this.worldEditHelper.getSelectedRegion(player);
        if (region != null && region.getArea() > this.config.maxSize()) {
            this.particleSender.setParticlesForPlayer(player, null);
            UUID uniqueId = player.getUniqueId();
            if (this.lastSelectionTooLarge.containsKey(uniqueId) && !this.lastSelectionTooLarge.get(uniqueId).booleanValue()) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "The visualizer only works with selections up to a size of " + this.config.maxSize() + " blocks.");
            }
            this.lastSelectionTooLarge.put(player.getUniqueId(), true);
        } else {
            this.lastSelectionTooLarge.put(player.getUniqueId(), false);
            this.particleSender.setParticlesForPlayer(player, this.shapeHelper.getLocationsFromRegion(region));
        }
        this.shown.put(player.getUniqueId(), true);
    }

    public void hideSelection(Player player) {
        this.shown.put(player.getUniqueId(), false);
        this.particleSender.setParticlesForPlayer(player, null);
    }
}

