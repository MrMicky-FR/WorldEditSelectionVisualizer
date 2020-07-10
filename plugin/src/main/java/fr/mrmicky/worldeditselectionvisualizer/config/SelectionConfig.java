package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.worldeditselectionvisualizer.display.ParticleData;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SelectionConfig {

    private final double pointsDistance;
    private final double linesGap;
    private final int updateInterval;
    private final int viewDistance;

    @NotNull
    private final ParticleData particleData;

    public SelectionConfig(double pointsDistance, double linesGap, int updateInterval, int viewDistance, @NotNull ParticleData particleData) {
        this.pointsDistance = pointsDistance;
        this.linesGap = linesGap;
        this.updateInterval = updateInterval;
        this.viewDistance = viewDistance;
        this.particleData = Objects.requireNonNull(particleData, "particleData");
    }

    public double getPointsDistance() {
        return pointsDistance;
    }

    public double getLinesGap() {
        return linesGap;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    @NotNull
    public ParticleData getParticleData() {
        return particleData;
    }
}
