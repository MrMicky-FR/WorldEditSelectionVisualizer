package fr.mrmicky.worldeditselectionvisualizer.listeners;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final WorldEditSelectionVisualizer plugin;

    public PlayerListener(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        this.plugin.loadPlayer(player);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();

        this.plugin.getOptionalPlayerData(player).ifPresent(data -> {
            this.plugin.updateHoldingSelectionItem(data);

            data.getEnabledVisualizations().forEach(PlayerSelection::resetSelection);
            this.plugin.getSelectionManager().updatePlayerVisualizations(data);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHeldItem(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());

        if (oldItem == null && newItem == null) {
            return;
        }

        if (oldItem != null && newItem != null && oldItem.getType() == newItem.getType()) {
            return;
        }

        Bukkit.getScheduler().runTask(this.plugin, () ->
                this.plugin.updateHoldingSelectionItem(this.plugin.getPlayerData(player)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        this.plugin.unloadPlayer(player);
    }
}
