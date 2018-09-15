/*
 * Decompiled with CFR 0_110.
 */

package com.rojel.wesv;

import java.util.EnumMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import fr.mrmicky.fastparticle.ParticleType;

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
	private final JavaPlugin plugin;

	/**
	 * WESV YAML configuration representation.
	 */
	private FileConfiguration config;

	/**
	 * ENUM value of a particle effect used to visually display current
	 * WorldEdit selection.
	 */
	private ParticleType particle;

	/**
	 * ENUM of valid configuration values.
	 */
	private enum ConfigValue {
		/**
		 * Size of a space left between 2 points.
		 */
		GAPBETWEENPOINTS("gapBetweenPoints", 0.5d),
		/**
		 * Size of a vertical space left between 2 points.
		 */
		VERTICALGAP("verticalGap", 1d),
		/**
		 * Interval in which particles should be updated for the MC client.
		 */
		UPDATEPARTICLESINTERVAL("updateParticlesInterval", 5),
		/**
		 * Interval (ms) in which the selection should be updated for the MC
		 * client.
		 */
		UPDATESELECTIONINTERVAL("updateSelectionInterval", 20),
		/**
		 * Whether or not to show cuboid lines.
		 */
		CUBOIDLINES("horizontalLinesForCuboid", true),
		/**
		 * Whether or not to show polygon lines.
		 */
		POLYGONLINES("horizontalLinesForPolygon", true),
		/**
		 * Whether or not to show cylinder lines.
		 */
		CYLINDERLINES("horizontalLinesForCylinder", true),
		/**
		 * Whether or not to show ellipsoid lines.
		 */
		ELLIPSOIDLINES("horizontalLinesForEllipsoid", true),
		/**
		 * Whether or not to check for the WorldEdit tool in hand.
		 */
		CHECKFORAXE("checkForAxe", false),
		/**
		 * Maximum distance to see selection particles from.
		 */
		PARTICLEDISTANCE("particleDistance", 32),
		/**
		 * Maximum size of the visualized selection itself.
		 */
		MAXSIZE("maxSize", 10000),
		/**
		 * Language translation string from config.
		 */
		LANGVISUALIZERENABLED("lang.visualizerEnabled", "Your visualizer has been enabled."),
		/**
		 * Language translation string from config.
		 */
		LANGVISUALIZERDISABLED("lang.visualizerDisabled", "Your visualizer has been disabled."),
		/**
		 * Language translation string from config.
		 */
		LANGPLAYERSONLY("lang.playersOnly", "Only a player can toggle his visualizer."),
		/**
		 * Language translation string from config.
		 */
		LANGSELECTIONSIZEOF("lang.selectionSizeOf", "The visualizer only works with selections up to a size of "),
		/**
		 * Language translation string from config.
		 */
		LANGBLOCKS("lang.blocks", "blocks"),
		/**
		 * Language translation string from config.
		 */
		LANGCONFIGRELOADED("lang.configReloaded", "Configuration for visualizer was reloaded from the disk."),

		/**
		 * Hide particles after a confgured amount of time
		 */
		FADE_DELAY("particleFadeDelay", 0),

		/**
		 * Additional data for some particles (can be a color or a material)
		 */
		PARTICLE_DATA("particleData", "255,0,0");

		/**
		 * The string value of an ENUM.
		 */
		private final String configValue;
		private final Object defaultValue;

		/**
		 * Constructor for String ENUMs.
		 * 
		 * @param value
		 *            String value for the ENUM.
		 */
		ConfigValue(final String value, final Object defaultValue) {
			this.configValue = value;
			this.defaultValue = defaultValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.configValue;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}
	}

	private final EnumMap<ConfigValue, Object> configItems = new EnumMap<>(ConfigValue.class);

	/**
	 * Constructor, takes the WESV plugin instance as a parameter.
	 * 
	 * @param plugin
	 *            WESV plugin instance.
	 */
	public Configuration(final JavaPlugin plugin) {
		this.plugin = plugin;

		for (final ConfigValue values : ConfigValue.values()) {
			configItems.put(values, values.getDefaultValue());
		}
	}

	/**
	 * Loads configuration values from the config.yml YAML file.
	 */
	public void load() {
		this.plugin.saveDefaultConfig();
		this.config = this.plugin.getConfig();

		for (final ConfigValue values : ConfigValue.values()) {
			this.config.addDefault(values.toString(), values.getDefaultValue());
		}

		this.config.options().copyDefaults(true);
		this.plugin.saveConfig();
		this.reloadConfig();
	}

	/**
	 * Loads values from config.yml file into the internal config EnumMap.
	 */
	public void reloadConfig() {
		this.plugin.reloadConfig();
		this.config = this.plugin.getConfig();

		this.particle = this.getParticleEffect(this.config.getString("particleEffect"));

		this.configItems.put(ConfigValue.GAPBETWEENPOINTS,
				this.config.getDouble(ConfigValue.GAPBETWEENPOINTS.toString()));

		this.configItems.put(ConfigValue.VERTICALGAP, this.config.getDouble(ConfigValue.VERTICALGAP.toString()));

		this.configItems.put(ConfigValue.UPDATEPARTICLESINTERVAL,
				this.config.getInt(ConfigValue.UPDATEPARTICLESINTERVAL.toString()));

		this.configItems.put(ConfigValue.UPDATESELECTIONINTERVAL,
				this.config.getInt(ConfigValue.UPDATESELECTIONINTERVAL.toString()));

		this.configItems.put(ConfigValue.CUBOIDLINES, this.config.getBoolean(ConfigValue.CUBOIDLINES.toString()));

		this.configItems.put(ConfigValue.POLYGONLINES, this.config.getBoolean(ConfigValue.POLYGONLINES.toString()));

		this.configItems.put(ConfigValue.CYLINDERLINES, this.config.getBoolean(ConfigValue.CYLINDERLINES.toString()));

		this.configItems.put(ConfigValue.ELLIPSOIDLINES, this.config.getBoolean(ConfigValue.ELLIPSOIDLINES.toString()));

		this.configItems.put(ConfigValue.CHECKFORAXE, this.config.getBoolean(ConfigValue.CHECKFORAXE.toString()));

		this.configItems.put(ConfigValue.PARTICLEDISTANCE, this.config.getInt(ConfigValue.PARTICLEDISTANCE.toString()));

		this.configItems.put(ConfigValue.MAXSIZE, this.config.getInt(ConfigValue.MAXSIZE.toString()));

		// language config
		this.configItems.put(ConfigValue.LANGVISUALIZERENABLED,
				this.config.getString(ConfigValue.LANGVISUALIZERENABLED.toString()));

		this.configItems.put(ConfigValue.LANGVISUALIZERDISABLED,
				this.config.getString(ConfigValue.LANGVISUALIZERDISABLED.toString()));

		this.configItems.put(ConfigValue.LANGPLAYERSONLY,
				this.config.getString(ConfigValue.LANGPLAYERSONLY.toString()));

		this.configItems.put(ConfigValue.LANGSELECTIONSIZEOF,
				this.config.getString(ConfigValue.LANGSELECTIONSIZEOF.toString()));

		this.configItems.put(ConfigValue.LANGBLOCKS, this.config.getString(ConfigValue.LANGBLOCKS.toString()));

		this.configItems.put(ConfigValue.LANGCONFIGRELOADED,
				this.config.getString(ConfigValue.LANGCONFIGRELOADED.toString()));

		this.configItems.put(ConfigValue.FADE_DELAY, this.config.getInt(ConfigValue.FADE_DELAY.toString()));

		this.configItems.put(ConfigValue.PARTICLE_DATA,
				getParticleData(this.config.getString(ConfigValue.PARTICLE_DATA.toString())));
	}

	/**
	 * Retrieves ParticleEffect representation of the given name.
	 * 
	 * @param name
	 *            Name of the particle effect from config.
	 * @return Returns a ParticleEffect representation of the given name.
	 */
	public ParticleType getParticleEffect(final String name) {
		final ParticleType effect = ParticleType.getParticle(name);
		if (effect != null && effect.isCompatibleWithServerVersion()) {
			return effect;
		}
		this.plugin.getLogger().warning("The particle effect set in the configuration file is invalid.");
		return ParticleType.REDSTONE;
	}

	public Object getParticleData(final String name) {
		if (this.particle.getDataType() == Color.class && !name.isEmpty()) {
			final String[] split = name.split(",");
			if (split.length == 3) {
				try {
					final int r = Integer.parseInt(split[0]);
					final int g = Integer.parseInt(split[1]);
					final int b = Integer.parseInt(split[2]);

					return Color.fromRGB(r, g, b);
				} catch (IllegalArgumentException e) {
					this.plugin.getLogger().warning("'" + name + "' is not a valid color: " + e.getMessage());
				}
			}
		} else if (this.particle.getDataType() == MaterialData.class) {
			final Material material = getMaterial(name);
			if (material != null) {
				return new MaterialData(material);
			}
		} else if (this.particle.getDataType() == ItemStack.class) {
			final Material material = getMaterial(name);
			if (material != null) {
				return new ItemStack(material);
			}
		}
		return null;
	}

	private Material getMaterial(String mat) {
		final Material material = Material.matchMaterial(mat);
		if (material == null) {
			this.plugin.getLogger().warning("'" + mat + "' is not a valid material");
		}
		return material;
	}

	/**
	 * Checks whether WESV is enabled for the given player.
	 * 
	 * @param player
	 *            Player to check if WESV is enabled for.
	 * @return Returns true if WESV is enabled for the given player, false
	 *         otherwise.
	 */
	public boolean isEnabled(final Player player) {
		final String path = "players." + player.getUniqueId().toString();
		this.config.addDefault(path, true);
		return this.config.getBoolean(path);
	}

	/**
	 * Enables or disables WESV for the given player.
	 * 
	 * @param player
	 *            Player to enable or disable WESV visualization for.
	 * @param enabled
	 *            Whether to enable (true) or disable (false) WESV for the given
	 *            player.
	 */
	public void setEnabled(final Player player, final boolean enabled) {
		this.config.set("players." + player.getUniqueId().toString(), enabled);
		this.plugin.saveConfig();
	}

	/**
	 * Retrieves the "particle" property value.
	 * 
	 * @return Returns the "particle" property value.
	 */
	public ParticleType getParticle() {
		return this.particle;
	}

	/**
	 * Retrieves the "gapBetweenPoints" property value.
	 * 
	 * @return Returns the "gapBetweenPoints" property value.
	 */
	public double getGapBetweenPoints() {
		return (double) this.configItems.get(ConfigValue.GAPBETWEENPOINTS);
	}

	/**
	 * Retrieves the "verticalGap" property value.
	 * 
	 * @return Returns the "verticalGap" property value.
	 */
	public double getVerticalGap() {
		return (double) this.configItems.get(ConfigValue.VERTICALGAP);
	}

	/**
	 * Retrieves the "updateParticlesInterval" property value.
	 * 
	 * @return Returns the "updateParticlesInterval" property value.
	 */
	public int getUpdateParticlesInterval() {
		return (int) this.configItems.get(ConfigValue.UPDATEPARTICLESINTERVAL);
	}

	/**
	 * Retrieves the "updateSelectionInterval" property value.
	 * 
	 * @return Returns the "updateSelectionInterval" property value.
	 */
	public int getUpdateSelectionInterval() {
		return (int) this.configItems.get(ConfigValue.UPDATESELECTIONINTERVAL);
	}

	/**
	 * Retrieves the "cuboidLines" property value.
	 * 
	 * @return Returns the "cuboidLines" property value.
	 */
	public boolean isCuboidLinesEnabled() {
		return (boolean) this.configItems.get(ConfigValue.CUBOIDLINES);
	}

	/**
	 * Retrieves the "polygonLines" property value.
	 * 
	 * @return Returns the "polygonLines" property value.
	 */
	public boolean isPolygonLinesEnabled() {
		return (boolean) this.configItems.get(ConfigValue.POLYGONLINES);
	}

	/**
	 * Retrieves the "cylinderLines" property value.
	 * 
	 * @return Returns the "cylinderLines" property value.
	 */
	public boolean isCylinderLinesEnabled() {
		return (boolean) this.configItems.get(ConfigValue.CYLINDERLINES);
	}

	/**
	 * Retrieves the "ellipsoidLines" property value.
	 * 
	 * @return Returns the "ellipsoidLines" property value.
	 */
	public boolean isEllipsoidLinesEnabled() {
		return (boolean) this.configItems.get(ConfigValue.ELLIPSOIDLINES);
	}

	/**
	 * Retrieves the "checkForAxe" property value.
	 * 
	 * @return Returns the "checkForAxe" property value.
	 */
	public boolean isCheckForAxeEnabled() {
		return (boolean) this.configItems.get(ConfigValue.CHECKFORAXE);
	}

	/**
	 * Retrieves the "particleDistance" property value.
	 * 
	 * @return Returns the "particleDistance" property value.
	 */
	public int getParticleDistance() {
		return (int) this.configItems.get(ConfigValue.PARTICLEDISTANCE);
	}

	/**
	 * Retrieves the "maxSize" property value.
	 * 
	 * @return Returns the "maxSize" property value.
	 */
	public int getMaxSize() {
		return (int) this.configItems.get(ConfigValue.MAXSIZE);
	}

	/**
	 * Retrieves translation for the "langVisualizerEnabled" text.
	 * 
	 * @return Translation of "langVisualizerEnabled".
	 */
	public String getLangVisualizerEnabled() {
		return color((String) this.configItems.get(ConfigValue.LANGVISUALIZERENABLED));
	}

	/**
	 * Retrieves translation for the "visualizerDisabled" text.
	 * 
	 * @return Translation of "visualizerDisabled".
	 */
	public String getLangVisualizerDisabled() {
		return color((String) this.configItems.get(ConfigValue.LANGVISUALIZERDISABLED));
	}

	/**
	 * Retrieves translation for the "playersOnly" text.
	 * 
	 * @return Translation of "playersOnly".
	 */
	public String getLangPlayersOnly() {
		return color((String) this.configItems.get(ConfigValue.LANGPLAYERSONLY));
	}

	/**
	 * Retrieves translation for the "selectionSizeOf" text.
	 * 
	 * @return Translation of "selectionSizeOf".
	 */
	public String getLangSelectionSizeOf() {
		return color((String) this.configItems.get(ConfigValue.LANGSELECTIONSIZEOF));
	}

	/**
	 * Retrieves translation for the "langBlocks" text.
	 * 
	 * @return Translation of "langBlocks".
	 */
	public String getLangBlocks() {
		return color((String) this.configItems.get(ConfigValue.LANGBLOCKS));
	}

	/**
	 * Retrieves translation for the "configReloaded" text.
	 * 
	 * @return Translation of "configReloaded".
	 */
	public String getConfigReloaded() {
		return (String) this.configItems.get(ConfigValue.LANGCONFIGRELOADED);
	}

	// TODO JavaDoc
	public int getParticleFadeDelay() {
		return (int) this.configItems.get(ConfigValue.FADE_DELAY);
	}

	public Object getParticleData() {
		return this.configItems.get(ConfigValue.PARTICLE_DATA);
	}

	private String color(final String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
