package fr.mrmicky.worldeditselectionvisualizer;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.commands.CommandWesv;
import fr.mrmicky.worldeditselectionvisualizer.compat.CompatibilityHelper;
import fr.mrmicky.worldeditselectionvisualizer.compat.ScheduledTask;
import fr.mrmicky.worldeditselectionvisualizer.compat.SchedulerAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.ConfigurationManager;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import fr.mrmicky.worldeditselectionvisualizer.display.ParticlesTask;
import fr.mrmicky.worldeditselectionvisualizer.listeners.PlayerListener;
import fr.mrmicky.worldeditselectionvisualizer.placeholders.PlaceholderAPIExpansion;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionManager;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import fr.mrmicky.worldeditselectionvisualizer.selection.StorageManager;
import fr.mrmicky.worldeditselectionvisualizer.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class WorldEditSelectionVisualizer extends JavaPlugin {

    private final Map<SelectionType, GlobalSelectionConfig> configurations = new EnumMap<>(SelectionType.class);
    private final Map<UUID, PlayerVisualizerData> players = new ConcurrentHashMap<>();
    private final List<ScheduledTask> particlesTasks = new ArrayList<>(6);

    private final SelectionManager selectionManager = new SelectionManager(this);
    private final StorageManager storageManager = new StorageManager(this);
    private final ConfigurationManager configurationManager = new ConfigurationManager(this);
    private final CompatibilityHelper compatibilityHelper = new CompatibilityHelper(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.compatibilityHelper.init();
        this.storageManager.init();
        this.selectionManager.init();

        getLogger().info("Using WorldEdit " + this.compatibilityHelper.getWorldEditVersion() + " api");
        getLogger().info("Using " + this.getScheduler().name() + " scheduler");

        if (this.compatibilityHelper.getWorldEditVersion() == 7) {
            try {
                Region.class.getMethod("getVolume");
            } catch (NoSuchMethodException e) {
                getLogger().severe("You are using an unsupported WorldEdit version (7.0.x or 7.1.x)");
                getLogger().severe("Please update WorldEdit: https://dev.bukkit.org/projects/worldedit/files");
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
            getScheduler().executeAsync(this::checkUpdate);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        loadConfig();

        this.compatibilityHelper.init();
    }

    private void loadConfig() {
        this.particlesTasks.forEach(ScheduledTask::cancel);
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
        PlayerVisualizerData playerData = new PlayerVisualizerData(player);

        for (SelectionType type : SelectionType.values()) {
            boolean enable = !getConfig().getBoolean("save-toggle") || this.storageManager.isEnabled(player, type);
            playerData.toggleSelectionVisibility(type, enable);
        }

        updateHoldingSelectionItem(playerData);

        this.players.putIfAbsent(player.getUniqueId(), playerData);
    }

    public void unloadPlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public PlayerVisualizerData getPlayerData(Player player) {
        PlayerVisualizerData playerData = this.players.get(player.getUniqueId());

        if (playerData == null) {
            throw new IllegalStateException("No player data loaded for " + player.getName());
        }

        return playerData;
    }

    public Optional<PlayerVisualizerData> getOptionalPlayerData(Player player) {
        return Optional.ofNullable(this.players.get(player.getUniqueId()));
    }

    public Collection<PlayerVisualizerData> getPlayers() {
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

    public SchedulerAdapter getScheduler() {
        return this.compatibilityHelper.getScheduler();
    }

    @SuppressWarnings("deprecation") // Ignore Paper-specific deprecations (for compatibility)
    public String getPluginVersion() {
        return getDescription().getVersion();
    }

    private void checkUpdate() {
        try {
            URL url = URI.create("https://api.spigotmc.org/legacy/update.php?resource=17311").toURL();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String lastVersion = reader.readLine();
                if (!getPluginVersion().equalsIgnoreCase(lastVersion)) {
                    getLogger().warning("A new version is available. Last version is " + lastVersion + " and you are on " + getPluginVersion());
                    getLogger().warning("You can download it on https://www.spigotmc.org/resources/worldeditselectionvisualizer.17311/");
                }
            }
        } catch (IOException e) {
            getLogger().warning("Unable to check for updates: " + e.getMessage());
        }
    }
}
