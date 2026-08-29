package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.fastparticles.ParticleData;
import fr.mrmicky.fastparticles.ParticleType;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.display.Particle;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public class ConfigurationManager {

    private final WorldEditSelectionVisualizer plugin;

    public ConfigurationManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    public VisualizationConfig loadVisualizationConfig(SelectionType type) {
        ConfigurationSection section = this.plugin.getConfig()
                .getConfigurationSection("visualization." + type.getName());

        int fadeDelay = section.getInt("fade-delay");
        int max = section.getInt("max-selection-size");
        boolean cuboidTopBottom = this.plugin.getConfig().getBoolean("cuboid-top-bottom");
        DisplayConfig primary = loadDisplayConfig(section.getConfigurationSection("primary"));
        DisplayConfig secondary = loadDisplayConfig(section.getConfigurationSection("secondary"));
        DisplayConfig origin = loadDisplayConfig(section.getConfigurationSection("origin"));

        return new VisualizationConfig(fadeDelay, max, cuboidTopBottom, primary, secondary, origin);
    }

    private DisplayConfig loadDisplayConfig(ConfigurationSection config) {
        return new DisplayConfig(config, this::loadParticle);
    }

    public Particle loadParticle(@Nullable ConfigurationSection config) {
        if (config == null) {
            return Particle.FALLBACK;
        }

        String rawType = config.getString("type");

        if (rawType == null) {
            return Particle.FALLBACK;
        }

        try {
            ParticleType type = ParticleType.of(rawType);
            String data = config.getString("data");

            return new Particle(type, loadParticleData(type.getDataType(), data));
        } catch (IllegalArgumentException e) {
            this.plugin.getLogger().warning("Invalid or unsupported particle type in the config: " + rawType);
            return Particle.FALLBACK;
        }
    }

    private @Nullable ParticleData loadParticleData(Class<?> dataClass, @Nullable String name) {
        if (dataClass == ParticleData.DustOptions.class) {
            if (name == null || name.isEmpty()) {
                return ParticleData.createDustOptions(Color.RED, 1);
            }

            try {
                String[] split = name.split(",");

                if (split.length == 3) {
                    int r = Integer.parseInt(split[0].trim());
                    int g = Integer.parseInt(split[1].trim());
                    int b = Integer.parseInt(split[2].trim());

                    return ParticleData.createDustOptions(Color.fromRGB(r, g, b), 1);
                }

                // All public fields in the Color class are colors, so cast is safe
                Color color = (Color) Color.class.getField(name.toUpperCase(Locale.ROOT)).get(null);
                return ParticleData.createDustOptions(color, 1);
            } catch (IllegalArgumentException | ReflectiveOperationException e) {
                this.plugin.getLogger().warning("Invalid particle color in the config: " + name);

                return ParticleData.createDustOptions(Color.RED, 1);
            }
        }

        if (dataClass == ItemStack.class) {
            return ParticleData.of(new ItemStack(getMaterial(name, false)));
        }

        if (dataClass == ParticleData.BlockData.class) {
            return ParticleData.createBlockData(getMaterial(name, true));
        }

        if (dataClass == Void.class) {
            return null;
        }

        throw new IllegalArgumentException("Invalid particle data: " + dataClass.getName());
    }

    private Material getMaterial(@Nullable String type, boolean needBlock) {
        Material material = Material.matchMaterial(type != null ? type : "");
        if (material == null) {
            this.plugin.getLogger().warning("Invalid material for particle in the config: " + type);
            return Material.STONE;
        }

        if (needBlock && !material.isBlock()) {
            this.plugin.getLogger().warning("The material for particle in the config must be a block: " + type);
            return Material.STONE;
        }

        return material;
    }
}
