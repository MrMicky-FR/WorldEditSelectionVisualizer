package fr.mrmicky.worldeditselectionvisualizer;

import fr.mrmicky.worldeditselectionvisualizer.commands.CommandWesv;
import fr.mrmicky.worldeditselectionvisualizer.compat.CompatibilityHelper;
import fr.mrmicky.worldeditselectionvisualizer.config.ConfigurationHelper;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.display.ParticlesTask;
import fr.mrmicky.worldeditselectionvisualizer.display.PositionBlockTask;
import fr.mrmicky.worldeditselectionvisualizer.listeners.PlayerListener;
import fr.mrmicky.worldeditselectionvisualizer.metrics.WesvMetrics;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerInfos;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import fr.mrmicky.worldeditselectionvisualizer.selection.StorageManager;
import fr.mrmicky.worldeditselectionvisualizer.selection.WorldEditHelper;
import fr.mrmicky.worldeditselectionvisualizer.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class WorldEditSelectionVisualizer extends JavaPlugin {

    private final Map<UUID, PlayerVisualizerInfos> players = new HashMap<>();
    private final Map<SelectionType, GlobalSelectionConfig> configurations = new EnumMap<>(SelectionType.class);

    private final Set<BukkitTask> visualizationTasks = new HashSet<>();

    private WorldEditHelper worldEditHelper;
    private StorageManager storageManager;
    private ConfigurationHelper configurationHelper;
    private CompatibilityHelper compatibilityHelper;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().get("lang") != null) {
            // Rename the config from WorldEditSelectionVisualizer v1.x to prevent any issues
            File configFile = new File(getDataFolder(), "config.yml");
            File configBackupFile = new File(getDataFolder(), "config-old.yml");

            configFile.renameTo(configBackupFile);
            saveDefaultConfig();
            reloadConfig();
        }

        compatibilityHelper = new CompatibilityHelper(this);
        storageManager = new StorageManager(this);
        configurationHelper = new ConfigurationHelper(this);
        worldEditHelper = new WorldEditHelper(this);

        getCommand("worldeditselectionvisualizer").setExecutor(new CommandWesv(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        loadConfig();

        getServer().getOnlinePlayers().forEach(this::loadPlayer);

        if (getConfig().getBoolean("check-updates")) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdate);
        }

        WesvMetrics.register(this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (configurationHelper != null) {
            loadConfig();
        }
    }

    private void loadConfig() {
        visualizationTasks.forEach(BukkitTask::cancel);
        visualizationTasks.clear();

        for (SelectionType type : SelectionType.values()) {
            GlobalSelectionConfig config = configurationHelper.loadGlobalSelectionConfig(type);

            configurations.put(type, config);

            visualizationTasks.add(new ParticlesTask(this, type, true, config.primary()).start());
            visualizationTasks.add(new ParticlesTask(this, type, false, config.secondary()).start());
        }

        GlobalSelectionConfig config = configurationHelper.loadGlobalSelectionConfig(SelectionType.SELECTION);
        BukkitTask positionBlockBukkitTask = (new PositionBlockTask(this, config.positionBlock()).start());
        if (positionBlockBukkitTask != null)
            visualizationTasks.add(positionBlockBukkitTask);
    }

    public void updateHoldingSelectionItem(PlayerVisualizerInfos playerInfos) {
        playerInfos.setHoldingSelectionItem(compatibilityHelper.isHoldingSelectionItem(playerInfos.getPlayer()));
    }

    public void loadPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerVisualizerInfos playerInfos = new PlayerVisualizerInfos(player);

        for (SelectionType type : SelectionType.values()) {
            boolean enable = !getConfig().getBoolean("save-toggle") || storageManager.isEnabled(player, type);
            playerInfos.toggleSelectionVisibility(type, enable);
        }

        updateHoldingSelectionItem(playerInfos);

        players.put(player.getUniqueId(), playerInfos);
    }

    public void unloadPlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    @NotNull
    public PlayerVisualizerInfos getPlayerInfos(Player player) {
        PlayerVisualizerInfos playerInfos = players.get(player.getUniqueId());

        if (playerInfos == null) {
            throw new IllegalStateException("No player infos loaded for " + player.getName());
        }

        return playerInfos;
    }

    @NotNull
    public Optional<PlayerVisualizerInfos> getPlayerInfosSafe(Player player) {
        return Optional.ofNullable(players.get(player.getUniqueId()));
    }

    public Map<UUID, PlayerVisualizerInfos> getPlayers() {
        return players;
    }

    public WorldEditHelper getWorldEditHelper() {
        return worldEditHelper;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public CompatibilityHelper getCompatibilityHelper() {
        return compatibilityHelper;
    }

    public GlobalSelectionConfig getSelectionConfig(SelectionType selectionType) {
        return configurations.get(selectionType);
    }

    public String getMessage(String path) {
        return ChatUtils.color(getConfig().getString("messages." + path));
    }

    private void checkUpdate() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=17311");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String lastVersion = reader.readLine();
                if (!getDescription().getVersion().equalsIgnoreCase(lastVersion)) {
                    getLogger().warning("A new version is available ! Last version is " + lastVersion + " and you are on " + getDescription().getVersion());
                    getLogger().warning("You can download it on: " + getDescription().getWebsite());
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
