package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.event.VisualizationToggleEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@NullMarked
public class StorageManager {

    private final WorldEditSelectionVisualizer plugin;

    private @Nullable File playersFile;
    private @Nullable FileConfiguration playersConfig;
    private @Nullable ConfigurationSection playersSection;

    public StorageManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.playersFile = new File(this.plugin.getDataFolder(), "players.yml");
        this.playersConfig = YamlConfiguration.loadConfiguration(this.playersFile);

        ConfigurationSection section = this.playersConfig.getConfigurationSection("players");
        this.playersSection = section != null
                ? section : this.playersConfig.createSection("players");
    }

    public boolean isEnabled(Player player, SelectionType type) {
        String key = player.getUniqueId() + "." + type.getName();
        boolean defaultValue = this.plugin.getConfig()
                .getBoolean("default-enabled." + type.getName());

        if (this.playersSection == null || !this.plugin.getConfig().getBoolean("save-toggle")) {
            return defaultValue;
        }

        return this.playersSection.getBoolean(key, defaultValue);
    }

    public void setEnable(Player player, SelectionType type, boolean enable) {
        if (this.playersSection == null || this.playersConfig == null || this.playersFile == null) {
            throw new IllegalStateException("StorageManager is not initialized yet");
        }

        this.playersSection.set(player.getUniqueId() + "." + type.getName(), enable);

        Bukkit.getPluginManager().callEvent(new VisualizationToggleEvent(player, enable));

        try {
            this.playersConfig.save(this.playersFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while saving players.yml", e);
        }
    }
}
