package fr.mrmicky.fastparticle.compatibility;

import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Particle sender without NMS for 1.9-1.12 servers
 *
 * @author MrMicky
 */
public class ParticleSender extends AbstractParticleSender {

    @Override
    public void spawnParticle(Object receiver, ParticleType particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        Particle bukkitParticle = Particle.valueOf(particle.toString());

        if (data instanceof Color) {
            Color color = (Color) data;
            if (particle.getDataType() == Color.class) {
                count = 0;
                offsetX = color(color.getRed());
                offsetY = color(color.getGreen());
                offsetZ = color(color.getBlue());
                extra = 1.0;
            }
            data = null;
        }

        if (receiver instanceof World) {
            ((World) receiver).spawnParticle(bukkitParticle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
        } else if (receiver instanceof Player) {
            ((Player) receiver).spawnParticle(bukkitParticle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
        }
    }

    @Override
    public Particle getParticle(ParticleType particle) {
        try {
            return Particle.valueOf(particle.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean isValidData(Object particle, Object data) {
        return isValidDataBukkit((Particle) particle, data);
    }

    public boolean isValidDataBukkit(Particle particle, Object data) {
        return particle.getDataType() == Void.class || particle.getDataType().isInstance(data);
    }
}
