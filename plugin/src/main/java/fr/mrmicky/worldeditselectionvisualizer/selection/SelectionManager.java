package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.event.ClipboardChangeEvent;
import fr.mrmicky.worldeditselectionvisualizer.event.SelectionChangeEvent;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.ConvexPolyhedralProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.CuboidProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.CylinderProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.EllipsoidProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.Polygonal2DProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.ShapeProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager extends BukkitRunnable {

    private final Map<Class<? extends Region>, ShapeProcessor<?>> shapeProcessors = new HashMap<>(8);

    private final WorldEditSelectionVisualizer plugin;
    private final WorldEditPlugin worldEditPlugin;

    public SelectionManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        registerShapeProcessors();

        worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        if (worldEditPlugin == null) {
            throw new IllegalStateException("WorldEditPlugin not found");
        }

        runTaskTimer(plugin, 20, plugin.getConfig().getInt("selection-update-interval"));
    }

    @Override
    public void run() {
        for (PlayerVisualizerData player : plugin.getPlayers()) {
            updatePlayerVisualizations(player);
        }
    }

    public void updatePlayerVisualizations(PlayerVisualizerData playerData) {
        for (SelectionType type : SelectionType.getValues()) {
            updatePlayerVisualization(playerData, type);
        }
    }

    public void updatePlayerVisualization(PlayerVisualizerData playerData, SelectionType type) { // TODO cleanup this
        Player player = playerData.getPlayer();
        LocalSession session;
        try {
            session = worldEditPlugin.getSession(player);
        } catch (Exception e) {
            // sometimes after reloading, getSession creates errors with WorldEdit
            return;
        }

        PlayerSelection playerSelection = playerData.getSelection(type).orElse(null);

        if (playerSelection == null || session == null) {
            return;
        }

        Vector3d origin = Vector3d.ZERO;
        Region region;

        if (type == SelectionType.CLIPBOARD) {
            ClipboardHolder clipboardHolder = getClipboardHolder(session);

            if (clipboardHolder == null) {
                playerSelection.resetSelection();
                return;
            }

            Clipboard clipboard = clipboardHolder.getClipboard();
            Transform transform = clipboardHolder.getTransform();

            origin = plugin.getCompatibilityHelper().adaptClipboard(clipboard).getOrigin();
            region = clipboard.getRegion().clone();

            if (!transform.isIdentity()) {
                region = plugin.getCompatibilityHelper().adaptRegion(region).transform(transform, origin);
            }
        } else {
            region = getSelectedRegion(session);
        }

        if (region == null) {
            playerSelection.resetSelection();
            return;
        }

        if (type == SelectionType.SELECTION && (region.getWorld() == null || !region.getWorld().getName().equals(player.getWorld().getName()))) {
            playerSelection.resetSelection();
            return;
        }

        RegionAdapter regionAdapter = plugin.getCompatibilityHelper().adaptRegion(region);
        RegionInfo regionInfo = regionAdapter.getRegionInfo();

        if (regionInfo.equals(playerSelection.getLastSelectedRegion())) {
            SelectionPoints points = playerSelection.getSelectionPoints();
            if (points == null || playerSelection.getOrigin().equals(origin)) {
                return;
            }
        }

        if (!player.hasPermission("wesv.use")) {
            playerSelection.resetSelection(regionInfo);
            return;
        }

        GlobalSelectionConfig config = plugin.getSelectionConfig(type);
        long volume = regionAdapter.getVolume();

        if (volume <= 0 || volume > config.getMaxSelectionSize()) {
            if (!playerSelection.isLastSelectionTooLarge()) {
                String message = plugin.getMessage("selection-too-large")
                        .replace("%blocks%", Integer.toString(config.getMaxSelectionSize()));
                plugin.getCompatibilityHelper().sendActionBar(player, message);
            }

            playerSelection.resetSelection(regionInfo);
            playerSelection.setLastSelectionTooLarge(true);
            return;
        }

        plugin.updateHoldingSelectionItem(playerData);

        Event event = (type == SelectionType.SELECTION)
                ? new SelectionChangeEvent(player, region) : new ClipboardChangeEvent(player, region);
        Bukkit.getPluginManager().callEvent(event);

        ShapeProcessor<?> shapeProcessor = shapeProcessors.get(region.getClass());

        if (shapeProcessor == null) { // Unsupported selection type
            playerSelection.resetSelection(regionInfo);
            return;
        }

        SelectionPoints selection = shapeProcessor.processSelection(regionAdapter, config);
        playerSelection.updateSelection(selection, regionInfo, origin, config.getFadeDelay());
    }

    @Nullable
    private Region getSelectedRegion(@Nullable LocalSession session) {
        if (session != null && session.getSelectionWorld() != null) {
            RegionSelector selector = session.getRegionSelector(session.getSelectionWorld());

            if (selector.isDefined()) {
                try {
                    return selector.getRegion();
                } catch (IncompleteRegionException e) {
                    plugin.getLogger().warning("Region still incomplete");
                }
            }
        }
        return null;
    }

    @Nullable
    private ClipboardHolder getClipboardHolder(@Nullable LocalSession session) {
        if (session != null) {
            try {
                return session.getClipboard();
            } catch (EmptyClipboardException e) {
                // ignore, clipboard is empty
            }
        }
        return null;
    }

    private void registerShapeProcessors() {
        shapeProcessors.put(CuboidRegion.class, new CuboidProcessor(plugin));
        shapeProcessors.put(Polygonal2DRegion.class, new Polygonal2DProcessor(plugin));
        shapeProcessors.put(EllipsoidRegion.class, new EllipsoidProcessor(plugin));
        shapeProcessors.put(CylinderRegion.class, new CylinderProcessor(plugin));
        shapeProcessors.put(ConvexPolyhedralRegion.class, new ConvexPolyhedralProcessor(plugin));
    }
}
