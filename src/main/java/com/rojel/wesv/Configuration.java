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

import java.util.HashMap;

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
     * Configuration HashMap containing all configuration options and their values.
     * This configuration comes from config.yml file.
     */
    private final HashMap<String, Object> configItems = new HashMap<String, Object>() {

        private static final long serialVersionUID = 634292893169729562L;

        {

            // Size of a space left between 2 points.
            this.put("gapBetweenPoints", 0.5d);

            // Size of a vertical space left between 2 points.
            this.put("verticalGap", 1d);

            // Interval (ms) in which particles should be updated for the MC client.
            this.put("updateParticlesInterval", 5);

            // Interval (ms) in which the selection should be updated for the MC client.
            this.put("updateSelectionInterval", 20);

            // Whether or not to show cuboid lines.
            this.put("cuboidLines", true);

            // Whether or not to show polygon lines.
            this.put("polygonLines", true);

            // Whether or not to show cylinder lines.
            this.put("cylinderLines", true);

            // Whether or not to show ellipsoid lines.
            this.put("ellipsoidLines", true);

            // Whether or not to check for the WorldEdit tool in hand.
            this.put("checkForAxe", true);

            // Maximum distance to see selection particles from.
            this.put("particleDistance", 16);

            // Whether or not to use the ProtocolLib library (allows to see particles from longer distances).
            this.put("useProtocolLib", false);

            // Maximum size of the visualized selection itself.
            this.put("maxSize", 10000);

            // Language translation string from config.
            this.put("langVisualizerEnabled", "Your visualizer has been enabled.");

            // Language translation string from config.
            this.put("langVisualizerDisabled", "Your visualizer has been disabled.");

            // Language translation string from config.
            this.put("langPlayersOnly", "Only a player can toggle his visualizer.");

            // Language translation string from config.
            this.put("langSelectionSizeOf", "The visualizer only works with selections up to a size of ");

            // Language translation string from config.
            this.put("langBlocks", "blocks");

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
        this.configItems.replace("gapBetweenPoints", this.config.getDouble("gapBetweenPoints"));
        this.configItems.replace("verticalGap", this.config.getDouble("verticalGap"));
        this.configItems.replace("updateParticlesInterval", this.config.getInt("updateParticlesInterval"));
        this.configItems.replace("updateSelectionInterval", this.config.getInt("updateSelectionInterval"));
        this.configItems.replace("cuboidLines", this.config.getBoolean("horizontalLinesForCuboid"));
        this.configItems.replace("polygonLines", this.config.getBoolean("horizontalLinesForPolygon"));
        this.configItems.replace("cylinderLines", this.config.getBoolean("horizontalLinesForCylinder"));
        this.configItems.replace("ellipsoidLines", this.config.getBoolean("horizontalLinesForEllipsoid"));
        this.configItems.replace("checkForAxe", this.config.getBoolean("checkForAxe"));
        this.configItems.replace("selectionItem", this.getSelectionItem(this.config.getString("selectionItem")));
        this.configItems.replace("particleDistance", this.config.getInt("particleDistance"));
        this.configItems.replace("useProtocolLib", this.config.getBoolean("useProtocolLib"));
        this.configItems.replace("maxSize", this.config.getInt("maxSize"));

        this.configItems.replace("langVisualizerEnabled", this.config.getString("lang.visualizerEnabled"));
        this.configItems.replace("langVisualizerDisabled", this.config.getString("lang.visualizerDisabled"));
        this.configItems.replace("langPlayersOnly", this.config.getString("lang.playersOnly"));
        this.configItems.replace("langSelectionSizeOf", this.config.getString("lang.selectionSizeOf"));
        this.configItems.replace("langBlocks", this.config.getString("lang.blocks"));
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
        return (double) this.configItems.get("gapBetweenPoints");
    }

    /**
     * Retrieves the "verticalGap" property value.
     * @return Returns the "verticalGap" property value.
     */
    public double getVerticalGap() {
        return (double) this.configItems.get("verticalGap");
    }

    /**
     * Retrieves the "updateParticlesInterval" property value.
     * @return Returns the "updateParticlesInterval" property value.
     */
    public int getUpdateParticlesInterval() {
        return (int) this.configItems.get("updateParticlesInterval");
    }

    /**
     * Retrieves the "updateSelectionInterval" property value.
     * @return Returns the "updateSelectionInterval" property value.
     */
    public int getUpdateSelectionInterval() {
        return (int) this.configItems.get("updateSelectionInterval");
    }

    /**
     * Retrieves the "cuboidLines" property value.
     * @return Returns the "cuboidLines" property value.
     */
    public boolean isCuboidLinesEnabled() {
        return (boolean) this.configItems.get("cuboidLines");
    }

    /**
     * Retrieves the "polygonLines" property value.
     * @return Returns the "polygonLines" property value.
     */
    public boolean isPolygonLinesEnabled() {
        return (boolean) this.configItems.get("polygonLines");
    }

    /**
     * Retrieves the "cylinderLines" property value.
     * @return Returns the "cylinderLines" property value.
     */
    public boolean isCylinderLinesEnabled() {
        return (boolean) this.configItems.get("cylinderLines");
    }

    /**
     * Retrieves the "ellipsoidLines" property value.
     * @return Returns the "ellipsoidLines" property value.
     */
    public boolean isEllipsoidLinesEnabled() {
        return (boolean) this.configItems.get("ellipsoidLines");
    }

    /**
     * Retrieves the "checkForAxe" property value.
     * @return Returns the "checkForAxe" property value.
     */
    public boolean isCheckForAxeEnabled() {
        return (boolean) this.configItems.get("checkForAxe");
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
        return (boolean) this.configItems.get("useProtocolLib");
    }

    /**
     * Retrieves the "particleDistance" property value.
     * @return Returns the "particleDistance" property value.
     */
    public int getParticleDistance() {
        return (int) this.configItems.get("particleDistance");
    }

    /**
     * Retrieves the "maxSize" property value.
     * @return Returns the "maxSize" property value.
     */
    public int getMaxSize() {
        return (int) this.configItems.get("maxSize");
    }

    /**
     * Retrieves translation for the "langVisualizerEnabled" text.
     * @return Translation of "langVisualizerEnabled".
     */
    public String getLangVisualizerEnabled() {
        return (String) this.configItems.get("langVisualizerEnabled");
    }

    /**
     * Retrieves translation for the "visualizerDisabled" text.
     * @return Translation of "visualizerDisabled".
     */
    public String getLangVisualizerDisabled() {
        return (String) this.configItems.get("langVisualizerDisabled");
    }

    /**
     * Retrieves translation for the "playersOnly" text.
     * @return Translation of "playersOnly".
     */
    public String getLangPlayersOnly() {
        return (String) this.configItems.get("langPlayersOnly");
    }

    /**
     * Retrieves translation for the "selectionSizeOf" text.
     * @return Translation of "selectionSizeOf".
     */
    public String getLangSelectionSizeOf() {
        return (String) this.configItems.get("langSelectionSizeOf");
    }

    /**
     * Retrieves translation for the "langBlocks" text.
     * @return Translation of "langBlocks".
     */
    public String getLangBlocks() {
        return (String) this.configItems.get("langBlocks");
    }
}
