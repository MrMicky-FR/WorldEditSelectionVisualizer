package fr.mrmicky.worldeditselectionvisualizer.metrics;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.CompatibilityHelper;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class WesvMetrics {

    private static final int B_STATS_PLUGIN_ID = 335;

    private final Metrics metrics;

    private WesvMetrics(WorldEditSelectionVisualizer plugin) {
        this.metrics = new Metrics(plugin, B_STATS_PLUGIN_ID);

        CompatibilityHelper compatHelper = plugin.getCompatibilityHelper();

        addBooleanChart("top_bottom_cuboid", () -> plugin.getConfig().getBoolean("cuboid-top-bottom"));
        addBooleanChart("check_for_axe", () -> plugin.getConfig().getBoolean("need-we-wand"));
        addCustomChart("worldedit_version", () -> "WorldEdit " + compatHelper.getWorldEditVersion());
        addCustomChart("selection_update_interval", () -> {
            int interval = plugin.getConfig().getInt("selection-update-interval");
            return Integer.toString(interval);
        });
    }

    public static void register(WorldEditSelectionVisualizer plugin) {
        new WesvMetrics(plugin);
    }

    private void addBooleanChart(String name, BooleanSupplier value) {
        addCustomChart(name, () -> value.getAsBoolean() ? "Enabled" : "Disabled");
    }

    private void addCustomChart(String name, Supplier<String> value) {
        this.metrics.addCustomChart(new SimplePie(name, value::get));
    }
}
