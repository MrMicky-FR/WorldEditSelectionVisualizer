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
     * Plugin configuration, determines what data to capture.
     */
    private final Configuration config;

    /**
     * Constructor. Saves references to plugin and its configuration.
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
     * Metrics initialization method.
     */
    public void initMetrics() {
        try {
            /*
             * Initialize MCStats metrics.
             */
            final Metrics metrics = new Metrics(this.plugin);

            // create graph for Horizontal lines for cuboid selections
            this.addMcstatsGraph(metrics, "Horizontal lines for cuboid selections",
                    this.config.cuboidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for polygon selections
            this.addMcstatsGraph(metrics, "Horizontal lines for polygon selections",
                    this.config.polygonLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for cylinder selections
            this.addMcstatsGraph(metrics, "Horizontal lines for cylinder selections",
                    this.config.cylinderLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Horizontal lines for ellipsoid selections
            this.addMcstatsGraph(metrics, "Horizontal lines for ellipsoid selections",
                    this.config.ellipsoidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Gap between points
            this.addMcstatsGraph(metrics, "Gap between points", "" + this.config.gapBetweenPoints() + "");

            // create graph for Vertical gap between horizontal filling lines
            this.addMcstatsGraph(metrics, "Vertical gap between horizontal filling lines",
                    "" + this.config.verticalGap() + "");

            // create graph for Particle update interval
            this.addMcstatsGraph(metrics, "Particle update interval", "" + this.config.updateParticlesInterval() + "");

            // create graph for Selection update interval
            this.addMcstatsGraph(metrics, "Selection update interval", "" + this.config.updateSelectionInterval() + "");

            // create graph for Particle effect
            this.addMcstatsGraph(metrics, "Particle effect", this.config.particle().getName());

            // create graph for Check for axe
            this.addMcstatsGraph(metrics, "Check for axe",
                    this.config.checkForAxe() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Use ProtocolLib
            this.addMcstatsGraph(metrics, "Use ProtocolLib",
                    this.config.useProtocolLib() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

            // create graph for Particle distance
            this.addMcstatsGraph(metrics, "Particle distance", "" + this.config.particleDistance() + "");

            // create graph for Maximum selection size
            this.addMcstatsGraph(metrics, "Maximum selection size", "" + this.config.maxSize() + "");

            // start collecting statistics
            metrics.start();
        } catch (final IOException e) {
            this.plugin.getLogger().info("Unable to submit statistics to MCStats :(");
        }

        /*
         * Initialize BStats metrics.
         */
        final org.bstats.Metrics bmetrics = new org.bstats.Metrics(this.plugin);

        // create graph for Horizontal lines for cuboid selections
        this.addBcstatsGraph(bmetrics, "h_lines_cuboid",
                this.config.cuboidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for polygon selections
        this.addBcstatsGraph(bmetrics, "h_lines_polygon",
                this.config.polygonLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for cylinder selections
        this.addBcstatsGraph(bmetrics, "h_lines_cylinder",
                this.config.cylinderLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Horizontal lines for ellipsoid selections
        this.addBcstatsGraph(bmetrics, "h_lines_ellipsoid",
                this.config.ellipsoidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Gap between points
        this.addBcstatsGraph(bmetrics, "gap_between_points", "" + this.config.gapBetweenPoints() + "");

        // create graph for Vertical gap between horizontal filling lines
        this.addBcstatsGraph(bmetrics, "v_gap_horizontal_lines", "" + this.config.verticalGap() + "");

        // create graph for Particle update interval
        this.addBcstatsGraph(bmetrics, "particle_update_interval", "" + this.config.updateParticlesInterval() + "");

        // create graph for Selection update interval
        this.addBcstatsGraph(bmetrics, "selection_update_interval", "" + this.config.updateSelectionInterval() + "");

        // create graph for Particle effect
        this.addBcstatsGraph(bmetrics, "particle_effect", this.config.particle().getName());

        // create graph for Check for axe
        this.addBcstatsGraph(bmetrics, "check_for_axe",
                this.config.checkForAxe() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Use ProtocolLib
        this.addBcstatsGraph(bmetrics, "protocollib_use",
                this.config.useProtocolLib() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue);

        // create graph for Particle distance
        this.addBcstatsGraph(bmetrics, "particle_distance", "" + this.config.particleDistance() + "");

        // create graph for Maximum selection size
        this.addBcstatsGraph(bmetrics, "max_selection_size", "" + this.config.maxSize() + "");
    }

}
