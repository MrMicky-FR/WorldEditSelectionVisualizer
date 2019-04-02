package com.rojel.wesv;

import org.bstats.bukkit.Metrics;

import java.util.concurrent.Callable;

public final class MetricsUtils {

    private MetricsUtils() {
        throw new UnsupportedOperationException();
    }

    public static void register(WorldEditSelectionVisualizer plugin) {
        Metrics metrics = new Metrics(plugin);
        Configuration config = plugin.getCustomConfig();

        addCustomChartBoolean(metrics, "h_lines_cuboid", config::isCuboidLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_polygon", config::isPolygonLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_cylinder", config::isCylinderLinesEnabled);
        addCustomChartBoolean(metrics, "h_lines_ellipsoid", config::isEllipsoidLinesEnabled);
        addCustomChartBoolean(metrics, "top_bottom_cuboid", config::isCuboidTopAndBottomEnabled);
        addCustomChartBoolean(metrics, "top_bottom_ellipsoid", config::isCuboidTopAndBottomEnabled);
        addCustomChartObject(metrics, "gap_between_points", config::getGapBetweenPoints);
        addCustomChartObject(metrics, "v_gap_horizontal_lines", config::getVerticalGap);
        addCustomChartObject(metrics, "particle_update_interval", config::getUpdateParticlesInterval);
        addCustomChartObject(metrics, "selection_update_interval", config::getUpdateSelectionInterval);
        addCustomChartString(metrics, "particle_effect", config.getParticle()::getName);
        addCustomChartBoolean(metrics, "check_for_axe", config::isCheckForAxeEnabled);
        addCustomChartObject(metrics, "particle_distance", config::getParticleDistance);
        addCustomChartObject(metrics, "max_selection_size", config::getMaxSize);
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
