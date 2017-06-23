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

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.darkblade12.particleeffect.ParticleEffect;

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
     * Size of a space left between 2 points.
     */
    private double gapBetweenPoints;

    /**
     * Size of a vertical space left between 2 points.
     */
    private double verticalGap;

    /**
     * Interval (ms) in which particles should be updated for the MC client.
     */
    private int updateParticlesInterval;

    /**
     * Interval (ms) in which the selection should be updated for the MC client.
     */
    private int updateSelectionInterval;

    /**
     * Whether or not to show cuboid lines.
     */
    private boolean cuboidLines;

    /**
     * Whether or not to show polygon lines.
     */
    private boolean polygonLines;

    /**
     * Whether or not to show cylinder lines.
     */
    private boolean cylinderLines;

    /**
     * Whether or not to show ellipsoid lines.
     */
    private boolean ellipsoidLines;

    /**
     * Whether or not to check for the WorldEdit tool in hand.
     */
    private boolean checkForAxe;

    /**
     * The item used to perform selections in WorldEdit.
     */
    private Material selectionItem;

    /**
     * Maximum distance to see selection particles from.
     */
    private int particleDistance;

    /**
     * Whether or not to use the ProtocolLib library (allows to see particles from longer distances).
     */
    private boolean useProtocolLib;

    /**
     * Maximum size of the visualized selection itself.
     */
    private int maxSize;

    /**
     * Language translation string from config.
     */
    private String langVisualizerEnabled;

    /**
     * Language translation string from config.
     */
    private String langVisualizerDisabled;

    /**
     * Language translation string from config.
     */
    private String langPlayersOnly;

    /**
     * Language translation string from config.
     */
    private String langSelectionSizeOf;

    /**
     * Language translation string from config.
     */
    private String langBlocks;

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
        this.gapBetweenPoints = this.config.getDouble("gapBetweenPoints");
        this.verticalGap = this.config.getDouble("verticalGap");
        this.updateParticlesInterval = this.config.getInt("updateParticlesInterval");
        this.updateSelectionInterval = this.config.getInt("updateSelectionInterval");
        this.cuboidLines = this.config.getBoolean("horizontalLinesForCuboid");
        this.polygonLines = this.config.getBoolean("horizontalLinesForPolygon");
        this.cylinderLines = this.config.getBoolean("horizontalLinesForCylinder");
        this.ellipsoidLines = this.config.getBoolean("horizontalLinesForEllipsoid");
        this.checkForAxe = this.config.getBoolean("checkForAxe");
        this.selectionItem = this.getSelectionItem(this.config.getString("selectionItem"));
        this.particleDistance = this.config.getInt("particleDistance");
        this.useProtocolLib = this.config.getBoolean("useProtocolLib");
        this.maxSize = this.config.getInt("maxSize");

        this.langVisualizerEnabled = this.config.getString("lang.visualizerEnabled");
        this.langVisualizerDisabled = this.config.getString("lang.visualizerDisabled");
        this.langPlayersOnly = this.config.getString("lang.playersOnly");
        this.langSelectionSizeOf = this.config.getString("lang.selectionSizeOf");
        this.langBlocks = this.config.getString("lang.blocks");
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
    public ParticleEffect particle() {
        return this.particle;
    }

    /**
     * Retrieves the "gapBetweenPoints" property value.
     * @return Returns the "gapBetweenPoints" property value.
     */
    public double gapBetweenPoints() {
        return this.gapBetweenPoints;
    }

    /**
     * Retrieves the "verticalGap" property value.
     * @return Returns the "verticalGap" property value.
     */
    public double verticalGap() {
        return this.verticalGap;
    }

    /**
     * Retrieves the "updateParticlesInterval" property value.
     * @return Returns the "updateParticlesInterval" property value.
     */
    public int updateParticlesInterval() {
        return this.updateParticlesInterval;
    }

    /**
     * Retrieves the "updateSelectionInterval" property value.
     * @return Returns the "updateSelectionInterval" property value.
     */
    public int updateSelectionInterval() {
        return this.updateSelectionInterval;
    }

    /**
     * Retrieves the "cuboidLines" property value.
     * @return Returns the "cuboidLines" property value.
     */
    public boolean cuboidLines() {
        return this.cuboidLines;
    }

    /**
     * Retrieves the "polygonLines" property value.
     * @return Returns the "polygonLines" property value.
     */
    public boolean polygonLines() {
        return this.polygonLines;
    }

    /**
     * Retrieves the "cylinderLines" property value.
     * @return Returns the "cylinderLines" property value.
     */
    public boolean cylinderLines() {
        return this.cylinderLines;
    }

    /**
     * Retrieves the "ellipsoidLines" property value.
     * @return Returns the "ellipsoidLines" property value.
     */
    public boolean ellipsoidLines() {
        return this.ellipsoidLines;
    }

    /**
     * Retrieves the "checkForAxe" property value.
     * @return Returns the "checkForAxe" property value.
     */
    public boolean checkForAxe() {
        return this.checkForAxe;
    }

    /**
     * Retrieves the "selectionItem" property value.
     * @return Returns the "selectionItem" property value.
     */
    public Material selectionItem() {
        return this.selectionItem;
    }

    /**
     * Retrieves the "useProtocolLib" property value.
     * @return Returns the "useProtocolLib" property value.
     */
    public boolean useProtocolLib() {
        return this.useProtocolLib;
    }

    /**
     * Retrieves the "particleDistance" property value.
     * @return Returns the "particleDistance" property value.
     */
    public int particleDistance() {
        return this.particleDistance;
    }

    /**
     * Retrieves the "maxSize" property value.
     * @return Returns the "maxSize" property value.
     */
    public int maxSize() {
        return this.maxSize;
    }

    /**
     * Retrieves translation for the "langVisualizerEnabled" text.
     * @return Translation of "langVisualizerEnabled".
     */
    public String getLangVisualizerEnabled() {
        return this.langVisualizerEnabled;
    }

    /**
     * Retrieves translation for the "visualizerDisabled" text.
     * @return Translation of "visualizerDisabled".
     */
    public String getLangVisualizerDisabled() {
        return this.langVisualizerDisabled;
    }

    /**
     * Retrieves translation for the "playersOnly" text.
     * @return Translation of "playersOnly".
     */
    public String getLangPlayersOnly() {
        return this.langPlayersOnly;
    }

    /**
     * Retrieves translation for the "selectionSizeOf" text.
     * @return Translation of "selectionSizeOf".
     */
    public String getLangSelectionSizeOf() {
        return this.langSelectionSizeOf;
    }

    /**
     * Retrieves translation for the "langBlocks" text.
     * @return Translation of "langBlocks".
     */
    public String getLangBlocks() {
        return this.langBlocks;
    }
}
