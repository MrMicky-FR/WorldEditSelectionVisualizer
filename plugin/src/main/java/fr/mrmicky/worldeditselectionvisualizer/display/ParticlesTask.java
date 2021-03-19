package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ParticlesTask extends BukkitRunnable {

    private final WorldEditSelectionVisualizer plugin;

    @NotNull
    private final SelectionType type;
    private final boolean primary;

    @NotNull
    private final SelectionConfig config;

    public ParticlesTask(WorldEditSelectionVisualizer plugin, @NotNull SelectionType type, boolean primary, @NotNull SelectionConfig config) {
        this.plugin = plugin;
        this.type = type;
        this.primary = primary;
        this.config = config;
    }

    public BukkitTask start() {
        return runTaskTimer(plugin, config.getUpdateInterval(), config.getUpdateInterval());
    }

    @Override
    public void run() {
        boolean needWand = plugin.getConfig().getBoolean("need-we-wand");
        double maxDistanceSquared = NumberConversions.square(config.getViewDistance());
        ParticleData particleData = config.getParticleData();

        for (PlayerVisualizerData visualizerData : plugin.getPlayers()) {
            Player player = visualizerData.getPlayer();
            PlayerSelection playerSelection = visualizerData.getSelection(type).orElse(null);

            if (playerSelection == null) {
                continue;
            }

            playerSelection.checkExpireTime();
            SelectionPoints selectionPoints = playerSelection.getSelectionPoints();

            if (selectionPoints == null || (needWand && !visualizerData.isHoldingSelectionItem())) {
                continue;
            }

            Collection<Vector3d> vectors = primary ? selectionPoints.primary() : selectionPoints.secondary();

            Vector3d location = new Vector3d(player.getLocation().toVector());
            Vector3d origin = (type != SelectionType.CLIPBOARD) ? Vector3d.ZERO : location.subtract(selectionPoints.origin()).floor();

            for (Vector3d vector : vectors) {
                double x = vector.getX() + origin.getX();
                double y = vector.getY() + origin.getY();
                double z = vector.getZ() + origin.getZ();

                if (location.distanceSquared(x, y, z) > maxDistanceSquared) {
                    continue;
                }

                FastParticle.spawnParticle(player, particleData.getType(), x, y, z, 1, 0, 0, 0, 0, particleData.getData());
            }
        }
    }
}
