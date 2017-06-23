/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.ProtocolManager
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.wrappers.EnumWrappers
 *  com.comphenix.protocol.wrappers.EnumWrappers$Particle
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */

package com.rojel.wesv;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.darkblade12.particleeffect.ParticleEffect;

public class ProtocolLibHelper {
    private final JavaPlugin      plugin;
    private final Configuration   config;
    private final boolean         protocolLibInstalled;
    private ProtocolManager       protocolManager;
    private EnumWrappers.Particle particleType;

    public ProtocolLibHelper(final JavaPlugin plugin, final Configuration config) {
        this.plugin = plugin;
        this.config = config;
        this.protocolLibInstalled = plugin.getServer().getPluginManager().getPlugin("ProtocolLib") != null;
        if (this.canUseProtocolLib()) {
            this.protocolManager = ProtocolLibrary.getProtocolManager();
            this.particleType = this.fromParticleEffect(config.getParticle());
        }
    }

    public void sendParticle(final Player player, final Location loc) {
        final PacketContainer particle = this.protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        particle.getParticles().write(0, this.particleType);
        particle.getBooleans().write(0, true);
        particle.getFloat().write(0, Float.valueOf((float) loc.getX()));
        particle.getFloat().write(1, Float.valueOf((float) loc.getY()));
        particle.getFloat().write(2, Float.valueOf((float) loc.getZ()));
        try {
            this.protocolManager.sendServerPacket(player, particle);
        } catch (final InvocationTargetException e) {
            this.plugin.getLogger().warning("Failed to send particle.");
        }
    }

    private EnumWrappers.Particle fromParticleEffect(final ParticleEffect effect) {
        final EnumWrappers.Particle particle = EnumWrappers.Particle.getById(effect.getId());
        if (particle != null) {
            return particle;
        }
        return EnumWrappers.Particle.REDSTONE;
    }

    public boolean canUseProtocolLib() {
        return this.protocolLibInstalled && this.config.getUseProtocolLib();
    }

    public boolean isProtocolLibInstalled() {
        return this.protocolLibInstalled;
    }
}
