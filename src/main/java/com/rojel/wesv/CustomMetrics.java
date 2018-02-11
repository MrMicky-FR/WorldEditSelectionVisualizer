/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */

package com.rojel.wesv;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin statistics that use Hidendra's MCStats Metrics library
 * as well as Bastian's bStats Metrics library and provides extended
 * information for various online graphs.
 *
 * @author Rojel
 * @author martinambrus
 */
public class CustomMetrics {

    /**
     * Configuration option for disabled metrics options.
     */
    private static final String disabledValue = "Disabled";

    /**
     * Configuration option for enabled metrics options.
     */
    private static final String enabledValue = "Enabled";

    /**
     * The plugin for which these statistics are being collected.
     */
    private final JavaPlugin plugin;

    /**
     * WESV configuration, determines what data to capture.
     */
    private final Configuration config;

    /**
     * Constructor. Saves references to plugin and its configuration.
     *
     * ```java
     * final CustomMetrics customMetrics = new CustomMetrics(yourPluginInstance, WESVConfigurationInstance);
     * customMetrics.initMetrics();
     * ```
     *
     * @param plugin - The plugin for which statistics are being collected.
     * @param config - Configuration of this plugin.
     */
    public CustomMetrics(final JavaPlugin plugin, final Configuration config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Adds a new Graph into MCStats metrics using the name and the value provided.
     *
     * ```java
     * customMetrics.addMcstatsGraph(mcStatsMetricsInstance, "PluginVersion", 1.21);
     * ```
     *
     * @param metrics MCStats Metrics class instance.
     * @param graphName Name of the graph to add.
     * @param graphValue Value for the graph to send.
     */
    private void addMcstatsGraph(final Metrics metrics, final String graphName, final String graphValue) {
        final Metrics.Graph cuboidGraph = metrics.createGraph(graphName);
        cuboidGraph.addPlotter(new Metrics.AbstractPlotter(graphValue) {
            @Override
            public int getValue() {
                return 1;
            }
        });
    }

    /**
     * Adds a new Graph into BStats metrics using the name and the value provided.
     *
     * ```java
     * customMetrics.addBcstatsGraph(bcStatsMetricsInstance, "PluginVersion", 1.21);
     * ```
     *
     * @param bmetrics BStats Metrics class instance.
     * @param graphID ID of the graph to add.
     * @param graphValue Value for the graph to send.
     */
    private void addBcstatsGraph(final org.bstats.Metrics bmetrics, final String graphID, final String graphValue) {
        bmetrics.addCustomChart(new org.bstats.Metrics.SimplePie(graphID) {
            @Override
            public String getValue() {
                return graphValue;
            }
        });
    }

    /**
     * Initialization routine for MCStats.
     *
     * ```java
     * final CustomMetrics customMetrics = new CustomMetrics(yourPluginInstance, WESVConfigurationInstance);
     * customMetrics.initMcStats();
     * ```
     */
    private void initMcStats() {
        try {
            final Metrics metrics = new Metrics(this.plugin);

            // create graph for Horizontal lines for cuboid selections
            this.addMcstatsGraph(metrics, "Horizontal lines for cuboid selections",
                    this.config.isCuboidLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for polygon selections
            this.addMcstatsGraph(metrics, "Horizontal lines for polygon selections",
                    this.config.isPolygonLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for cylinder selections
            this.addMcstatsGraph(metrics, "Horizontal lines for cylinder selections",
                    this.config.isCylinderLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for ellipsoid selections
            this.addMcstatsGraph(metrics, "Horizontal lines for ellipsoid selections",
                    this.config.isEllipsoidLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Gap between points
            this.addMcstatsGraph(metrics, "Gap between points", "" + this.config.getGapBetweenPoints() + "");

            // create graph for Vertical gap between horizontal filling lines
            this.addMcstatsGraph(metrics, "Vertical gap between horizontal filling lines",
                    "" + this.config.getVerticalGap() + "");

            // create graph for Particle update interval
            this.addMcstatsGraph(metrics, "Particle update interval",
                    "" + this.config.getUpdateParticlesInterval() + "");

            // create graph for Selection update interval
            this.addMcstatsGraph(metrics, "Selection update interval",
                    "" + this.config.getUpdateSelectionInterval() + "");

            // create graph for Particle effect
            this.addMcstatsGraph(metrics, "Particle effect", this.config.getParticle().getName());

            // create graph for Check for axe
            this.addMcstatsGraph(metrics, "Check for axe",
                    this.config.isCheckForAxeEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Particle distance
            this.addMcstatsGraph(metrics, "Particle distance", "" + this.config.getParticleDistance() + "");

            // create graph for Maximum selection size
            this.addMcstatsGraph(metrics, "Maximum selection size", "" + this.config.getMaxSize() + "");

            // start collecting statistics
            metrics.start();
        } catch (final IOException e) {
            this.plugin.getLogger().info("Unable to submit statistics to MCStats :(");
        }
    }

    /**
     * Initialization routine for BStats.
     *
     * ```java
     * final CustomMetrics customMetrics = new CustomMetrics(yourPluginInstance, WESVConfigurationInstance);
     * customMetrics.initBStats();
     * ```
     */
    private void initBStats() {
        final org.bstats.Metrics bmetrics = new org.bstats.Metrics(this.plugin);

        // create graph for Horizontal lines for cuboid selections
        this.addBcstatsGraph(bmetrics, "h_lines_cuboid",
                this.config.isCuboidLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for polygon selections
        this.addBcstatsGraph(bmetrics, "h_lines_polygon",
                this.config.isPolygonLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for cylinder selections
        this.addBcstatsGraph(bmetrics, "h_lines_cylinder",
                this.config.isCylinderLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for ellipsoid selections
        this.addBcstatsGraph(bmetrics, "h_lines_ellipsoid",
                this.config.isEllipsoidLinesEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Gap between points
        this.addBcstatsGraph(bmetrics, "gap_between_points", "" + this.config.getGapBetweenPoints() + "");

        // create graph for Vertical gap between horizontal filling lines
        this.addBcstatsGraph(bmetrics, "v_gap_horizontal_lines", "" + this.config.getVerticalGap() + "");

        // create graph for Particle update interval
        this.addBcstatsGraph(bmetrics, "particle_update_interval", "" + this.config.getUpdateParticlesInterval() + "");

        // create graph for Selection update interval
        this.addBcstatsGraph(bmetrics, "selection_update_interval", "" + this.config.getUpdateSelectionInterval() + "");

        // create graph for Particle effect
        this.addBcstatsGraph(bmetrics, "particle_effect", this.config.getParticle().getName());

        // create graph for Check for axe
        this.addBcstatsGraph(bmetrics, "check_for_axe",
                this.config.isCheckForAxeEnabled() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Particle distance
        this.addBcstatsGraph(bmetrics, "particle_distance", "" + this.config.getParticleDistance() + "");

        // create graph for Maximum selection size
        this.addBcstatsGraph(bmetrics, "max_selection_size", "" + this.config.getMaxSize() + "");
    }

    /**
     * Initialization method for all supported metrics.
     *
     * ```java
     * final CustomMetrics customMetrics = new CustomMetrics(yourPluginInstance, WESVConfigurationInstance);
     * customMetrics.initMetrics();
     * ```
     */
    public void initMetrics() {
        this.initMcStats();
        this.initBStats();
    }

}
