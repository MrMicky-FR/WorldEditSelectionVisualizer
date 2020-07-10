package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.config.PositionBlockConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerInfos;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;

import java.util.Objects;

public class PositionBlockTask extends BukkitRunnable {
    private final WorldEditSelectionVisualizer plugin;
    private final PositionBlockConfig config;

    public PositionBlockTask(WorldEditSelectionVisualizer plugin, PositionBlockConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public BukkitTask start() {
        if (config == null || config.getPrimary() == null || config.getSecondary() == null)
            return null;
        return runTaskTimer(plugin, config.getUpdateInterval(), config.getUpdateInterval());
    }

    @Override
    public void run() {
        boolean needWand = plugin.getConfig().getBoolean("need-we-wand");

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerVisualizerInfos visualizerInfo = plugin.getPlayerInfos(player);

            PlayerSelection playerSelection = visualizerInfo.getSelection(SelectionType.SELECTION).orElse(null);

            if (playerSelection == null) {
                clearPositionBlocks(player, visualizerInfo);
                continue;
            }

            playerSelection.checkExpireTime();
            SelectionPoints selectionPoints = playerSelection.getSelectionPoints();

            if (selectionPoints == null || (needWand && !visualizerInfo.isHoldingSelectionItem())) {
                clearPositionBlocks(player, visualizerInfo);
                continue;
            }
            updatePositionBlocks(player, visualizerInfo, selectionPoints);
        }
    }

    private void clearPositionBlocks(Player player, PlayerVisualizerInfos visualizerInfos) {
        SelectionPoints selectionPoints = visualizerInfos.getSelectionPoints();
        if (selectionPoints == null)
            return;

        for (Vector3d pos : selectionPoints.primaryPositions()) {
            Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }
        Vector3d pos = selectionPoints.getSecondaryPosition();
        if (pos != null) {
            Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }

        visualizerInfos.setSelectionPoints(null);
    }

    private void updatePositionBlocks(Player player, PlayerVisualizerInfos visualizerInfos, SelectionPoints selectionPoints) {
        SelectionPoints lastSelectionPoints = visualizerInfos.getSelectionPoints();
        if (lastSelectionPoints != null) {
            if (!lastSelectionPoints.primaryPositions().equals(selectionPoints.primaryPositions())) {
                for (Vector3d pos : visualizerInfos.getSelectionPoints().primaryPositions()) {
                    Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
                    player.sendBlockChange(location, location.getBlock().getBlockData());
                }
            }
            if (!Objects.equals(lastSelectionPoints.getSecondaryPosition(), selectionPoints.getSecondaryPosition())) {
                Vector3d pos = lastSelectionPoints.getSecondaryPosition();
                if (pos != null) {
                    Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
                    player.sendBlockChange(location, location.getBlock().getBlockData());
                }
            }
        }

        for (Vector3d pos : selectionPoints.primaryPositions()) {
            Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
            player.sendBlockChange(location, config.getPrimary());
        }
        Vector3d pos = selectionPoints.getSecondaryPosition();
        if (pos != null) {
            Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
            player.sendBlockChange(location, config.getSecondary());
        }

        visualizerInfos.setSelectionPoints(selectionPoints);
    }
}
