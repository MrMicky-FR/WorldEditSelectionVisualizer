package fr.mrmicky.fastparticle;

import fr.mrmicky.fastparticle.compatibility.AbstractParticleSender;
import fr.mrmicky.fastparticle.compatibility.ParticleSender;
import fr.mrmicky.fastparticle.compatibility.ParticleSender1_13;
import fr.mrmicky.fastparticle.compatibility.ParticleSenderLegacy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * An API for display particles on all server versions from 1.7 to 1.13
 * <p>
 * The project is on <a href="https://github.com/MrMicky-FR/FastParticles">GitHub</a>
 *
 * @author MrMicky
 */
public class FastParticle {

    public static final String SERVER_VERSION;

    static {
    	final String name = Bukkit.getServer().getClass().getPackage().getName();
        SERVER_VERSION = name.substring(name.lastIndexOf('.') + 1);
    }

    private static AbstractParticleSender particleSender = getSender();

    private static AbstractParticleSender getSender() {
        try {
            Class.forName("org.bukkit.Particle$DustOptions");
            return new ParticleSender1_13();
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("org.bukkit.Particle");
                return new ParticleSender();
            } catch (ClassNotFoundException e2) {
                return new ParticleSenderLegacy();
            }
        }
    }
    
    private FastParticle() {
    }

    /*
     *
     * Worlds methods
     *
     */
    public static void spawnParticle(World world, ParticleType particle, Location location, int count) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count);
    }

    public static void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count) {
        spawnParticle(world, particle, x, y, z, count, null);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, Location location, int count, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count,
                                         T data) {
        spawnParticle(world, particle, x, y, z, count, 0.0D, 0.0D, 0.0D, data);
    }

    public static void spawnParticle(World world, ParticleType particle, Location location, int count, double offsetX,
                                     double offsetY, double offsetZ) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ);
    }

    public static void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count,
                                     double offsetX, double offsetY, double offsetZ) {
        spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, Location location, int count,
                                         double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, data);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count,
                                         double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, 1.0D, data);
    }

    public static void spawnParticle(World world, ParticleType particle, Location location, int count, double offsetX,
                                     double offsetY, double offsetZ, double extra) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, extra);
    }

    public static void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count,
                                     double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, Location location, int count,
                                         double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(world, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, extra, data);
    }

    public static <T> void spawnParticle(World world, ParticleType particle, double x, double y, double z, int count,
                                         double offsetX, double offsetY, double offsetZ, double extra, T data) {
        if (particleSender == null) {
            return;
        }

        particleSender.spawnParticle(world, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
    }

    /*
     *
     * Player methods
     *
     */
    public static void spawnParticle(Player player, ParticleType particle, Location location, int count) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count);
    }

    public static void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count) {
        spawnParticle(player, particle, x, y, z, count, null);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, Location location, int count, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count,
                                         T data) {
        spawnParticle(player, particle, x, y, z, count, 0.0D, 0.0D, 0.0D, data);
    }

    public static void spawnParticle(Player player, ParticleType particle, Location location, int count, double offsetX,
                                     double offsetY, double offsetZ) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ);
    }

    public static void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count,
                                     double offsetX, double offsetY, double offsetZ) {
        spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, Location location, int count,
                                         double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, data);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count,
                                         double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, 1.0D, data);
    }

    public static void spawnParticle(Player player, ParticleType particle, Location location, int count, double offsetX,
                                     double offsetY, double offsetZ, double extra) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, extra);
    }

    public static void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count,
                                     double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, Location location, int count,
                                         double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(player, particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY,
                offsetZ, extra, data);
    }

    public static <T> void spawnParticle(Player player, ParticleType particle, double x, double y, double z, int count,
                                         double offsetX, double offsetY, double offsetZ, double extra, T data) {
        if (particleSender == null) {
            return;
        }

        particleSender.spawnParticle(player, particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
    }
}
