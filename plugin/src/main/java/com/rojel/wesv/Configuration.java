package com.rojel.wesv;

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
        GAP_BETWEEN_POINTS("gapBetweenPoints", 0.5, double.class),
        /**
         * Size of a vertical space left between 2 points.
         */
        VERTICAL_GAP("verticalGap", 1.0, double.class),
        /**
         * Interval in which particles should be updated for the MC client.
         */
        UPDATE_PARTICLES_INTERVAL("updateParticlesInterval", 5, int.class),
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

        PARTICLE_TYPE("particleEffect", ParticleType.REDSTONE, ParticleType.class),
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
        PARTICLE_DATA("particleData", "255,0,0", String.class);

        private final String configValue;
        private final Object defaultValue;
        private final Class<?> type;

        ConfigValue(String configValue, Object defaultValue, Class<?> type) {
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
    public Configuration(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads configuration values from the config.yml YAML file.
     */
    public void load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        boolean found = false;
        for (ConfigValue value : ConfigValue.values()) {
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
    public void reloadConfig(boolean reload) {
        if (reload) {
            plugin.reloadConfig();
            config = plugin.getConfig();
        }

        for (ConfigValue value : ConfigValue.values()) {
            if (value.getType() == String.class) {
                configItems.put(value, ChatColor.translateAlternateColorCodes('&', config.getString(value.getConfigValue())));
            } else if (value.getType() == boolean.class) {
                configItems.put(value, config.getBoolean(value.getConfigValue()));
            } else if (value.getType() == int.class) {
                configItems.put(value, config.getInt(value.getConfigValue()));
            } else if (value.getType() == double.class) {
                configItems.put(value, config.getDouble(value.getConfigValue()));
            } else if (value.getType() == ParticleType.class) {
                configItems.put(value, getParticleType(config.getString(value.getConfigValue())));
            } else {
                configItems.put(value, config.get(value.getConfigValue()));
            }
        }

        configItems.put(ConfigValue.PARTICLE_DATA, getParticleData((String) configItems.get(ConfigValue.PARTICLE_DATA)));
    }

    private ParticleType getParticleType(String name) {
        ParticleType effect = ParticleType.getParticle(name);
        if (effect != null && effect.isCompatibleWithServerVersion()) {
            return effect;
        }
        plugin.getLogger().warning("The particle effect in the config is invalid.");
        return ParticleType.REDSTONE;
    }

    private Object getParticleData(String name) {
        ParticleType particle = getParticle();
        if (particle.getDataType() == Color.class) {
            String[] split = name.split(",");
            if (split.length == 3) {
                try {
                    int r = Integer.parseInt(split[0]);
                    int g = Integer.parseInt(split[1]);
                    int b = Integer.parseInt(split[2]);

                    return Color.fromRGB(r, g, b);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("'" + name + "' is not a valid color: " + e.getMessage());
                }
            }
            return Color.RED;
        } else if (particle.getDataType() == MaterialData.class) {
            return new MaterialData(getMaterial(name, true));
        } else if (particle.getDataType() == ItemStack.class) {
            return new ItemStack(getMaterial(name, false));
        }
        return null;
    }

    private Material getMaterial(String mat, boolean needBlock) {
        Material material = Material.matchMaterial(mat);
        if (material == null || (needBlock && !material.isBlock())) {
            plugin.getLogger().warning("'" + mat + "' is not a valid material" + (needBlock ? " or is not a block" : ""));
            return Material.STONE;
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
     * Retrieves the "gapBetweenPoints" property value.
     *
     * @return Returns the "gapBetweenPoints" property value.
     */
    public boolean isUpdateCheckerEnabled() {
        return (boolean) configItems.get(ConfigValue.UPDATE_CHECKER);
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
     * Retrieves the "verticalGap" property value.
     *
     * @return Returns the "verticalGap" property value.
     */
    public double getVerticalGap() {
        return (double) configItems.get(ConfigValue.VERTICAL_GAP);
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
     * Retrieves the "updateSelectionInterval" property value.
     *
     * @return Returns the "updateSelectionInterval" property value.
     */
    public int getUpdateSelectionInterval() {
        return (int) configItems.get(ConfigValue.UPDATE_SELECTION_INTERVAL);
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
     * Retrieves the "polygonLines" property value.
     *
     * @return Returns the "polygonLines" property value.
     */
    public boolean isPolygonLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.POLYGON_LINES);
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
     * Retrieves the "ellipsoidLines" property value.
     *
     * @return Returns the "ellipsoidLines" property value.
     */
    public boolean isEllipsoidLinesEnabled() {
        return (boolean) configItems.get(ConfigValue.ELLIPSOID_LINES);
    }

    public boolean isCuboidTopAndBottomEnabled() {
        return (boolean) configItems.get(ConfigValue.CUBOID_TOP_BOTTOM);
    }

    public boolean isCylinderTopAndBottomEnabled() {
        return (boolean) configItems.get(ConfigValue.CYLINDER_TOP_BOTTOM);
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
     * Retrieves the "particleDistance" property value.
     *
     * @return Returns the "particleDistance" property value.
     */
    public int getParticleDistance() {
        return (int) configItems.get(ConfigValue.PARTICLE_DISTANCE);
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
     * Retrieves translation for the "langVisualizerEnabled" text.
     *
     * @return Translation of "langVisualizerEnabled".
     */
    public String getLangVisualizerEnabled() {
        return (String) configItems.get(ConfigValue.LANG_VISUALIZER_ENABLED);
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
     * Retrieves translation for the "playersOnly" text.
     *
     * @return Translation of "playersOnly".
     */
    public String getLangPlayersOnly() {
        return (String) configItems.get(ConfigValue.LANG_PLAYERS_ONLY);
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
     * Retrieves translation for the "maxSelection" text.
     *
     * @return Translation of "maxSelection".
     */
    public String getLangNoPermission() {
        return (String) configItems.get(ConfigValue.LANG_NO_PERMISSION);
    }

    /**
     * Retrieves translation for the "configReloaded" text.
     *
     * @return Translation of "configReloaded".
     */
    public String getConfigReloaded() {
        return (String) configItems.get(ConfigValue.LANG_CONFIGRELOADED);
    }

    // TODO JavaDoc
    public int getParticleFadeDelay() {
        return (int) configItems.get(ConfigValue.FADE_DELAY);
    }

    public Object getParticleData() {
        return configItems.get(ConfigValue.PARTICLE_DATA);
    }
}
