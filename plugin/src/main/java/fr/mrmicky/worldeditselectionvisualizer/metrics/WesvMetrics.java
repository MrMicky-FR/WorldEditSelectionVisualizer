package fr.mrmicky.worldeditselectionvisualizer.metrics;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.CompatibilityHelper;
import org.bstats.bukkit.Metrics;

import java.util.function.Supplier;

public final class WesvMetrics {

    private final Metrics metrics;

    private WesvMetrics(WorldEditSelectionVisualizer plugin) {
        metrics = new Metrics(plugin);

        CompatibilityHelper compatHelper = plugin.getCompatibilityHelper();

        addObjectChart("selection_update_interval", () -> plugin.getConfig().getInt("selection-update-interval"));
        addBooleanChart("top_bottom_cuboid", () -> plugin.getConfig().getBoolean("cuboid-top-bottom"));
        addCustomChart("worldedit_version", () -> "WorldEdit " + compatHelper.getWorldEditVersion());
        addBooleanChart("use_fawe", compatHelper::isUsingFawe);
        addBooleanChart("check_for_axe", () -> plugin.getConfig().getBoolean("need-we-wand"));
    }

    public static void register(WorldEditSelectionVisualizer plugin) {
        try {
            // bStats use Gson, but Gson is not shade with Spigot < 1.8
            Class.forName("com.google.gson.Gson");

            new WesvMetrics(plugin);
        } catch (ClassNotFoundException e) {
            // disable metrics
        }
    }

    private void addBooleanChart(String name, Supplier<Boolean> value) {
        addCustomChart(name, () -> value.get() ? "Enabled" : "Disabled");
    }

    private void addObjectChart(String name, Supplier<Object> value) {
        addCustomChart(name, () -> value.get().toString());
    }

    private void addCustomChart(String name, Supplier<String> value) {
        metrics.addCustomChart(new Metrics.SimplePie(name, value::get));
    }
}
