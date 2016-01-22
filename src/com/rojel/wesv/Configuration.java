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
    private JavaPlugin plugin;
    private FileConfiguration config;
    private ParticleEffect particle;
    private double gapBetweenPoints;
    private double verticalGap;
    private int updateParticlesInterval;
    private int updateSelectionInterval;
    private boolean cuboidLines;
    private boolean polygonLines;
    private boolean cylinderLines;
    private boolean ellipsoidLines;
    private boolean checkForAxe;
    private Material selectionItem;
    private int particleDistance;
    private boolean useProtocolLib;
    private int maxSize;

    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

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
    }

    public ParticleEffect getParticleEffect(String name) {
        ParticleEffect effect = ParticleEffect.fromName(name);
        if (effect != null) {
            return effect;
        }
        this.plugin.getLogger().warning("The particle effect set in the configuration file is invalid.");
        return ParticleEffect.REDSTONE;
    }

    public Material getSelectionItem(String name) {
        Material selectionItem = Material.getMaterial(name);
        if (selectionItem != null) {
            return selectionItem;
        }
        this.plugin.getLogger().warning("The selection item set in the configuration file is invalid.");
        return Material.WOOD_AXE;
    }

    public boolean isEnabled(Player player) {
        String path = "players." + player.getUniqueId().toString();
        this.config.addDefault(path, true);
        return this.config.getBoolean(path);
    }

    public void setEnabled(Player player, boolean enabled) {
        this.config.set("players." + player.getUniqueId().toString(), enabled);
        this.plugin.saveConfig();
    }

    public ParticleEffect particle() {
        return this.particle;
    }

    public double gapBetweenPoints() {
        return this.gapBetweenPoints;
    }

    public double verticalGap() {
        return this.verticalGap;
    }

    public int updateParticlesInterval() {
        return this.updateParticlesInterval;
    }

    public int updateSelectionInterval() {
        return this.updateSelectionInterval;
    }

    public boolean cuboidLines() {
        return this.cuboidLines;
    }

    public boolean polygonLines() {
        return this.polygonLines;
    }

    public boolean cylinderLines() {
        return this.cylinderLines;
    }

    public boolean ellipsoidLines() {
        return this.ellipsoidLines;
    }

    public boolean checkForAxe() {
        return this.checkForAxe;
    }

    public Material selectionItem() {
        return this.selectionItem;
    }

    public boolean useProtocolLib() {
        return this.useProtocolLib;
    }

    public int particleDistance() {
        return this.particleDistance;
    }

    public int maxSize() {
        return this.maxSize;
    }
}

