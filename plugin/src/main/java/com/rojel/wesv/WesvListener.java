package com.rojel.wesv;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WesvListener implements Listener {

    private final WorldEditSelectionVisualizer plugin;

    public WesvListener(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (plugin.isSelectionShown(player)) {
            plugin.showSelection(player);
        }
        if (plugin.isClipboardShown(player)) {
            plugin.showClipboard(player);
        }
    }

    @EventHandler
    public void onPlayerItemChange(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getCustomConfig().isCheckForAxeEnabled() && plugin.getStorageManager().isEnabled(player)) {

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (plugin.isHoldingSelectionItem(player)) {
                    plugin.showSelection(player);
                    plugin.showClipboard(player);
                } else {
                    plugin.hideSelection(player);
                    plugin.hideClipboard(player);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        plugin.removePlayer(event.getPlayer());
    }
}
