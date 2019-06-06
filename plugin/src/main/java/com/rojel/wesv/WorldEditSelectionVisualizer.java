package com.rojel.wesv;

import com.rojel.wesv.v6.RegionWrapper6;
import com.rojel.wesv.v7.RegionWrapper7;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class WorldEditSelectionVisualizer extends JavaPlugin {

    private Configuration config;
    private StorageManager storageManager;
    private WorldEditHelper worldEditHelper;
    private ShapeHelper shapeHelper;
    private boolean faweEnabled;
    private boolean legacyWorldEdit;

    private final Set<UUID> shown = new HashSet<>();
    private final Set<UUID> lastSelectionTooLarge = new HashSet<>();
    private final Map<UUID, Region> lastSelectedRegions = new HashMap<>();
    private final Map<UUID, Integer> fadeOutTasks = new HashMap<>();
    private final Map<UUID, Collection<ImmutableVector>> playerParticleMap = new HashMap<>();

    @Override
    public void onEnable() {
        config = new Configuration(this);
        storageManager = new StorageManager(this);

        config.load();

        worldEditHelper = new WorldEditHelper(this);
        shapeHelper = new ShapeHelper(this);

        new ParticleTask(this);

        getServer().getPluginManager().registerEvents(new WesvListener(this), this);
        getCommand("wesv").setExecutor(new CommandWesv(this));

        faweEnabled = getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null;

        try {
            Class.forName("com.sk89q.worldedit.math.Vector3");
            legacyWorldEdit = false;

            getLogger().info("Using WorldEdit 7 API");
        } catch (ClassNotFoundException e) {
            legacyWorldEdit = true;
            getLogger().info("Using WorldEdit 6 API");
        }

        if (faweEnabled) {
            getLogger().info("FastAsyncWorldEdit support enabled");
        }

        if (config.isUpdateCheckerEnabled()) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdate);
        }

        getServer().getScheduler().runTask(this, () -> getServer().getOnlinePlayers().forEach(this::addPlayer));

        try {
            // bStats use Gson, but Gson is not with Spigot < 1.8
            Class.forName("com.google.gson.Gson");

            MetricsUtils.register(this);
        } catch (ClassNotFoundException e) {
            // disable metrics
        }
    }

    public boolean isHoldingSelectionItem(Player player) {
        return worldEditHelper.isHoldingSelectionItem(player);
    }

    public boolean isSelectionShown(Player player) {
        return shown.contains(player.getUniqueId()) && shouldShowSelection(player);
    }

    public boolean shouldShowSelection(Player player) {
        return storageManager.isEnabled(player) && (!config.isCheckForAxeEnabled() || isHoldingSelectionItem(player));
    }

    public void showSelection(Player player) {
        if (!player.hasPermission("wesv.use")) {
            return;
        }

        Region region = worldEditHelper.getSelectedRegion(player);
        UUID uuid = player.getUniqueId();

        if (region != null && region.getArea() > config.getMaxSize()) {
            setParticlesForPlayer(player, null);

            if (!lastSelectionTooLarge.contains(uuid)) {
                player.sendMessage(config.getLangMaxSelection().replace("%blocks%", Integer.toString(config.getMaxSize())));
                lastSelectionTooLarge.add(uuid);
            }
        } else {
            lastSelectionTooLarge.remove(player.getUniqueId());

            if (region != null && region.getWorld() != null && region.getWorld().getName().equals(player.getWorld().getName())) {
                setParticlesForPlayer(player, shapeHelper.getVectorsFromRegion(wrapRegion(region)));
            } else {
                setParticlesForPlayer(player, null);
            }
        }
        shown.add(player.getUniqueId());
    }

    public void hideSelection(Player player) {
        shown.remove(player.getUniqueId());
        playerParticleMap.remove(player.getUniqueId());
        cancelAndRemoveFadeOutTask(player.getUniqueId());
    }

    public void setParticlesForPlayer(Player player, Collection<ImmutableVector> vectors) {
        cancelAndRemoveFadeOutTask(player.getUniqueId());

        if (vectors == null || vectors.isEmpty()) {
            playerParticleMap.remove(player.getUniqueId());
        } else {
            playerParticleMap.put(player.getUniqueId(), vectors);

            int fade = config.getParticleFadeDelay();

            if (fade > 0) {
                int id = getServer().getScheduler().runTaskLater(this, () -> {

                    fadeOutTasks.remove(player.getUniqueId());
                    playerParticleMap.remove(player.getUniqueId());
                }, fade).getTaskId();

                fadeOutTasks.put(player.getUniqueId(), id);
            }
        }
    }

    private void cancelAndRemoveFadeOutTask(UUID uuid) {
        if (fadeOutTasks.containsKey(uuid)) {
            getServer().getScheduler().cancelTask(fadeOutTasks.get(uuid));
            fadeOutTasks.remove(uuid);
        }
    }

    public void addPlayer(Player player) {
        if (shouldShowSelection(player)) {
            showSelection(player);
        }
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        shown.remove(uuid);
        lastSelectionTooLarge.remove(uuid);
        lastSelectedRegions.remove(uuid);
        playerParticleMap.remove(uuid);

        cancelAndRemoveFadeOutTask(uuid);
    }

    public RegionWrapper wrapRegion(Region region) {
        return legacyWorldEdit ? new RegionWrapper6(region) : new RegionWrapper7(region);
    }

    public boolean isFaweEnabled() {
        return faweEnabled;
    }

    public Configuration getCustomConfig() {
        return config;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public Map<UUID, Region> getLastSelectedRegions() {
        return lastSelectedRegions;
    }

    public Map<UUID, Collection<ImmutableVector>> getPlayerParticleMap() {
        return playerParticleMap;
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
