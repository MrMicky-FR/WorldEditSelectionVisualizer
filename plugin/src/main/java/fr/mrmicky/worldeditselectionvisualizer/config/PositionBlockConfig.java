package fr.mrmicky.worldeditselectionvisualizer.config;

import org.bukkit.block.data.BlockData;

public class PositionBlockConfig {
    private final int updateInterval;

    private final BlockData primary;
    private final BlockData secondary;

    public PositionBlockConfig(int updateInterval, BlockData primary, BlockData secondary) {
        this.updateInterval = updateInterval;
        this.primary = primary;
        this.secondary = secondary;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public BlockData getPrimary() {
        return primary;
    }

    public BlockData getSecondary() {
        return secondary;
    }
}
