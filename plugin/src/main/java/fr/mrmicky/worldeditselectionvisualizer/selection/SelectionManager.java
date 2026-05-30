package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.*;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class SelectionManager implements Runnable {

    private final Map<Class<? extends Region>, ShapeProcessor<?>> shapeProcessors = new HashMap<>(8);

    private final WorldEditSelectionVisualizer plugin;
    private final WorldEditPlugin worldEditPlugin;

    public SelectionManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        if (worldEdit == null) {
            throw new IllegalStateException("WorldEdit plugin not found");
        }

        this.worldEditPlugin = worldEdit;
        this.shapeProcessors.put(CuboidRegion.class, new CuboidProcessor(this.plugin));
        this.shapeProcessors.put(Polygonal2DRegion.class, new Polygonal2DProcessor(this.plugin));
        this.shapeProcessors.put(EllipsoidRegion.class, new EllipsoidProcessor(this.plugin));
        this.shapeProcessors.put(CylinderRegion.class, new CylinderProcessor(this.plugin));
        this.shapeProcessors.put(ConvexPolyhedralRegion.class, new ConvexPolyhedralProcessor(this.plugin));
    }

    public void init() {
        int delay = this.plugin.getConfig().getInt("selection-update-interval");

        this.plugin.getScheduler().scheduleGlobalRepeating(this, 20, delay);
    }

    @Override
    public void run() {
        for (PlayerVisualizerData player : this.plugin.getPlayers()) {
            updatePlayerVisualizations(player);
        }
    }

    public void updatePlayerVisualizations(PlayerVisualizerData playerData) {
        for (SelectionType type : SelectionType.getValues()) {
            updatePlayerVisualization(playerData, type);
        }
    }

    public void updatePlayerVisualization(PlayerVisualizerData playerData, SelectionType type) {
        Player player = playerData.getPlayer();
        LocalSession session = getSession(player);
        PlayerSelection playerSelection = playerData.getSelection(type).orElse(null);

        if (playerSelection == null || session == null) {
            return;
        }

        ResolvedRegion resolved = resolveRegion(type, session, player);

        if (resolved == null) {
            playerSelection.resetSelection();
            return;
        }

        this.plugin.updateHoldingSelectionItem(playerData);

        RegionAdapter regionAdapter = this.plugin.getCompatibilityHelper().adaptRegion(resolved.region);
        RegionInfo regionInfo = regionAdapter.getRegionInfo();

        if (isUnchanged(playerSelection, regionInfo, resolved.origin)) {
            return;
        }

        if (!player.hasPermission("wesv.use")) {
            playerSelection.resetSelection(regionInfo);
            return;
        }

        GlobalSelectionConfig config = this.plugin.getSelectionConfig(type);

        if (isSelectionTooLarge(regionAdapter, config, player, playerSelection, regionInfo)) {
            return;
        }

        Event event = type == SelectionType.SELECTION
                ? new SelectionChangeEvent(player, resolved.region)
                : new ClipboardChangeEvent(player, resolved.region);
        Bukkit.getPluginManager().callEvent(event);

        ShapeProcessor<?> shapeProcessor = this.shapeProcessors.get(resolved.region.getClass());

        if (shapeProcessor == null) { // Unsupported selection type
            playerSelection.resetSelection(regionInfo);
            return;
        }

        SelectionPoints selection = shapeProcessor.processSelection(regionAdapter, config);
        playerSelection.updateSelection(selection, regionInfo, resolved.origin, config.getFadeDelay());
    }

    private @Nullable ResolvedRegion resolveRegion(SelectionType type, LocalSession session, Player player) {
        if (type != SelectionType.CLIPBOARD) {
            Region region = getSelectedRegion(session, player);
            return region != null ? new ResolvedRegion(region, Vector3d.ZERO) : null;
        }

        ClipboardHolder clipboardHolder = getClipboardHolder(session);

        if (clipboardHolder == null) {
            return null;
        }

        Clipboard clipboard = clipboardHolder.getClipboard();
        Transform transform = clipboardHolder.getTransform();
        Vector3d origin = this.plugin.getCompatibilityHelper().adaptClipboard(clipboard).getOrigin();
        Region region = clipboard.getRegion().clone();

        if (!transform.isIdentity()) {
            region = this.plugin.getCompatibilityHelper().adaptRegion(region).transform(transform, origin);
        }

        return new ResolvedRegion(region, origin);
    }

    private boolean isUnchanged(PlayerSelection playerSelection, RegionInfo regionInfo, Vector3d origin) {
        if (!regionInfo.equals(playerSelection.getLastSelectedRegion())) {
            return false;
        }

        SelectionPoints points = playerSelection.getSelectionPoints();

        return points == null || playerSelection.getOrigin().equals(origin);
    }

    private boolean isSelectionTooLarge(RegionAdapter regionAdapter, GlobalSelectionConfig config,
                                        Player player, PlayerSelection playerSelection, RegionInfo regionInfo) {
        long volume = Math.abs(regionAdapter.getVolume());

        if (volume <= config.getMaxSelectionSize()) {
            return false;
        }

        if (!playerSelection.isLastSelectionTooLarge()) {
            String message = this.plugin.getMessage("selection-too-large")
                    .replace("%blocks%", Integer.toString(config.getMaxSelectionSize()));
            this.plugin.getCompatibilityHelper().sendActionBar(player, message);
        }

        playerSelection.resetSelection(regionInfo);
        playerSelection.setLastSelectionTooLarge(true);

        return true;
    }

    private @Nullable LocalSession getSession(Player player) {
        try {
            return this.worldEditPlugin.getSession(player);
        } catch (Exception e) {
            // Sometimes after reloading, getSession creates errors with WorldEdit
            return null;
        }
    }

    private @Nullable Region getSelectedRegion(LocalSession session, Player player) {
        if (session.getSelectionWorld() == null) {
            return null;
        }

        if (!session.getSelectionWorld().getName().equals(player.getWorld().getName())) {
            return null;
        }

        RegionSelector selector = session.getRegionSelector(session.getSelectionWorld());

        if (!selector.isDefined()) {
            return null;
        }

        try {
            return selector.getRegion();
        } catch (IncompleteRegionException e) {
            this.plugin.getLogger().warning("Region still incomplete from " + selector.getClass().getName());
            return null;
        }
    }

    private @Nullable ClipboardHolder getClipboardHolder(LocalSession session) {
        try {
            return session.getClipboard();
        } catch (EmptyClipboardException e) {
            return null; // ignore, clipboard is empty
        }
    }

    private static final class ResolvedRegion {
        final Region region;
        final Vector3d origin;

        ResolvedRegion(Region region, Vector3d origin) {
            this.region = region;
            this.origin = origin;
        }
    }
}
