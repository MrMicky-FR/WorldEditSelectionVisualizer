package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ParticlesTask implements Runnable {

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
        return Bukkit.getScheduler().runTaskTimer(plugin, this, config.getUpdateInterval(), config.getUpdateInterval());
    }

    @Override
    public void run() {
        boolean needWand = plugin.getConfig().getBoolean("need-we-wand");
        double maxDistanceSquared = NumberConversions.square(config.getViewDistance());
        Particle particleData = config.getParticle();

        for (PlayerVisualizerData playerData : plugin.getPlayers()) {
            Player player = playerData.getPlayer();
            PlayerSelection selection = playerData.getSelection(type).orElse(null);

            if (selection == null) {
                continue;
            }

            SelectionPoints selectionPoints = selection.verifyExpireTime().getSelectionPoints();

            if (selectionPoints == null || (needWand && !playerData.isHoldingSelectionItem())) {
                continue;
            }

            Collection<Shape> renderables = primary ? selectionPoints.getPrimary() : selectionPoints.getSecondary();
            Vector3d location = new Vector3d(player.getLocation().toVector());
            Vector3d origin = (type != SelectionType.CLIPBOARD) ? Vector3d.ZERO : location.subtract(selection.getOrigin()).floor();
            ParticleRenderer renderer = new ParticleRenderer(player, location, origin, maxDistanceSquared, particleData);

            for (Shape renderable : renderables) {
                renderable.render(renderer);
            }
        }
    }

    private static class ParticleRenderer implements Shape.VectorRenderer {
        private final Player player;
        private final Vector3d location;
        private final Vector3d origin;
        private final double maxDistance;
        private final Particle particle;

        public ParticleRenderer(Player player, Vector3d location, Vector3d origin, double maxDistance, Particle particle) {
            this.player = player;
            this.location = location;
            this.origin = origin;
            this.maxDistance = maxDistance;
            this.particle = particle;
        }

        @Override
        public void render(double vecX, double vecY, double vecZ) {
            double x = vecX + origin.getX();
            double y = vecY + origin.getY();
            double z = vecZ + origin.getZ();

            if (location.distanceSquared(x, y, z) > maxDistance) {
                return;
            }

            particle.getType().spawn(player, x, y, z, 1, 0, 0, 0, 0, particle.getData());
        }
    }
}
