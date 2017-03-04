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
 * and provides extended information for various online graphs.
 *
 * @author Rojel
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
     * Metrics initialization method.
     */
    public void initMetrics() {
        try {
            // initialize metrics
            final Metrics metrics = new Metrics(this.plugin);

            // create graph for Horizontal lines for cuboid selections
            final Metrics.Graph cuboidGraph = metrics.createGraph("Horizontal lines for cuboid selections");
            cuboidGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.cuboidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Horizontal lines for polygon selections
            final Metrics.Graph polygonGraph = metrics.createGraph("Horizontal lines for polygon selections");
            polygonGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.polygonLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Horizontal lines for cylinder selections
            final Metrics.Graph cylinderGraph = metrics.createGraph("Horizontal lines for cylinder selections");
            cylinderGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.cylinderLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Horizontal lines for ellipsoid selections
            final Metrics.Graph ellipsoidGraph = metrics.createGraph("Horizontal lines for ellipsoid selections");
            ellipsoidGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.ellipsoidLines() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Gap between points
            final Metrics.Graph pointGapGraph = metrics.createGraph("Gap between points");
            pointGapGraph.addPlotter(new Metrics.AbstractPlotter("" + this.config.gapBetweenPoints() + "") {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Vertical gap between horizontal filling lines
            final Metrics.Graph verticalGapGraph = metrics.createGraph("Vertical gap between horizontal filling lines");
            verticalGapGraph.addPlotter(new Metrics.AbstractPlotter("" + this.config.verticalGap() + "") {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Particle update interval
            final Metrics.Graph particleIntervalGraph = metrics.createGraph("Particle update interval");
            particleIntervalGraph
                    .addPlotter(new Metrics.AbstractPlotter("" + this.config.updateParticlesInterval() + "") {

                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });

            // create graph for Selection update interval
            final Metrics.Graph selectionIntervalGraph = metrics.createGraph("Selection update interval");
            selectionIntervalGraph
                    .addPlotter(new Metrics.AbstractPlotter("" + this.config.updateSelectionInterval() + "") {

                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });

            // create graph for Particle effect
            final Metrics.Graph particleEffectGraph = metrics.createGraph("Particle effect");
            particleEffectGraph.addPlotter(new Metrics.AbstractPlotter(this.config.particle().getName()) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Check for axe
            final Metrics.Graph checkForAxeGraph = metrics.createGraph("Check for axe");
            checkForAxeGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.checkForAxe() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Use ProtocolLib
            final Metrics.Graph protocolLibGraph = metrics.createGraph("Use ProtocolLib");
            protocolLibGraph.addPlotter(new Metrics.AbstractPlotter(
                    this.config.useProtocolLib() ? CustomMetrics.enabledValue : CustomMetrics.disabledValue) {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Particle distance
            final Metrics.Graph particleDistanceGraph = metrics.createGraph("Particle distance");
            particleDistanceGraph.addPlotter(new Metrics.AbstractPlotter("" + this.config.particleDistance() + "") {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // create graph for Maximum selection size
            final Metrics.Graph maxSizeGraph = metrics.createGraph("Maximum selection size");
            maxSizeGraph.addPlotter(new Metrics.AbstractPlotter("" + this.config.maxSize() + "") {

                @Override
                public int getValue() {
                    return 1;
                }
            });

            // start collecting statistics
            metrics.start();
        } catch (final IOException e) {
            this.plugin.getLogger().info("Unable to submit statistics to MCStats :(");
        }
    }

}
