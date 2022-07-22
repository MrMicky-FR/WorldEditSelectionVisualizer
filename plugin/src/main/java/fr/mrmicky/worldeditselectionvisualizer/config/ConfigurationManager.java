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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ConfigurationManager {

    private final WorldEditSelectionVisualizer plugin;

    public ConfigurationManager(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    public @NotNull GlobalSelectionConfig loadGlobalSelectionConfig(SelectionType type) {
        ConfigurationSection section = this.plugin.getConfig()
                .getConfigurationSection("visualization." + type.getName());

        int fadeDelay = section.getInt("fade-delay");
        int max = section.getInt("max-selection-size");
        SelectionConfig primary = loadSelectionConfig(section.getConfigurationSection("primary"));
        SelectionConfig secondary = loadSelectionConfig(section.getConfigurationSection("secondary"));
        SelectionConfig origin = loadSelectionConfig(section.getConfigurationSection("origin"));

        return new GlobalSelectionConfig(fadeDelay, max, primary, secondary, origin);
    }

    private @NotNull SelectionConfig loadSelectionConfig(ConfigurationSection config) {
        return new SelectionConfig(config, this::loadParticle);
    }

    public @NotNull Particle loadParticle(ConfigurationSection config) {
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

    private @Nullable ParticleData loadParticleData(Class<?> dataClass, String name) {
        if (dataClass == ParticleData.DustOptions.class) {
            if (name == null || name.isEmpty()) {
                return ParticleData.createDustOptions(Color.RED, 1);
            }

            try {
                String[] split = name.split(",");

                if (split.length == 3) {
                    int r = Integer.parseInt(split[0]);
                    int g = Integer.parseInt(split[1]);
                    int b = Integer.parseInt(split[2]);

                    return ParticleData.createDustOptions(Color.fromRGB(r, g, b), 1);
                }

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

    private @NotNull Material getMaterial(String type, boolean needBlock) {
        Material material = Material.matchMaterial(type);
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
