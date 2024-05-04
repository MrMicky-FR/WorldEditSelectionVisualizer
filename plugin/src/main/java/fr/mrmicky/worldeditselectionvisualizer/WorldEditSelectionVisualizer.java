package fr.mrmicky.worldeditselectionvisualizer;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.commands.CommandWesv;
import fr.mrmicky.worldeditselectionvisualizer.compat.CompatibilityHelper;
import fr.mrmicky.worldeditselectionvisualizer.config.ConfigurationManager;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import fr.mrmicky.worldeditselectionvisualizer.display.ParticlesTask;
import fr.mrmicky.worldeditselectionvisualizer.listeners.PlayerListener;
import fr.mrmicky.worldeditselectionvisualizer.metrics.WesvMetrics;
import fr.mrmicky.worldeditselectionvisualizer.placeholders.PlaceholderAPIExpansion;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionManager;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import fr.mrmicky.worldeditselectionvisualizer.selection.StorageManager;
import fr.mrmicky.worldeditselectionvisualizer.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class WorldEditSelectionVisualizer extends JavaPlugin {

    private final Map<SelectionType, GlobalSelectionConfig> configurations = new EnumMap<>(SelectionType.class);
    private final Map<UUID, PlayerVisualizerData> players = new HashMap<>();
    private final List<BukkitTask> particlesTasks = new ArrayList<>(6);

    private SelectionManager selectionManager;
    private StorageManager storageManager;
    private ConfigurationManager configurationManager;
    private CompatibilityHelper compatibilityHelper;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        migrateV1Config();

        this.compatibilityHelper = new CompatibilityHelper(this);
        this.storageManager = new StorageManager(this);
        this.configurationManager = new ConfigurationManager(this);
        this.selectionManager = new SelectionManager(this);

        if (this.compatibilityHelper.getWorldEditVersion() == 7) {
            try {
                Region.class.getMethod("getVolume");
            } catch (NoSuchMethodException e) {
                getLogger().severe("**********************************");
                getLogger().severe("You are using an unsupported WorldEdit version (7.0.x or 7.1.x)!");
                getLogger().severe("WorldEditSelection visualizer doesn't works with outdated WorldEdit version.");
                getLogger().severe("You can download the latest WorldEdit version here: https://dev.bukkit.org/projects/worldedit/files");
                getLogger().severe("**********************************");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        getCommand("worldeditselectionvisualizer").setExecutor(new CommandWesv(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        loadConfig();

        getServer().getOnlinePlayers().forEach(this::loadPlayer);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion(this).registerExpansion();
        }

        if (getConfig().getBoolean("check-updates")) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdate);
        }

        WesvMetrics.register(this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (this.configurationManager != null) {
            loadConfig();
        }
    }

    private void loadConfig() {
        this.particlesTasks.forEach(BukkitTask::cancel);
        this.particlesTasks.clear();

        for (SelectionType type : SelectionType.values()) {
            GlobalSelectionConfig config = this.configurationManager.loadGlobalSelectionConfig(type);

            this.configurations.put(type, config);

            for (DisplayType display : DisplayType.values()) {
                ParticlesTask task = new ParticlesTask(this, type, display, config.byType(display));
                this.particlesTasks.add(task.start());
            }
        }
    }

    public void updateHoldingSelectionItem(PlayerVisualizerData playerData) {
        playerData.setHoldingSelectionItem(this.compatibilityHelper.isHoldingSelectionItem(playerData.getPlayer()));
    }

    public void loadPlayer(Player player) {
        if (this.players.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerVisualizerData playerData = new PlayerVisualizerData(player);

        for (SelectionType type : SelectionType.values()) {
            boolean enable = !getConfig().getBoolean("save-toggle") || this.storageManager.isEnabled(player, type);
            playerData.toggleSelectionVisibility(type, enable);
        }

        updateHoldingSelectionItem(playerData);

        this.players.put(player.getUniqueId(), playerData);
    }

    public void unloadPlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public @NotNull PlayerVisualizerData getPlayerData(Player player) {
        PlayerVisualizerData playerData = this.players.get(player.getUniqueId());

        if (playerData == null) {
            throw new IllegalStateException("No player data loaded for " + player.getName());
        }

        return playerData;
    }

    public @NotNull Optional<PlayerVisualizerData> getOptionalPlayerData(Player player) {
        return Optional.ofNullable(this.players.get(player.getUniqueId()));
    }

    public @NotNull Collection<PlayerVisualizerData> getPlayers() {
        return this.players.values();
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public StorageManager getStorageManager() {
        return this.storageManager;
    }

    public CompatibilityHelper getCompatibilityHelper() {
        return this.compatibilityHelper;
    }

    public GlobalSelectionConfig getSelectionConfig(SelectionType selectionType) {
        return this.configurations.get(selectionType);
    }

    public String getMessage(String path) {
        return ChatUtils.color(getConfig().getString("messages." + path));
    }

    private void checkUpdate() {
        try {
            URL url = URI.create("https://api.spigotmc.org/legacy/update.php?resource=17311").toURL();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String lastVersion = reader.readLine();
                if (!getDescription().getVersion().equalsIgnoreCase(lastVersion)) {
                    getLogger().warning("A new version is available! Last version is " + lastVersion + " and you are on " + getDescription().getVersion());
                    getLogger().warning("You can download it on " + getDescription().getWebsite());
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private void migrateV1Config() {
        if (getConfig().get("lang") == null) {
            return;
        }

        // Rename the config from WorldEditSelectionVisualizer v1.x to prevent any issues
        File configFile = new File(getDataFolder(), "config.yml");
        File configBackupFile = new File(getDataFolder(), "config-old.yml");

        if (!configFile.renameTo(configBackupFile)) {
            getLogger().warning("Unable to rename old config.yml file.");
        }

        saveDefaultConfig();
        reloadConfig();
    }
}
