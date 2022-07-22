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

    private final @NotNull SelectionType type;
    private final @NotNull DisplayType displayType;
    private final @NotNull SelectionConfig config;

    public ParticlesTask(WorldEditSelectionVisualizer plugin,
                         @NotNull SelectionType type,
                         @NotNull DisplayType displayType,
                         @NotNull SelectionConfig config) {
        this.plugin = plugin;
        this.type = type;
        this.displayType = displayType;
        this.config = config;
    }

    public BukkitTask start() {
        int interval = this.config.getUpdateInterval();

        return Bukkit.getScheduler().runTaskTimer(this.plugin, this, interval, interval);
    }

    @Override
    public void run() {
        boolean needWand = this.plugin.getConfig().getBoolean("need-we-wand");
        double distanceSquare = NumberConversions.square(this.config.getViewDistance());
        Particle particleData = this.config.getParticle();

        for (PlayerVisualizerData playerData : this.plugin.getPlayers()) {
            Player player = playerData.getPlayer();
            PlayerSelection selection = playerData.getSelection(this.type).orElse(null);

            if (selection == null) {
                continue;
            }

            SelectionPoints selectionPoints = selection.verifyExpireTime().getSelectionPoints();

            if (selectionPoints == null || (needWand && !playerData.isHoldingSelectionItem())) {
                continue;
            }

            Collection<Shape> renderables = selectionPoints.get(this.displayType);
            Vector3d location = new Vector3d(playerData.getClipboardLocation().toVector());
            Vector3d origin = (this.type != SelectionType.CLIPBOARD)
                    ? Vector3d.ZERO :
                    location.subtract(selection.getOrigin()).floor();

            ParticleRenderer renderer = new ParticleRenderer(player, location,
                    origin, distanceSquare, particleData);

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

        public ParticleRenderer(Player player, Vector3d location, Vector3d origin,
                                double maxDistance, Particle particle) {
            this.player = player;
            this.location = location;
            this.origin = origin;
            this.maxDistance = maxDistance;
            this.particle = particle;
        }

        @Override
        public void render(double vecX, double vecY, double vecZ) {
            double x = vecX + this.origin.getX();
            double y = vecY + this.origin.getY();
            double z = vecZ + this.origin.getZ();

            if (this.location.distanceSquared(x, y, z) > this.maxDistance) {
                return;
            }

            this.particle.getType().spawn(this.player, x, y, z, 1, 0, 0, 0, 0, this.particle.getData());
        }
    }
}
