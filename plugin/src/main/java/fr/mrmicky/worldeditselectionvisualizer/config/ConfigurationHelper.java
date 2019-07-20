package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.fastparticle.ParticleType;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.display.ParticleData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurationHelper {

    private final WorldEditSelectionVisualizer plugin;

    public ConfigurationHelper(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public GlobalSelectionConfig loadGlobalSelectionConfig(SelectionType type) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("visualization." + type.toString().toLowerCase());

        int fadeDelay = section.getInt("fade-delay");
        int maxSelectionSize = section.getInt("max-selection-size");
        SelectionConfig primary = loadSelectionConfig(section.getConfigurationSection("primary"));
        SelectionConfig secondary = loadSelectionConfig(section.getConfigurationSection("secondary"));

        return new GlobalSelectionConfig(fadeDelay, maxSelectionSize, primary, secondary);
    }

    @NotNull
    private SelectionConfig loadSelectionConfig(ConfigurationSection config) {
        double pointsDistance = config.getDouble("points-distance");
        double linesGap = config.getDouble("lines-gap");
        int updateInterval = config.getInt("update-interval");
        int viewDistance = config.getInt("view-distance");
        ParticleData particleData = loadParticle(config.getConfigurationSection("particles"));

        return new SelectionConfig(pointsDistance, linesGap, updateInterval, viewDistance, particleData);
    }

    @NotNull
    private ParticleData loadParticle(ConfigurationSection config) {
        String rawType = config.getString("type");
        ParticleType type = ParticleType.getParticle(rawType);

        if (type == null) {
            plugin.getLogger().warning("Invalid particle type in the config: " + rawType);
            return new ParticleData(ParticleType.REDSTONE);
        }

        return new ParticleData(type, loadParticleData(type.getDataType(), config.getString("data")));
    }

    @SuppressWarnings("deprecation")
    @Nullable
    private Object loadParticleData(Class<?> dataClass, String name) {
        if (dataClass == Color.class) {
            String[] split = name.split(",");
            if (split.length == 3) {
                try {
                    int r = Integer.parseInt(split[0]);
                    int g = Integer.parseInt(split[1]);
                    int b = Integer.parseInt(split[2]);

                    return Color.fromRGB(r, g, b);
                } catch (IllegalArgumentException e) {
                    // ignore, warn below
                }
            } else {
                try {
                    return Color.class.getField(name.toUpperCase()).get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // ignore, warn below
                }
            }

            plugin.getLogger().warning("Invalid particle color in the config: " + name);
            return Color.RED;
        }

        if (dataClass == ItemStack.class) {
            return new ItemStack(getMaterial(name, false));
        }

        if (dataClass == MaterialData.class) {
            return new MaterialData(getMaterial(name, true));
        }

        return null;
    }

    @NotNull
    private Material getMaterial(String type, boolean needBlock) {
        Material material = Material.matchMaterial(type);
        if (material == null) {
            plugin.getLogger().warning("Invalid material for particle in the config: " + type);
            return Material.STONE;
        }

        if (needBlock && !material.isBlock()) {
            plugin.getLogger().warning("The material for particle in the config must be a block: " + type);
            return Material.STONE;
        }

        return material;
    }
}
