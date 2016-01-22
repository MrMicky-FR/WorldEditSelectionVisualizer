/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.rojel.wesv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleSender
implements Listener {
    private final JavaPlugin plugin;
    private final Configuration config;
    private final ProtocolLibHelper protocolLibHelper;
    private final Map<UUID, Collection<Location>> playerParticleMap;

    public ParticleSender(JavaPlugin plugin, Configuration config, ProtocolLibHelper protocolLibHelper) {
        this.plugin = plugin;
        this.config = config;
        this.protocolLibHelper = protocolLibHelper;
        this.playerParticleMap = new HashMap<UUID, Collection<Location>>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.startSending();
    }

    public void setParticlesForPlayer(Player player, Collection<Location> locations) {
        this.playerParticleMap.put(player.getUniqueId(), locations);
        if (locations == null || locations.size() == 0) {
            this.playerParticleMap.remove(player.getUniqueId());
        }
    }

    private void startSending() {
        new BukkitRunnable(){

            @Override
			public void run() {
                int particleDistance = ParticleSender.this.config.particleDistance();
                if (particleDistance > 16 && !ParticleSender.this.protocolLibHelper.canUseProtocolLib()) {
                    particleDistance = 16;
                }
                for (UUID uuid : ParticleSender.this.playerParticleMap.keySet()) {
                    Player player = ParticleSender.this.plugin.getServer().getPlayer(uuid);
                    for (Location loc : ParticleSender.this.playerParticleMap.get(uuid)) {
                        if (!loc.getWorld().equals(player.getLocation().getWorld()) || loc.distance(player.getLocation()) > particleDistance) {
							continue;
						}
                        if (ParticleSender.this.protocolLibHelper.canUseProtocolLib()) {
                            ParticleSender.this.protocolLibHelper.sendParticle(player, loc);
                            continue;
                        }
                        ParticleSender.this.config.particle().display(0.0f, 0.0f, 0.0f, 0.0f, 1, loc, player);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0, this.config.updateParticlesInterval());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.playerParticleMap.remove(event.getPlayer().getUniqueId());
    }

}

