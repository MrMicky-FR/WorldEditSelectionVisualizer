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

    public WesvListener(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (plugin.isSelectionShown(player)) {
            plugin.showSelection(player);
        }
    }

    @EventHandler
    public void onPlayerItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getCustomConfig().isCheckForAxeEnabled() && plugin.getStorageManager().isEnabled(player)) {

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (plugin.isHoldingSelectionItem(player)) {
                    plugin.showSelection(player);
                } else {
                    plugin.hideSelection(player);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.removePlayer(event.getPlayer());
    }
}
