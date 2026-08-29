package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
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

    public synchronized void setEnabled(Player player, SelectionType type, boolean enabled) {
        if (this.playersSection == null) {
            throw new IllegalStateException("StorageManager is not initialized yet");
        }

        String uuid = player.getUniqueId().toString();
        boolean defaultValue = this.plugin.getConfig().getBoolean("default-enabled." + type.getName());

        ConfigurationSection playerSection = this.playersSection.getConfigurationSection(uuid);

        if (playerSection == null) {
            playerSection = this.playersSection.createSection(uuid);
        }

        playerSection.set(type.getName(), enabled != defaultValue ? enabled : null);

        if (playerSection.getKeys(false).isEmpty()) {
            this.playersSection.set(uuid, null);
        }

        this.plugin.getScheduler().executeAsync(this::save);
    }

    private synchronized void save() {
        FileConfiguration config = this.playersConfig;
        File file = this.playersFile;

        if (config == null || file == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while saving players.yml", e);
        }
    }
}
