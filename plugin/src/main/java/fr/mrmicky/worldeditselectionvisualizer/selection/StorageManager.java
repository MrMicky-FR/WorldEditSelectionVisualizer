package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.event.VisualizationToggleEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class StorageManager {

    private final WorldEditSelectionVisualizer plugin;

    private final File playersFile;
    private final FileConfiguration playersConfig;
    private final ConfigurationSection playersSection;

    public StorageManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        this.playersFile = new File(plugin.getDataFolder(), "players.yml");
        this.playersConfig = YamlConfiguration.loadConfiguration(this.playersFile);

        ConfigurationSection section = this.playersConfig.getConfigurationSection("players");
        this.playersSection = section != null
                ? section : this.playersConfig.createSection("players");
    }

    public boolean isEnabled(Player player, SelectionType type) {
        String key = player.getUniqueId() + "." + type.getName();
        boolean defaultValue = this.plugin.getConfig()
                .getBoolean("default-enabled." + type.getName());

        return this.playersSection.getBoolean(key, defaultValue);
    }

    public void setEnable(Player player, SelectionType type, boolean enable) {
        this.playersSection.set(player.getUniqueId() + "." + type.getName(), enable);

        Bukkit.getPluginManager().callEvent(new VisualizationToggleEvent(player, enable));

        save();
    }

    private void save() {
        try {
            this.playersConfig.save(this.playersFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while saving players.yml", e);
        }
    }
}
