/*
  * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */

package com.rojel.wesv;

import java.util.EnumMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.darkblade12.particleeffect.ParticleEffect;

/**
 * YAML plugin configuration retrieval and manipulation class.
 *
 * @author  Martin Ambrus
 * @since   1.0a
 */
public class Configuration {
    /**
     * WESV plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * WESV YAML configuration representation.
     */
    private FileConfiguration config;

    /**
     * ENUM value of a particle effect used to visually display current WorldEdit selection.
     */
    private ParticleEffect particle;

    /**
     * ENUM of valid configuration values.
     */
    private enum CONFIG_VALUES {
        /**
         * Size of a space left between 2 points.
         */
        GAPBETWEENPOINTS(
                "gapBetweenPoints"
        ),
        /**
         * Size of a vertical space left between 2 points.
         */
        VERTICALGAP(
                "verticalGap"
        ),
        /**
         * Interval in which particles should be updated for the MC client.
         */
        UPDATEPARTICLESINTERVAL(
                "updateParticlesInterval"
        ),
        /**
         * Interval (ms) in which the selection should be updated for the MC client.
         */
        UPDATESELECTIONINTERVAL(
                "updateSelectionInterval"
        ),
        /**
         * Whether or not to show cuboid lines.
         */
        CUBOIDLINES(
                "horizontalLinesForCuboid"
        ),
        /**
         * Whether or not to show polygon lines.
         */
        POLYGONLINES(
                "horizontalLinesForPolygon"
        ),
        /**
         * Whether or not to show cylinder lines.
         */
        CYLINDERLINES(
                "horizontalLinesForCylinder"
        ),
        /**
         * Whether or not to show ellipsoid lines.
         */
        ELLIPSOIDLINES(
                "horizontalLinesForEllipsoid"
        ),
        /**
         * Whether or not to check for the WorldEdit tool in hand.
         */
        CHECKFORAXE(
                "checkForAxe"
        ),
        /**
         * Maximum distance to see selection particles from.
         */
        PARTICLEDISTANCE(
                "particleDistance"
        ),
        /**
         * Whether or not to use the ProtocolLib library (allows to see particles from longer distances).
         */
        USEPROTOCOLLIB(
                "useProtocolLib"
        ),
        /**
         * Maximum size of the visualized selection itself.
         */
        MAXSIZE(
                "maxSize"
        ),
        /**
         * Language translation string from config.
         */
        LANGVISUALIZERENABLED(
                "lang.langVisualizerEnabled"
        ),
        /**
         * Language translation string from config.
         */
        LANGVISUALIZERDISABLED(
                "lang.langVisualizerDisabled"
        ),
        /**
         * Language translation string from config.
         */
        LANGPLAYERSONLY(
                "lang.langPlayersOnly"
        ),
        /**
         * Language translation string from config.
         */
        LANGSELECTIONSIZEOF(
                "lang,langSelectionSizeOf"
        ),
        /**
         * Language translation string from config.
         */
        LANGBLOCKS(
                "lang.langBlocks"
        );

        /**
         * The string value of an ENUM.
         */
        private final String configValue;

        /**
         * Constructor for String ENUMs.
         * @param value String value for the ENUM.
         */
        CONFIG_VALUES(final String value) {
            this.configValue = value;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.configValue;
        }

    }

    /**
     * Configuration HashMap containing all configuration options and their values.
     * This configuration comes from config.yml file.
     */
    private final EnumMap<CONFIG_VALUES, Object> configItems = new EnumMap<CONFIG_VALUES, Object>(CONFIG_VALUES.class) {

        private static final long serialVersionUID = 634292893169729562L;

        {

            // Size of a space left between 2 points.
            this.put(CONFIG_VALUES.GAPBETWEENPOINTS, 0.5d);

            // Size of a vertical space left between 2 points.
            this.put(CONFIG_VALUES.VERTICALGAP, 1d);

            // Interval (ms) in which particles should be updated for the MC client.
            this.put(CONFIG_VALUES.UPDATEPARTICLESINTERVAL, 5);

            // Interval (ms) in which the selection should be updated for the MC client.
            this.put(CONFIG_VALUES.UPDATESELECTIONINTERVAL, 20);

            // Whether or not to show cuboid lines.
            this.put(CONFIG_VALUES.CUBOIDLINES, true);

            // Whether or not to show polygon lines.
            this.put(CONFIG_VALUES.POLYGONLINES, true);

            // Whether or not to show cylinder lines.
            this.put(CONFIG_VALUES.CYLINDERLINES, true);

            // Whether or not to show ellipsoid lines.
            this.put(CONFIG_VALUES.ELLIPSOIDLINES, true);

            // Whether or not to check for the WorldEdit tool in hand.
            this.put(CONFIG_VALUES.CHECKFORAXE, true);

            // Maximum distance to see selection particles from.
            this.put(CONFIG_VALUES.PARTICLEDISTANCE, 16);

            // Whether or not to use the ProtocolLib library (allows to see particles from longer distances).
            this.put(CONFIG_VALUES.USEPROTOCOLLIB, false);

            // Maximum size of the visualized selection itself.
            this.put(CONFIG_VALUES.MAXSIZE, 10000);

            // Language translation string from config.
            this.put(CONFIG_VALUES.LANGVISUALIZERENABLED, "Your visualizer has been enabled.");

            // Language translation string from config.
            this.put(CONFIG_VALUES.LANGVISUALIZERDISABLED, "Your visualizer has been disabled.");

            // Language translation string from config.
            this.put(CONFIG_VALUES.LANGPLAYERSONLY, "Only a player can toggle his visualizer.");

            // Language translation string from config.
            this.put(CONFIG_VALUES.LANGSELECTIONSIZEOF, "The visualizer only works with selections up to a size of ");

            // Language translation string from config.
            this.put(CONFIG_VALUES.LANGBLOCKS, "blocks");

        }
    };

    /**
     * The item used to perform selections in WorldEdit.
     */
    private Material selectionItem;

    /**
     * Constructor, takes the WESV plugin instance as a parameter.
     * @param plugin WESV plugin instance.
     */
    public Configuration(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads configuration values from the config.yml YAML file.
     */
    public void load() {
        this.plugin.saveDefaultConfig();
        this.config = this.plugin.getConfig();
        this.config.options().copyDefaults(true);

        this.particle = this.getParticleEffect(this.config.getString("particleEffect"));
        this.selectionItem = this.getSelectionItem(this.config.getString("selectionItem"));

        this.configItems.put(CONFIG_VALUES.GAPBETWEENPOINTS,
                this.config.getDouble(CONFIG_VALUES.GAPBETWEENPOINTS.toString()));

        this.configItems.put(CONFIG_VALUES.VERTICALGAP, this.config.getDouble(CONFIG_VALUES.VERTICALGAP.toString()));

        this.configItems.put(CONFIG_VALUES.UPDATEPARTICLESINTERVAL,
                this.config.getInt(CONFIG_VALUES.UPDATEPARTICLESINTERVAL.toString()));

        this.configItems.put(CONFIG_VALUES.UPDATESELECTIONINTERVAL,
                this.config.getInt(CONFIG_VALUES.UPDATESELECTIONINTERVAL.toString()));

        this.configItems.put(CONFIG_VALUES.CUBOIDLINES, this.config.getBoolean(CONFIG_VALUES.CUBOIDLINES.toString()));

        this.configItems.put(CONFIG_VALUES.POLYGONLINES, this.config.getBoolean(CONFIG_VALUES.POLYGONLINES.toString()));

        this.configItems.put(CONFIG_VALUES.CYLINDERLINES,
                this.config.getBoolean(CONFIG_VALUES.CYLINDERLINES.toString()));

        this.configItems.put(CONFIG_VALUES.ELLIPSOIDLINES,
                this.config.getBoolean(CONFIG_VALUES.ELLIPSOIDLINES.toString()));

        this.configItems.put(CONFIG_VALUES.CHECKFORAXE, this.config.getBoolean(CONFIG_VALUES.CHECKFORAXE.toString()));

        this.configItems.put(CONFIG_VALUES.PARTICLEDISTANCE,
                this.config.getInt(CONFIG_VALUES.PARTICLEDISTANCE.toString()));

        this.configItems.put(CONFIG_VALUES.USEPROTOCOLLIB,
                this.config.getBoolean(CONFIG_VALUES.USEPROTOCOLLIB.toString()));

        this.configItems.put(CONFIG_VALUES.MAXSIZE, this.config.getInt(CONFIG_VALUES.MAXSIZE.toString()));

        // language config
        this.configItems.put(CONFIG_VALUES.LANGVISUALIZERENABLED,
                this.config.getString(CONFIG_VALUES.LANGVISUALIZERENABLED.toString()));

        this.configItems.put(CONFIG_VALUES.LANGVISUALIZERDISABLED,
                this.config.getString(CONFIG_VALUES.LANGVISUALIZERDISABLED.toString()));

        this.configItems.put(CONFIG_VALUES.LANGPLAYERSONLY,
                this.config.getString(CONFIG_VALUES.LANGPLAYERSONLY.toString()));

        this.configItems.put(CONFIG_VALUES.LANGSELECTIONSIZEOF,
                this.config.getString(CONFIG_VALUES.LANGSELECTIONSIZEOF.toString()));

        this.configItems.put(CONFIG_VALUES.LANGBLOCKS, this.config.getString(CONFIG_VALUES.LANGBLOCKS.toString()));
    }

    /**
     * Retrieves ParticleEffect representation of the given name.
     * @param name Name of the particle effect from config.
     * @return Returns a ParticleEffect representation of the given name.
     */
    public ParticleEffect getParticleEffect(final String name) {
        final ParticleEffect effect = ParticleEffect.fromName(name);
        if (effect != null) {
            return effect;
        }
        this.plugin.getLogger().warning("The particle effect set in the configuration file is invalid.");
        return ParticleEffect.REDSTONE;
    }

    /**
     * Retrieves material name for the chosen selection tool.
     * @param name Name of the tool from config.
     * @return Returns the name of the tool from config.
     */
    public Material getSelectionItem(final String name) {
        final Material selectionItem = Material.getMaterial(name);
        if (selectionItem != null) {
            return selectionItem;
        }
        this.plugin.getLogger().warning("The selection item set in the configuration file is invalid.");
        return Material.WOOD_AXE;
    }

    /**
     * Checks whether WESV is enabled for the given player.
     * @param player Player to check if WESV is enabled for.
     * @return Returns true if WESV is enabled for the given player, false otherwise.
     */
    public boolean isEnabled(final Player player) {
        final String path = "players." + player.getUniqueId().toString();
        this.config.addDefault(path, true);
        return this.config.getBoolean(path);
    }

    /**
     * Enables or disables WESV for the given player.
     * @param player Player to enable or disable WESV visualization for.
     * @param enabled Whether to enable (true) or disable (false) WESV for the given player.
     */
    public void setEnabled(final Player player, final boolean enabled) {
        this.config.set("players." + player.getUniqueId().toString(), enabled);
        this.plugin.saveConfig();
    }

    /**
     * Retrieves the "particle" property value.
     * @return Returns the "particle" property value.
     */
    public ParticleEffect getParticle() {
        return this.particle;
    }

    /**
     * Retrieves the "gapBetweenPoints" property value.
     * @return Returns the "gapBetweenPoints" property value.
     */
    public double getGapBetweenPoints() {
        return (double) this.configItems.get(CONFIG_VALUES.GAPBETWEENPOINTS);
    }

    /**
     * Retrieves the "verticalGap" property value.
     * @return Returns the "verticalGap" property value.
     */
    public double getVerticalGap() {
        return (double) this.configItems.get(CONFIG_VALUES.VERTICALGAP);
    }

    /**
     * Retrieves the "updateParticlesInterval" property value.
     * @return Returns the "updateParticlesInterval" property value.
     */
    public int getUpdateParticlesInterval() {
        return (int) this.configItems.get(CONFIG_VALUES.UPDATEPARTICLESINTERVAL);
    }

    /**
     * Retrieves the "updateSelectionInterval" property value.
     * @return Returns the "updateSelectionInterval" property value.
     */
    public int getUpdateSelectionInterval() {
        return (int) this.configItems.get(CONFIG_VALUES.UPDATESELECTIONINTERVAL);
    }

    /**
     * Retrieves the "cuboidLines" property value.
     * @return Returns the "cuboidLines" property value.
     */
    public boolean isCuboidLinesEnabled() {
        return (boolean) this.configItems.get(CONFIG_VALUES.CUBOIDLINES);
    }

    /**
     * Retrieves the "polygonLines" property value.
     * @return Returns the "polygonLines" property value.
     */
    public boolean isPolygonLinesEnabled() {
        return (boolean) this.configItems.get(CONFIG_VALUES.POLYGONLINES);
    }

    /**
     * Retrieves the "cylinderLines" property value.
     * @return Returns the "cylinderLines" property value.
     */
    public boolean isCylinderLinesEnabled() {
        return (boolean) this.configItems.get(CONFIG_VALUES.CYLINDERLINES);
    }

    /**
     * Retrieves the "ellipsoidLines" property value.
     * @return Returns the "ellipsoidLines" property value.
     */
    public boolean isEllipsoidLinesEnabled() {
        return (boolean) this.configItems.get(CONFIG_VALUES.ELLIPSOIDLINES);
    }

    /**
     * Retrieves the "checkForAxe" property value.
     * @return Returns the "checkForAxe" property value.
     */
    public boolean isCheckForAxeEnabled() {
        return (boolean) this.configItems.get(CONFIG_VALUES.CHECKFORAXE);
    }

    /**
     * Retrieves the "selectionItem" property value.
     * @return Returns the "selectionItem" property value.
     */
    public Material getSelectionItemConfigValue() {
        return this.selectionItem;
    }

    /**
     * Retrieves the "useProtocolLib" property value.
     * @return Returns the "useProtocolLib" property value.
     */
    public boolean isUsingProtocolLib() {
        return (boolean) this.configItems.get(CONFIG_VALUES.USEPROTOCOLLIB);
    }

    /**
     * Retrieves the "particleDistance" property value.
     * @return Returns the "particleDistance" property value.
     */
    public int getParticleDistance() {
        return (int) this.configItems.get(CONFIG_VALUES.PARTICLEDISTANCE);
    }

    /**
     * Retrieves the "maxSize" property value.
     * @return Returns the "maxSize" property value.
     */
    public int getMaxSize() {
        return (int) this.configItems.get(CONFIG_VALUES.MAXSIZE);
    }

    /**
     * Retrieves translation for the "langVisualizerEnabled" text.
     * @return Translation of "langVisualizerEnabled".
     */
    public String getLangVisualizerEnabled() {
        return (String) this.configItems.get(CONFIG_VALUES.LANGVISUALIZERENABLED);
    }

    /**
     * Retrieves translation for the "visualizerDisabled" text.
     * @return Translation of "visualizerDisabled".
     */
    public String getLangVisualizerDisabled() {
        return (String) this.configItems.get(CONFIG_VALUES.LANGVISUALIZERDISABLED);
    }

    /**
     * Retrieves translation for the "playersOnly" text.
     * @return Translation of "playersOnly".
     */
    public String getLangPlayersOnly() {
        return (String) this.configItems.get(CONFIG_VALUES.LANGPLAYERSONLY);
    }

    /**
     * Retrieves translation for the "selectionSizeOf" text.
     * @return Translation of "selectionSizeOf".
     */
    public String getLangSelectionSizeOf() {
        return (String) this.configItems.get(CONFIG_VALUES.LANGSELECTIONSIZEOF);
    }

    /**
     * Retrieves translation for the "langBlocks" text.
     * @return Translation of "langBlocks".
     */
    public String getLangBlocks() {
        return (String) this.configItems.get(CONFIG_VALUES.LANGBLOCKS);
    }
}
