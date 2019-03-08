package com.rojel.wesv;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author MrMicky
 */
public class StorageManager {

    private final WorldEditSelectionVisualizer plugin;

    private final Set<UUID> disabledPlayers = new HashSet<>();

    private final File playersFile;
    private FileConfiguration playerConfig;

    public StorageManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        playersFile = new File(plugin.getDataFolder(), "players.yml");

        playerConfig = YamlConfiguration.loadConfiguration(playersFile);

        playerConfig.getStringList("disabled-players").stream()
                .map(this::parseUUID)
                .filter(Objects::nonNull)
                .forEach(disabledPlayers::add);
    }

    public boolean isEnabled(Player p) {
        return !disabledPlayers.contains(p.getUniqueId());
    }

    public void setEnable(Player p, boolean enable) {
        if (enable) {
            disabledPlayers.remove(p.getUniqueId());
        } else {
            disabledPlayers.add(p.getUniqueId());
        }

        save();
    }

    public void save() {
        playerConfig.set("disabled-players", disabledPlayers.stream().map(UUID::toString).collect(Collectors.toList()));

        try {
            playerConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while saving players.yml", e);
        }
    }

    public Set<UUID> getDisabledPlayers() {
        return disabledPlayers;
    }

    private UUID parseUUID(String s) {
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
