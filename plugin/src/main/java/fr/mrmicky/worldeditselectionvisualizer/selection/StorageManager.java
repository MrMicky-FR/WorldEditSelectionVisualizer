package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
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

        playersFile = new File(plugin.getDataFolder(), "players.yml");

        playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        ConfigurationSection section = playersConfig.getConfigurationSection("players");

        playersSection = section != null ? section : playersConfig.createSection("players");
    }

    public boolean isEnabled(Player player, SelectionType type) {
        return playersSection.getBoolean(player.getUniqueId() + "." + type.toString().toLowerCase(), true);
    }

    public void setEnable(Player player, SelectionType type, boolean enable) {
        playersSection.set(player.getUniqueId() + "." + type.toString().toLowerCase(), enable);

        save();
    }

    private void save() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while saving players.yml", e);
        }
    }
}
