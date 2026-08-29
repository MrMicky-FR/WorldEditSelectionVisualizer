package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.ScheduledTask;
import fr.mrmicky.worldeditselectionvisualizer.config.DisplayConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public class ParticlesTask implements Runnable {

    private final WorldEditSelectionVisualizer plugin;

    private final SelectionType type;
    private final DisplayType displayType;
    private final DisplayConfig config;
    private final boolean needWand;

    public ParticlesTask(WorldEditSelectionVisualizer plugin,
                         SelectionType type,
                         DisplayType displayType,
                         DisplayConfig config) {
        this.plugin = plugin;
        this.type = type;
        this.displayType = displayType;
        this.config = config;
        this.needWand = plugin.getConfig().getBoolean("need-we-wand");
    }

    public ScheduledTask start() {
        int interval = this.config.getUpdateInterval();

        return this.plugin.getScheduler().scheduleAsyncRepeating(this, interval, interval);
    }

    @Override
    public void run() {
        double distanceSquare = NumberConversions.square(this.config.getViewDistance());
        Particle particleData = this.config.getParticle();

        for (PlayerVisualizerData playerData : this.plugin.getAllPlayerData()) {
            Player player = playerData.getPlayer();
            PlayerSelection selection = playerData.getSelection(this.type).orElse(null);

            if (selection == null) {
                continue;
            }

            SelectionPoints selectionPoints = selection.clearIfExpired().getSelectionPoints();

            if (selectionPoints == null || (this.needWand && !playerData.isHoldingSelectionItem())) {
                continue;
            }

            Collection<Shape> renderables = selectionPoints.get(this.displayType);
            Vector3d playerLocation = new Vector3d(player.getLocation());
            Vector3d origin = Vector3d.ZERO;
            Vector3d viewerOrigin = playerLocation;

            if (this.type == SelectionType.CLIPBOARD) {
                Location lock = playerData.getClipboardLockLocation();
                Vector3d clipboardLocation = lock != null ? new Vector3d(lock) : playerLocation;

                origin = clipboardLocation.subtract(selection.getOrigin()).floor();
                viewerOrigin = playerLocation.subtract(origin);
            }

            ParticleRenderer renderer = new ParticleRenderer(player, playerLocation,
                    origin, distanceSquare, particleData);

            for (Shape renderable : renderables) {
                renderable.render(viewerOrigin, renderer);
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
        public boolean render(double vecX, double vecY, double vecZ) {
            double x = vecX + this.origin.getX();
            double y = vecY + this.origin.getY();
            double z = vecZ + this.origin.getZ();

            if (this.location.distanceSquared(x, y, z) > this.maxDistance) {
                return false;
            }

            this.particle.getType().spawn(this.player, x, y, z, 1, 0, 0, 0, 0, this.particle.getData(), true);

            return true;
        }
    }
}
