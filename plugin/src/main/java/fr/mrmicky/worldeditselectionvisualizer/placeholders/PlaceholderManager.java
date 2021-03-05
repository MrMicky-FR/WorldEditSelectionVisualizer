package fr.mrmicky.worldeditselectionvisualizer.placeholders;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceholderManager {
    private final WorldEditSelectionVisualizer plugin;
    private PlaceholderExpansion placeholders = null;

    public PlaceholderManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    public void hook() {
        placeholders = new PlaceholderHandler(plugin);

        new BukkitRunnable(){
            @Override
            public void run() {
                placeholders.register();
            }
        }.runTask(plugin);
    }

    @SuppressWarnings("all")
    public void unhook() {
        if (placeholders != null) {
            Integer version = Integer.parseInt(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion().split("\\.")[2]);

            if (version > 5) {
                placeholders.unregister();
            } else {
                PlaceholderAPI.unregisterPlaceholderHook("wesv");
            }
        }
    }
}
