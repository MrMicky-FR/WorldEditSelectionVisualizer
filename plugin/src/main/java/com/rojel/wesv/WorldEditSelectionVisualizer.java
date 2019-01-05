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
import java.util.*;

public class WorldEditSelectionVisualizer extends JavaPlugin {

    private Configuration config;
    private StorageManager storageManager;
    private WorldEditHelper worldEditHelper;
    public ShapeHelper shapeHelper;
    private boolean faweEnabled;
    private boolean legacyWorldEdit;

    private final Set<UUID> shown = new HashSet<>();
    private final Set<UUID> clipboardShown = new HashSet<>();
    private final Set<UUID> lastSelectionTooLarge = new HashSet<>();
    private final Set<UUID> lastClipboardTooLarge = new HashSet<>();
    private final Map<UUID, Region> lastSelectedRegions = new HashMap<>();
    private final Map<UUID, Region> lastClipboardRegions = new HashMap<>();
    private final Map<UUID, Integer> fadeOutTasks = new HashMap<>();
    private final Map<UUID, Integer> fadeOutClipboardTasks = new HashMap<>();
    private final Map<UUID, Collection<ImmutableVector>> playerParticleMap = new HashMap<>();
    private final Map<UUID, Collection<ImmutableVector>> playerClipboardParticleMap = new HashMap<>();

    @Override
    public void onEnable() {
        config = new Configuration(this);
        storageManager = new StorageManager(this);

        config.load();

        worldEditHelper = new WorldEditHelper(this);
        shapeHelper = new ShapeHelper(this);

        new ParticleTask(this);
        new ClipboardParticleTask(this);

        getServer().getPluginManager().registerEvents(new WesvListener(this), this);
        getCommand("wesv").setExecutor(new CommandWesv(this));

        faweEnabled = getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null;

        MetricsUtils.register(this);

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

        for (final Player player : getServer().getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    public boolean isHoldingSelectionItem(final Player player) {
        return worldEditHelper.isHoldingSelectionItem(player);
    }

    public boolean isSelectionShown(final Player player) {
        return shown.contains(player.getUniqueId()) && shouldShowSelection(player);
    }

    public boolean isClipboardShown(final Player player) {
        return clipboardShown.contains(player.getUniqueId()) && shouldShowSelection(player);
    }

    public boolean shouldShowSelection(final Player player) {
        return storageManager.isEnabled(player) && (!config.isCheckForAxeEnabled() || isHoldingSelectionItem(player));
    }

    public void showSelection(final Player player) {
        if (!player.hasPermission("wesv.use")) {
            return;
        }

        final Region region = worldEditHelper.getSelectedRegion(player);
        final UUID uuid = player.getUniqueId();

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

    public void showClipboard(final Player player) {
        if (!player.hasPermission("wesv.use")) {
            return;
        }

        final Region region = worldEditHelper.getClipboardRegion(player);
        final UUID uuid = player.getUniqueId();

        if (region != null && region.getArea() > config.getMaxSize()) {
            setClipboardParticlesForPlayer(player, null);

            if (!lastClipboardTooLarge.contains(uuid)) {
                player.sendMessage(config.getLangMaxSelection().replace("%blocks%", Integer.toString(config.getMaxSize())));
                lastClipboardTooLarge.add(uuid);
            }
        } else {
            lastClipboardTooLarge.remove(player.getUniqueId());

            if (region != null && region.getWorld() != null && region.getWorld().getName().equals(player.getWorld().getName())) {
                setClipboardParticlesForPlayer(player, shapeHelper.getVectorsFromRegion(wrapRegion(region)));
            } else {
                setClipboardParticlesForPlayer(player, null);
            }
        }
        clipboardShown.add(player.getUniqueId());
    }

    public void hideSelection(final Player player) {
        shown.remove(player.getUniqueId());
        playerParticleMap.remove(player.getUniqueId());
        cancelAndRemoveFadeOutTask(player.getUniqueId());
    }

    public void hideClipboard(final Player player) {
        clipboardShown.remove(player.getUniqueId());
        playerClipboardParticleMap.remove(player.getUniqueId());
        cancelAndRemoveFadeOutClipboardTask(player.getUniqueId());
    }

    public void setParticlesForPlayer(final Player player, final Collection<ImmutableVector> vectors) {
        cancelAndRemoveFadeOutTask(player.getUniqueId());

        if (vectors == null || vectors.isEmpty()) {
            playerParticleMap.remove(player.getUniqueId());
        } else {
            playerParticleMap.put(player.getUniqueId(), vectors);

            final int fade = config.getParticleFadeDelay();

            if (fade > 0) {
                final int id = getServer().getScheduler().runTaskLater(this, () -> {

                    fadeOutTasks.remove(player.getUniqueId());
                    playerParticleMap.remove(player.getUniqueId());
                }, fade).getTaskId();

                fadeOutTasks.put(player.getUniqueId(), id);
            }
        }
    }

    public void setClipboardParticlesForPlayer(final Player player, final Collection<ImmutableVector> vectors) {
        cancelAndRemoveFadeOutClipboardTask(player.getUniqueId());

        if (vectors == null || vectors.isEmpty()) {
            playerClipboardParticleMap.remove(player.getUniqueId());
        } else {
            playerClipboardParticleMap.put(player.getUniqueId(), vectors);

            final int fade = config.getParticleFadeDelay();

            if (fade > 0) {
                final int id = getServer().getScheduler().runTaskLater(this, () -> {

                    fadeOutClipboardTasks.remove(player.getUniqueId());
                    playerClipboardParticleMap.remove(player.getUniqueId());
                }, fade).getTaskId();

                fadeOutClipboardTasks.put(player.getUniqueId(), id);
            }
        }
    }

    private void cancelAndRemoveFadeOutTask(final UUID uuid) {
        if (fadeOutTasks.containsKey(uuid)) {
            getServer().getScheduler().cancelTask(fadeOutTasks.get(uuid));
            fadeOutTasks.remove(uuid);
        }
    }

    private void cancelAndRemoveFadeOutClipboardTask(final UUID uuid) {
        if (fadeOutClipboardTasks.containsKey(uuid)) {
            getServer().getScheduler().cancelTask(fadeOutClipboardTasks.get(uuid));
            fadeOutClipboardTasks.remove(uuid);
        }
    }

    public void addPlayer(final Player player) {
        if (shouldShowSelection(player)) {
            showSelection(player);
            showClipboard(player);
        }
    }

    public void removePlayer(final Player player) {
        final UUID uuid = player.getUniqueId();
        shown.remove(uuid);
        clipboardShown.remove(uuid);
        lastSelectionTooLarge.remove(uuid);
        lastSelectedRegions.remove(uuid);
        lastClipboardRegions.remove(uuid);
        playerParticleMap.remove(uuid);
        playerClipboardParticleMap.remove(uuid);

        cancelAndRemoveFadeOutTask(uuid);
        cancelAndRemoveFadeOutClipboardTask(uuid);
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

    public Map<UUID, Region> getLastClipboardRegions() {
        return lastClipboardRegions;
    }

    public Map<UUID, Collection<ImmutableVector>> getPlayerParticleMap() {
        return playerParticleMap;
    }

    public Map<UUID, Collection<ImmutableVector>> getPlayerClipboardParticleMap() {
        return playerClipboardParticleMap;
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
            // Don't display an error
        }
    }
}
