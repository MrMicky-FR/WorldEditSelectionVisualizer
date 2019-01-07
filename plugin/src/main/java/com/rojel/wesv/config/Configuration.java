package com.rojel.wesv.config;

import com.rojel.wesv.WorldEditSelectionVisualizer;
import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.EnumMap;
import java.util.UUID;

/**
 * YAML plugin configuration retrieval and manipulation class.
 *
 * @author Martin Ambrus
 * @since 1.0a
 */
@SuppressWarnings("deprecation")
public class Configuration {
    /**
     * WESV plugin instance.
     */
    private final WorldEditSelectionVisualizer plugin;

    /**
     * WESV YAML configuration representation.
     */
    private FileConfiguration config;

    /**
     * ENUM of valid configuration values.
     */
    private enum ConfigValue {
        UPDATE_CHECKER("updateChecker", true, boolean.class),
        /**
         * Size of a space left between 2 points.
         */
        GAP_BETWEEN_POINTS("gapBetweenPoints", 0.5d, double.class),
        /**
         * Size of a vertical space left between 2 points.
         */
        VERTICAL_GAP("verticalGap", 1d, double.class),
        /**
         * Interval in which particles should be updated for the MC client.
         */
        UPDATE_PARTICLES_INTERVAL("updateParticlesInterval", 5, int.class),
        /**
         * Interval in which particles should be updated for the MC client.
         */
        UPDATE_CLIPBOARD_PARTICLES_INTERVAL("updateClipboardParticlesInterval", 5, int.class),
        /**
         * Interval (ms) in which the selection should be updated for the MC
         * client.
         */
        UPDATE_SELECTION_INTERVAL("updateSelectionInterval", 20, int.class),
        /**
         * Whether or not to show cuboid lines.
         */
        CUBOID_LINES("horizontalLinesForCuboid", true, boolean.class),
        /**
         * Whether or not to show polygon lines.
         */
        POLYGON_LINES("horizontalLinesForPolygon", true, boolean.class),
        /**
         * Whether or not to show cylinder lines.
         */
        CYLINDER_LINES("horizontalLinesForCylinder", true, boolean.class),
        /**
         * Whether or not to show ellipsoid lines.
         */
        ELLIPSOID_LINES("horizontalLinesForEllipsoid", true, boolean.class),

        CUBOID_TOP_BOTTOM("topAndBottomForCuboid", true, boolean.class),

        CYLINDER_TOP_BOTTOM("topAndBottomForCylinder", true, boolean.class),
        /**
         * Whether or not to check for the WorldEdit tool in hand.
         */
        CHECK_FOR_AXE("checkForAxe", false, boolean.class),

        SHOW_FOR_ALL_PLAYERS("showForAllPlayers", false, boolean.class),

        PARTICLE_TYPE("particleEffect", ParticleType.REDSTONE, ParticleType.class),
        CLIPBOARD_PARTICLE_TYPE("clipboardParticleEffect", ParticleType.VILLAGER_HAPPY, ParticleType.class),
        /**
         * Maximum distance to see selection particles from.
         */
        PARTICLE_DISTANCE("particleDistance", 32, int.class),
        /**
         * Maximum size of the visualized selection itself.
         */
        MAX_SIZE("maxSize", 10000, int.class),
        /**
         * Language translation string from config.
         */
        LANG_VISUALIZER_ENABLED("lang.visualizerEnabled", "&aYour visualizer has been enabled.", String.class),
        /**
         * Language translation string from config.
         */
        LANG_VISUALIZER_DISABLED("lang.visualizerDisabled", "&cYour visualizer has been disabled.", String.class),
        /**
         * Language translation string from config.
         */
        LANG_PLAYERS_ONLY("lang.playersOnly", "&cOnly a player can toggle his visualizer.", String.class),
        /**
         * Language translation string from config.
         */
        LANG_MAX_SELECTION("lang.maxSelection", "&6The visualizer only works with selections up to a size of %blocks% blocks", String.class),
        /**
         * Language translation string from config.
         */
        LANG_CONFIGRELOADED("lang.configReloaded", "&aConfiguration for visualizer was reloaded from the disk.", String.class),
        /**
         * Language translation string from config.
         */
        LANG_NO_PERMISSION("lang.noPermission", "&cYou don't have the permission to use this command.", String.class),
        /**
         * Hide particles after a confgured amount of time
         */
        FADE_DELAY("particleFadeDelay", 0, int.class),
        /**
         * Additional data for some particles (can be a color or a material)
         */
        PARTICLE_DATA("particleData", "255,0,0", String.class),
        CLIPBOARD_PARTICLE_DATA("clipboardParticleData", "255,0,0", String.class);

        private final String configValue;
        private final Object defaultValue;
        private Class<?> type;

        ConfigValue(final String configValue, final Object defaultValue, final Class<?> type) {
            this.configValue = configValue;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public String getConfigValue() {
            return configValue;
        }

        public Class<?> getType() {
            return type;
        }
    }

    private final EnumMap<ConfigValue, Object> configItems = new EnumMap<>(ConfigValue.class);

    /**
     * Constructor, takes the WESV plugin instance as a parameter.
     *
     * @param plugin WESV plugin instance.
     */
    public Configuration(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads configuration values from the config.yml YAML file.
     */
    public void load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        boolean found = false;
        for (final ConfigValue value : ConfigValue.values()) {
            if (config.get(value.getConfigValue(), null) == null) {
                plugin.getLogger().info("Adding '" + value.getConfigValue() + "' to the config");
                found = true;
            }
        }

        ConfigurationSection disabledPlayers = config.getConfigurationSection("players");

        if (disabledPlayers != null) {
            plugin.getLogger().warning("Migrating players from config.yml to players.yml ...");

            for (String key : disabledPlayers.getKeys(false)) {
                if (!disabledPlayers.getBoolean(key)) {
                    try {
                        plugin.getStorageManager().getDisabledPlayers().add(UUID.fromString(key));
                    } catch (IllegalArgumentException ignore) {
                    }
                }
            }

            plugin.getStorageManager().save();

            config.set("players", null);
            found = true;
        }

        if (found) {
            config.options().copyDefaults(true);
            plugin.saveConfig();
        }

        reloadConfig(false);
    }

    /**
     * Loads values from config.yml file into the internal config EnumMap.
     */
    public void reloadConfig(final boolean reload) {
        if (reload) {
            plugin.reloadConfig();
            config = plugin.getConfig();
        }

        for (final ConfigValue value : ConfigValue.values()) {
            if (value.getType() == String.class) {
                configItems.put(value, ChatColor.translateAlternateColorCodes('&', config.getString(value.getConfigValue())));
            } else if (value.getType() == boolean.class) {
                configItems.put(value, config.getBoolean(value.getConfigValue()));
            } else if (value.getType() == int.class) {
                configItems.put(value, config.getInt(value.getConfigValue()));
            } else if (value.getType() == double.class) {
                configItems.put(value, config.getDouble(value.getConfigValue()));
            } else if (value.getType() == ParticleType.class) {
                if (value == ConfigValue.PARTICLE_TYPE) {
                    configItems.put(value, getParticleType(config.getString(value.getConfigValue())));
                } else if (value == ConfigValue.CLIPBOARD_PARTICLE_TYPE) {
                    configItems.put(value, getClipboardParticleType(config.getString(value.getConfigValue())));
                }
            } else {
                configItems.put(value, config.get(value.getConfigValue()));
            }
        }

        configItems.put(ConfigValue.PARTICLE_DATA, getParticleData((String) configItems.get(ConfigValue.PARTICLE_DATA)));
        configItems.put(ConfigValue.CLIPBOARD_PARTICLE_DATA, getClipboardParticleData((String) configItems.get(ConfigValue.CLIPBOARD_PARTICLE_DATA)));
    }

    /**
     * Retrieves ParticleType representation of the given name.
     *
     * @param name Name of the particle type from config.
     * @return Returns a ParticleType representation of the given name.
     */
    public ParticleType getParticleType(final String name) {
        final ParticleType effect = ParticleType.getParticle(name);
        if (effect != null && effect.isCompatibleWithServerVersion()) {
            return effect;
        }
        plugin.getLogger().warning("The particle effect set in the configuration file is invalid.");
        return ParticleType.REDSTONE;
    }

    /**
     * Retrieves ParticleType representation of the given name in terms of the clipboard.
     *
     * @param name Name of the particle type from config.
     * @return Returns a ParticleType representation of the given name.
     */
    public ParticleType getClipboardParticleType(final String name) {
        final ParticleType effect = ParticleType.getParticle(name);
        if (effect != null && effect.isCompatibleWithServerVersion()) {
            return effect;
        }
        plugin.getLogger().warning("The particle effect set for the clipboard in the configuration file is invalid.");
        return ParticleType.VILLAGER_HAPPY;
    }

    public Object getParticleData(final String name) {
        final ParticleType particle = getParticle();
        if (particle.getDataType() == Color.class && !name.isEmpty()) {
            final String[] split = name.split(",");
            if (split.length == 3) {
                try {
                    final int r = Integer.parseInt(split[0]);
                    final int g = Integer.parseInt(split[1]);
                    final int b = Integer.parseInt(split[2]);

                    return Color.fromRGB(r, g, b);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("'" + name + "' is not a valid color: " + e.getMessage());
                }
            }
        } else if (particle.getDataType() == MaterialData.class) {
            final Material material = getMaterial(name);
            if (material != null) {
                return new MaterialData(material);
            }
        } else if (particle.getDataType() == ItemStack.class) {
            final Material material = getMaterial(name);
            if (material != null) {
                return new ItemStack(material);
            }
        }
        return null;
    }

    public Object getClipboardParticleData(final String name) {
        final ParticleType particle = getClipboardParticle();
        if (particle.getDataType() == Color.class && !name.isEmpty()) {
            final String[] split = name.split(",");
            if (split.length == 3) {
                try {
                    final int r = Integer.parseInt(split[0]);
                    final int g = Integer.parseInt(split[1]);
                    final int b = Integer.parseInt(split[2]);

                    return Color.fromRGB(r, g, b);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("'" + name + "' is not a valid color: " + e.getMessage());
                }
            }
        } else if (particle.getDataType() == MaterialData.class) {
            final Material material = getMaterial(name);
            if (material != null) {
                return new MaterialData(material);
            }
        } else if (particle.getDataType() == ItemStack.class) {
            final Material material = getMaterial(name);
            if (material != null) {
                return new ItemStack(material);
            }
        }
        return null;
    }

    private Material getMaterial(final String mat) {
        final Material material = Material.matchMaterial(mat);
        if (material == null) {
            plugin.getLogger().warning("'" + mat + "' is not a valid material");
        }
        return material;
    }

    /**
     * Retrieves the "particle" property value.
     *
     * @return Returns the "particle" property value.
     */
    public ParticleType getParticle() {
        return (ParticleType) configItems.get(ConfigValue.PARTICLE_TYPE);
    }

    /**
     * Sets the "particle" property value.
     */
    public void setParticle(final ParticleType var) {
        configItems.put(ConfigValue.PARTICLE_TYPE, var);
    }

    /**
     * Retrieves the "particleClipboard" property value.
     *
     * @return Returns the "particleClipboard" property value.
     */
    public ParticleType getClipboardParticle() {
        return (ParticleType) configItems.get(ConfigValue.CLIPBOARD_PARTICLE_TYPE);
    }

    /**
     * Sets the "particleClipboard" property value.
     */
    public void setClipboardParticle(final ParticleType var) {
        configItems.put(ConfigValue.CLIPBOARD_PARTICLE_TYPE, var);
    }

    /**
     * Retrieves the "updateChecker" property value.
     *
     * @return Returns the "updateChecker" property value.
     */
    public boolean isUpdateCheckerEnabled() {
        return (boolean) configItems.get(ConfigValue.UPDATE_CHECKER);
    }

    /**
     * Sets the "updateChecker" property value.
     */
    public void setUpdateCheckerEnabled(final boolean var) {
        configItems.put(ConfigValue.UPDATE_CHECKER, var);
    }

    /**
     * Retrieves the "gapBetweenPoints" property value.
     *
     * @return Returns the "gapBetweenPoints" property value.
     */
    public double getGapBetweenPoints() {
        return (double) configItems.get(ConfigValue.GAP_BETWEEN_POINTS);
    }

    /**
     * Sets the "gapBetweenPoints" property value.
     */
    public void setGapBetweenPoints(final double var) {
        configItems.put(ConfigValue.GAP_BETWEEN_POINTS, var);
    }

    /**
     * Retrieves the "verticalGap" property value.
     *
     * @return Returns the "verticalGap" property value.
     */
    public double getVerticalGap() {
        return (double) configItems.get(ConfigValue.VERTICAL_GAP);
    }

    /**
     * Sets the "verticalGap" property value.
     */
    public void setVerticalGap(final double var) {
        configItems.put(ConfigValue.VERTICAL_GAP, var);
    }

    /**
     * Retrieves the "updateParticlesInterval" property value.
     *
     * @return Returns the "updateParticlesInterval" property value.
     */
    public int getUpdateParticlesInterval() {
        return (int) configItems.get(ConfigValue.UPDATE_PARTICLES_INTERVAL);
    }

    /**
     * Sets the "updateParticlesInterval" property value.
     */
    public void setUpdateParticlesInterval(final int var) {
        configItems.put(ConfigValue.UPDATE_PARTICLES_INTERVAL, var);
    }

    /**
     * Retrieves the "updateParticlesInterval" property value.
     *
     * @return Returns the "updateParticlesInterval" property value.
     */
    public int getUpdateClipboardParticlesInterval() {
        return (int) configItems.get(ConfigValue.UPDATE_PARTICLES_INTERVAL);
    }

    /**
     * Sets the "updateParticlesInterval" property value.
     */
    public void setUpdateClipboardParticlesInterval(final int var) {
        configItems.put(ConfigValue.UPDATE_PARTICLES_INTERVAL, var);
    }

    /**
     * Retrieves the "updateSelectionInterval" property value.
     *
     * @return Returns the "updateSelectionInterval" property value.
     */
    public int getUpdateSelectionInterval() {
        return (int) configItems.get(ConfigValue.UPDATE_SELECTION_INTERVAL);
    }

    /**
     * Sets the "updateSelectionInterval" property value.
     */
    public void setUpdateSelectionInterval(final int var) {
        configItems.put(ConfigValue.UPDATE_SELECTION_INTERVAL, var);
    }

    /**
     * Retrieves the "cuboidLines" property value.
     *
     * @return Returns the "cuboidLines" property value.
     */
    public boolean isCuboidLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.CUBOID_LINES);
    }

    /**
     * Sets the "cuboidLines" property value.
     */
    public void setCuboidLinesEnabled(final boolean var) {
        configItems.put(ConfigValue.CUBOID_LINES, var);
    }

    /**
     * Retrieves the "polygonLines" property value.
     *
     * @return Returns the "polygonLines" property value.
     */
    public boolean isPolygonLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.POLYGON_LINES);
    }

    /**
     * Sets the "polygonLines" property value.
     */
    public void setPolygonLinesEnabled(final boolean var) {
        configItems.put(ConfigValue.POLYGON_LINES, var);
    }

    /**
     * Retrieves the "cylinderLines" property value.
     *
     * @return Returns the "cylinderLines" property value.
     */
    public boolean isCylinderLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.CYLINDER_LINES);
    }

    /**
     * Sets the "cylinderLines" property value.
     */
    public void setCylinderLinesEnabled(final boolean var) {
        configItems.put(ConfigValue.CYLINDER_LINES, var);
    }

    /**
     * Retrieves the "ellipsoidLines" property value.
     *
     * @return Returns the "ellipsoidLines" property value.
     */
    public boolean isEllipsoidLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.ELLIPSOID_LINES);
    }

    /**
     * Sets the "ellipsoidLines" property value.
     */
    public void setEllipsoidLinesEnabled(final boolean var) {
        configItems.put(ConfigValue.ELLIPSOID_LINES, var);
    }

    /**
     * Retrieves the "topAndBottomForCuboid" property value.
     *
     * @return Returns the "topAndBottomForCuboid" property value.
     */
    public boolean isCuboidTopAndBottomEnabled() {
        return (boolean) configItems.get(ConfigValue.CUBOID_TOP_BOTTOM);
    }

    /**
     * Sets the "topAndBottomForCuboid" property value.
     */
    public void setCuboidTopAndBottomEnabled(final boolean var) {
        configItems.put(ConfigValue.CUBOID_TOP_BOTTOM, var);
    }

    /**
     * Retrieves the "topAndBottomForCylinder" property value.
     *
     * @return Returns the "topAndBottomForCylinder" property value.
     */
    public boolean isCylinderTopAndBottomEnabled() {
        return (boolean) configItems.get(ConfigValue.CYLINDER_TOP_BOTTOM);
    }

    /**
     * Sets the "topAndBottomForCylinder" property value.
     */
    public void setCylinderTopAndBottomEnabled(final boolean var) {
        configItems.put(ConfigValue.CYLINDER_TOP_BOTTOM, var);
    }

    /**
     * Retrieves the "checkForAxe" property value.
     *
     * @return Returns the "checkForAxe" property value.
     */
    public boolean isCheckForAxeEnabled() {
        return (boolean) configItems.get(ConfigValue.CHECK_FOR_AXE);
    }

    /**
     * Sets the "checkForAxe" property value.
     */
    public void setCheckForAxeEnabled(final boolean var) {
        configItems.put(ConfigValue.CHECK_FOR_AXE, var);
    }

    /**
     * Retrieves the "showForAllPlayers" property value.
     *
     * @return Returns the "showForAllPlayers" property value.
     */
    public boolean isShowForAllPlayersEnabled() {
        return (boolean) configItems.get(ConfigValue.SHOW_FOR_ALL_PLAYERS);
    }

    /**
     * Sets the "showForAllPlayers" property value.
     */
    public void setShowForAllPlayersEnabled(final boolean var) {
        configItems.put(ConfigValue.SHOW_FOR_ALL_PLAYERS, var);
    }

    /**
     * Retrieves the "particleDistance" property value.
     *
     * @return Returns the "particleDistance" property value.
     */
    public int getParticleDistance() {
        return (int) configItems.get(ConfigValue.PARTICLE_DISTANCE);
    }

    /**
     * Sets the "particleDistance" property value.
     */
    public void setParticleDsettance(final int var) {
        configItems.put(ConfigValue.PARTICLE_DISTANCE, var);
    }

    /**
     * Retrieves the "maxSize" property value.
     *
     * @return Returns the "maxSize" property value.
     */
    public int getMaxSize() {
        return (int) configItems.get(ConfigValue.MAX_SIZE);
    }

    /**
     * Sets the "maxSize" property value.
     */
    public void setMaxSize(final int var) {
        configItems.put(ConfigValue.MAX_SIZE, var);
    }

    /**
     * Retrieves translation for the "langVisualizerEnabled" text.
     *
     * @return Translation of "langVisualizerEnabled".
     */
    public String getLangVisualizerEnabled() {
        return (String) configItems.get(ConfigValue.LANG_VISUALIZER_ENABLED);
    }

    /**
     * Sets translation for the "langVisualizerEnabled" text.
     */
    public void setLangVsetualizerEnabled(final String var) {
        configItems.put(ConfigValue.LANG_VISUALIZER_ENABLED, var);
    }

    /**
     * Retrieves translation for the "visualizerDisabled" text.
     *
     * @return Translation of "visualizerDisabled".
     */
    public String getLangVisualizerDisabled() {
        return (String) configItems.get(ConfigValue.LANG_VISUALIZER_DISABLED);
    }

    /**
     * Sets translation for the "visualizerDisabled" text.
     */
    public void setLangVsetualizerDsetabled(final String var) {
        configItems.put(ConfigValue.LANG_VISUALIZER_DISABLED, var);
    }

    /**
     * Retrieves translation for the "playersOnly" text.
     *
     * @return Translation of "playersOnly".
     */
    public String getLangPlayersOnly() {
        return (String) configItems.get(ConfigValue.LANG_PLAYERS_ONLY);
    }

    /**
     * Sets translation for the "playersOnly" text.
     */
    public void setLangPlayersOnly(final String var) {
        configItems.put(ConfigValue.LANG_PLAYERS_ONLY, var);
    }

    /**
     * Retrieves translation for the "maxSelection" text.
     *
     * @return Translation of "maxSelection".
     */
    public String getLangMaxSelection() {
        return (String) configItems.get(ConfigValue.LANG_MAX_SELECTION);
    }

    /**
     * Sets translation for the "maxSelection" text.
     */
    public void setLangMaxSelection(final String var) {
        configItems.put(ConfigValue.LANG_MAX_SELECTION, var);
    }

    /**
     * Retrieves translation for the "maxSelection" text.
     *
     * @return Translation of "maxSelection".
     */
    public String getLangNoPermission() {
        return (String) configItems.get(ConfigValue.LANG_NO_PERMISSION);
    }

    /**
     * Sets translation for the "maxSelection" text.
     */
    public void setLangNoPermsetsion(final String var) {
        configItems.put(ConfigValue.LANG_NO_PERMISSION, var);
    }

    /**
     * Retrieves translation for the "configReloaded" text.
     *
     * @return Translation of "configReloaded".
     */
    public String getConfigReloaded() {
        return (String) configItems.get(ConfigValue.LANG_CONFIGRELOADED);
    }

    /**
     * Sets translation for the "configReloaded" text.
     */
    public void setConfigReloaded(final String var) {
        configItems.put(ConfigValue.LANG_CONFIGRELOADED, var);
    }

    /**
     * Retrieves the "particleFadeDelay" property value.
     *
     * @return Returns the "particleFadeDelay" property value.
     */
    public int getParticleFadeDelay() {
        return (int) configItems.get(ConfigValue.FADE_DELAY);
    }

    /**
     * Sets the "particleFadeDelay" property value.
     */
    public void setParticleFadeDelay(final int var) {
        configItems.put(ConfigValue.FADE_DELAY, var);
    }

    /**
     * Retrieves the "particleData" property value.
     *
     * @return Returns the "particleData" property value.
     */
    public Object getParticleData() {
        return configItems.get(ConfigValue.PARTICLE_DATA);
    }

    /**
     * Sets the "particleData" property value.
     */
    public void setParticleData(final Object var) {
        configItems.put(ConfigValue.PARTICLE_DATA, var);
    }

    /**
     * Retrieves the "clipboardParticleData" property value.
     *
     * @return Returns the "clipboardParticleData" property value.
     */
    public Object getClipboardParticleData() {
        return configItems.get(ConfigValue.CLIPBOARD_PARTICLE_DATA);
    }

    /**
     * Sets the "clipboardParticleData" property value.
     */
    public void setClipboardParticleData(final Object var) {
        configItems.put(ConfigValue.CLIPBOARD_PARTICLE_DATA, var);
    }
}
