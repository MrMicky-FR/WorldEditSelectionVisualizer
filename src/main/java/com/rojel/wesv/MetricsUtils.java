package com.rojel.wesv;

import org.bstats.bukkit.Metrics;

import java.util.concurrent.Callable;

public class MetricsUtils {

    private MetricsUtils() {
    }

    public static void register(WorldEditSelectionVisualizer plugin) {
        Metrics metrics = new Metrics(plugin);

        addCustomChartBoolean(metrics, "h_lines_cuboid", plugin.getCustomConfig()::isCuboidLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_polygon", plugin.getCustomConfig()::isPolygonLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_cylinder", plugin.getCustomConfig()::isCylinderLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_ellipsoid", plugin.getCustomConfig()::isEllipsoidLinesEnabled);
        addCustomChartBoolean(metrics, "top_bottom_cuboid", plugin.getCustomConfig()::isCuboidTopAndBottomEnabled);
        addCustomChartBoolean(metrics, "top_bottom_ellipsoid", plugin.getCustomConfig()::isCuboidTopAndBottomEnabled);
        addCustomChartObject(metrics, "gap_between_points", plugin.getCustomConfig()::getGapBetweenPoints);
        addCustomChartObject(metrics, "v_gap_horizontal_lines", plugin.getCustomConfig()::getVerticalGap);
        addCustomChartObject(metrics, "particle_update_interval", plugin.getCustomConfig()::getUpdateParticlesInterval);
        addCustomChartObject(metrics, "selection_update_interval", plugin.getCustomConfig()::getUpdateSelectionInterval);
        addCustomChartString(metrics, "particle_effect", plugin.getCustomConfig().getParticle()::getName);
        addCustomChartBoolean(metrics, "check_for_axe", plugin.getCustomConfig()::isCheckForAxeEnabled);
        addCustomChartObject(metrics, "particle_distance", plugin.getCustomConfig()::getParticleDistance);
        addCustomChartObject(metrics, "max_selection_size", plugin.getCustomConfig()::getMaxSize);
        addCustomChartBoolean(metrics, "use_fawe", plugin::isFaweEnabled);
    }

    private static void addCustomChartBoolean(Metrics metrics, String name, Callable<Boolean> value) {
        addCustomChartString(metrics, name, () -> value.call() ? "Enabled" : "Disabled");
    }

    private static void addCustomChartObject(Metrics metrics, String name, Callable<Object> value) {
        addCustomChartString(metrics, name, () -> String.valueOf(value.call()));
    }

    private static void addCustomChartString(Metrics metrics, String name, Callable<String> value) {
        metrics.addCustomChart(new Metrics.SimplePie(name, value));
    }
}
