package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.ClipboardAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.event.SelectionChangeEvent;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.ShapeProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.ConvexPolyhedralProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.CuboidProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.CylinderProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.EllipsoidProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.FawePolyhedralProcessor;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.type.Polygonal2DProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WorldEditHelper extends BukkitRunnable {

    private final Map<Class<? extends Region>, ShapeProcessor<?>> shapeProcessors = new HashMap<>();

    private final WorldEditSelectionVisualizer plugin;
    private final WorldEditPlugin worldEditPlugin;

    public WorldEditHelper(WorldEditSelectionVisualizer plugin) {
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerVisualizations(plugin.getPlayerInfos(player));
        }
    }

    public void updatePlayerVisualizations(PlayerVisualizerInfos playerInfo) {
        for (SelectionType type : SelectionType.values()) {
            updatePlayerVisualization(playerInfo, type);
        }
    }

    public void updatePlayerVisualization(PlayerVisualizerInfos playerInfo, SelectionType type) {
        Player player = playerInfo.getPlayer();
        LocalSession session;
        try {
            session = worldEditPlugin.getSession(player);
        } catch (Exception e) {
            // sometimes after a reload getSession create errors with WorldEdit, this prevent error spam
            return;
        }

        PlayerSelection playerSelection = playerInfo.getSelection(type).orElse(null);

        if (playerSelection == null) {
            return;
        }

        Region region;

        if (type == SelectionType.CLIPBOARD) {
            ClipboardHolder clipboard = getClipboardHolder(session);

            if (clipboard == null) {
                playerSelection.resetSelection();
                return;
            }

            ClipboardAdapter clipboardAdapter = plugin.getCompatibilityHelper().adaptClipboard(clipboard.getClipboard());
            Vector3d shiftVector = Vector3d.ZERO.subtract(clipboardAdapter.getOrigin().add(0.5, 0, 0.5));

            try {
                region = clipboardAdapter.getShiftedRegion(shiftVector);
            } catch (RegionOperationException e) {
                playerSelection.resetSelection();
                return;
            }

        } else if (type == SelectionType.SELECTION) {
            region = getSelectedRegion(session);
        } else {
            return;
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
        RegionInfos regionInfos = regionAdapter.getRegionsInfos();

        if (regionInfos.equals(playerSelection.getLastSelectedRegion())) {
            return;
        }

        if (!player.hasPermission("wesv.use")) {
            playerSelection.resetSelection(regionInfos);
            return;
        }

        GlobalSelectionConfig config = plugin.getSelectionConfig(type);
        if (region.getArea() > config.getMaxSelectionSize()) {
            if (!playerSelection.isLastSelectionTooLarge()) {
                String message = plugin.getMessage("selection-too-large").replace("%blocks%", Integer.toString(config.getMaxSelectionSize()));
                plugin.getCompatibilityHelper().sendActionBar(player, message);
            }

            playerSelection.resetSelection(regionInfos);
            playerSelection.setLastSelectionTooLarge(true);
            return;
        }

        Bukkit.getPluginManager().callEvent(new SelectionChangeEvent(player, region));

        ShapeProcessor<?> shapeProcessor = shapeProcessors.get(region.getClass());

        if (shapeProcessor != null) {
            playerSelection.updateSelection(shapeProcessor.processSelection(regionAdapter, config), regionInfos, config.getFadeDelay());
        } else {
            // Unsupported selection type
            playerSelection.resetSelection(regionInfos);
        }
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

        if (plugin.getCompatibilityHelper().isUsingFawe()) {
            shapeProcessors.put(FawePolyhedralProcessor.getRegionClass(), new FawePolyhedralProcessor(plugin));
        }
    }
}
