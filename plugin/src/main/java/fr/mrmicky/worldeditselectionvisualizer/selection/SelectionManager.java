package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.ScheduledTask;
import fr.mrmicky.worldeditselectionvisualizer.config.VisualizationConfig;
import fr.mrmicky.worldeditselectionvisualizer.event.ClipboardChangeEvent;
import fr.mrmicky.worldeditselectionvisualizer.event.SelectionChangeEvent;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.shapes.*;
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

    private @Nullable ScheduledTask task;

    public SelectionManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        if (worldEdit == null) {
            throw new IllegalStateException("WorldEdit plugin not found");
        }

        this.worldEditPlugin = worldEdit;
        this.shapeProcessors.put(CuboidRegion.class, new CuboidProcessor());
        this.shapeProcessors.put(Polygonal2DRegion.class, new Polygonal2DProcessor());
        this.shapeProcessors.put(EllipsoidRegion.class, new EllipsoidProcessor());
        this.shapeProcessors.put(CylinderRegion.class, new CylinderProcessor());
        this.shapeProcessors.put(ConvexPolyhedralRegion.class, new ConvexPolyhedralProcessor());
    }

    public void init() {
        if (this.task != null) {
            this.task.cancel();
        }

        int delay = Math.max(1, this.plugin.getConfig().getInt("selection-update-interval"));
        this.task = this.plugin.getScheduler().scheduleGlobalRepeating(this, 20, delay);
    }

    @Override
    public void run() {
        for (PlayerVisualizerData player : this.plugin.getAllPlayerData()) {
            updatePlayerVisualizations(player);
        }
    }

    public void updatePlayerVisualizations(PlayerVisualizerData playerData) {
        LocalSession session = getSession(playerData.getPlayer());

        if (session == null) {
            return;
        }

        this.plugin.updateHoldingSelectionItem(playerData);

        for (SelectionType type : SelectionType.getValues()) {
            updatePlayerVisualization(playerData, session, type);
        }
    }

    public void updatePlayerVisualization(PlayerVisualizerData playerData, LocalSession session, SelectionType type) {
        Player player = playerData.getPlayer();
        PlayerSelection playerSelection = playerData.getSelection(type).orElse(null);

        if (playerSelection == null) {
            return;
        }

        ResolvedRegion resolved = resolveRegion(type, session, player);

        if (resolved == null) {
            if (playerSelection.getLastSelectedRegion() != null) {
                callChangeEvent(player, type, null);
            }

            playerSelection.resetSelection();
            return;
        }

        RegionAdapter regionAdapter = this.plugin.getCompatibilityHelper().adaptRegion(resolved.region);
        RegionInfo regionInfo = regionAdapter.getRegionInfo();

        // Skipping unchanged regions throttles the permission check below to region changes
        if (isUnchanged(playerSelection, regionInfo, resolved.origin)) {
            return;
        }

        callChangeEvent(player, type, resolved.region);

        // Losing 'wesv.use' applies on the next region change, but gaining it only applies
        // once the selection changes again, as resetSelection() keeps isUnchanged() true
        if (!player.hasPermission("wesv.use")) {
            playerSelection.resetSelection(regionInfo);
            return;
        }

        VisualizationConfig config = this.plugin.getVisualizationConfig(type);

        if (isSelectionTooLarge(regionAdapter, config, player, playerSelection, regionInfo)) {
            return;
        }

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

    private boolean isSelectionTooLarge(RegionAdapter regionAdapter, VisualizationConfig config,
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
        World selectionWorld = session.getSelectionWorld();

        if (selectionWorld == null) {
            return null;
        }

        if (!selectionWorld.getName().equals(player.getWorld().getName())) {
            return null;
        }

        RegionSelector selector = session.getRegionSelector(selectionWorld);

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

    private void callChangeEvent(Player player, SelectionType type, @Nullable Region region) {
        Event event = type == SelectionType.SELECTION
                ? new SelectionChangeEvent(player, region)
                : new ClipboardChangeEvent(player, region);

        this.plugin.getServer().getPluginManager().callEvent(event);
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
