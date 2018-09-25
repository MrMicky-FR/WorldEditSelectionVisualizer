package fr.mrmicky.fastparticle.compatibility;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Legacy particle sender with NMS for 1.7/1.8 servers
 *
 * @author MrMicky
 */
@SuppressWarnings("deprecation")
public class ParticleSenderLegacy extends AbstractParticleSender {

    private static final boolean SERVER_IS_1_8;
    private static final String PACKAGE_NAME_NMS;
    private static final String PACKAGE_NAME_OCB;

    private static final Constructor<?> PACKET_PARTICLE;
    private static final Class<?> ENUM_PARTICLE;

    private static final Method WORLD_GET_HANDLE;
    private static final Method WORLD_SEND_PARTICLE;

    private static final Method PLAYER_GET_HANDLE;
    private static final Field PLAYER_CONNECTION;
    private static final Method SEND_PACKET;

    static {
        String ver = FastParticle.SERVER_VERSION;

        PACKAGE_NAME_NMS = "net.minecraft.server." + ver;
        PACKAGE_NAME_OCB = "org.bukkit.craftbukkit." + ver;
        SERVER_IS_1_8 = ver.startsWith("v1_8_");

        try {
            Class<?> packetParticleClass = getClassNMS("PacketPlayOutWorldParticles");
            Class<?> playerClass = getClassNMS("EntityPlayer");
            Class<?> playerConnectionClass = getClassNMS("PlayerConnection");
            Class<?> packetClass = getClassNMS("Packet");
            Class<?> worldClass = getClassNMS("WorldServer");
            Class<?> entityPlayerClass = getClassNMS("EntityPlayer");

            Class<?> craftPlayerClass = getClassOCB("entity.CraftPlayer");
            Class<?> craftWorldClass = getClassOCB("CraftWorld");

            if (SERVER_IS_1_8) {
                ENUM_PARTICLE = getClassNMS("EnumParticle");
                PACKET_PARTICLE = packetParticleClass.getConstructor(ENUM_PARTICLE, boolean.class, float.class,
                        float.class, float.class, float.class, float.class, float.class, float.class, int.class,
                        int[].class);
                WORLD_SEND_PARTICLE = worldClass.getDeclaredMethod("sendParticles", entityPlayerClass, ENUM_PARTICLE,
                        boolean.class, double.class, double.class, double.class, int.class, double.class, double.class,
                        double.class, double.class, int[].class);
            } else {
                ENUM_PARTICLE = null;
                PACKET_PARTICLE = packetParticleClass.getConstructor(String.class, float.class, float.class, float.class,
                        float.class, float.class, float.class, float.class, int.class);
                WORLD_SEND_PARTICLE = worldClass.getDeclaredMethod("a", String.class, double.class, double.class,
                        double.class, int.class, double.class, double.class, double.class, double.class);
            }

            WORLD_GET_HANDLE = craftWorldClass.getDeclaredMethod("getHandle");
            PLAYER_GET_HANDLE = craftPlayerClass.getDeclaredMethod("getHandle");
            PLAYER_CONNECTION = playerClass.getField("playerConnection");
            SEND_PACKET = playerConnectionClass.getMethod("sendPacket", packetClass);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void spawnParticle(Object receiver, ParticleType particle, double x, double y, double z, int count, double offsetX, double offsetY,
                              double offsetZ, double extra, Object data) {
        try {
            int[] datas = toData(particle, data);

            if (data instanceof Color) {
                Color color = (Color) data;
                if (particle.getDataType() == Color.class) {
                    count = 0;
                    offsetX = color(color.getRed());
                    offsetY = color(color.getGreen());
                    offsetZ = color(color.getBlue());
                    extra = 1.0;
                }
            }

            if (receiver instanceof World) {
                Object worldServer = WORLD_GET_HANDLE.invoke(receiver);

                if (SERVER_IS_1_8) {
                    WORLD_SEND_PARTICLE.invoke(worldServer, null, enumParticleValueOf(particle), true, x, y, z, count, offsetX, offsetY, offsetZ, extra, datas);
                } else {
                    WORLD_SEND_PARTICLE.invoke(worldServer, particle.getName() + (datas.length != 2 ? "" : "_" + datas[0] + "_" + datas[1]), x, y, z,
                            count, offsetX, offsetY, offsetZ, extra);
                }
            } else if (receiver instanceof Player) {
                Object packet;

                if (SERVER_IS_1_8) {
                    packet = PACKET_PARTICLE.newInstance(enumParticleValueOf(particle), true, (float) x, (float) y,
                            (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, datas);
                } else {
                    packet = PACKET_PARTICLE.newInstance(
                            particle.getName() + (datas.length != 2 ? "" : "_" + datas[0] + "_" + datas[1]),
                            (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);
                }

                Object entityPlayer = PLAYER_GET_HANDLE.invoke(receiver);
                Object playerConnection = PLAYER_CONNECTION.get(entityPlayer);
                SEND_PACKET.invoke(playerConnection, packet);
            }
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "[FastParticle] Error on sending particle", e);
        }
    }

    @Override
    public boolean isValidData(Object particle, Object data) {
        return true;
    }

    @Override
    public Object getParticle(ParticleType particle) {
        if (!SERVER_IS_1_8) {
            return particle.getName();
        }

        try {
            return enumParticleValueOf(particle);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object enumParticleValueOf(ParticleType particle) {
        return Enum.valueOf((Class<Enum>) ENUM_PARTICLE, particle.toString().toUpperCase());
    }

    private int[] toData(ParticleType particle, Object data) {
        Class<?> dataType = particle.getDataType();
        if (dataType == ItemStack.class) {
            if (data == null) {
                return SERVER_IS_1_8 ? new int[2] : new int[]{1, 0};
            }

            ItemStack itemStack = (ItemStack) data;
            return new int[]{itemStack.getType().getId(), itemStack.getDurability()};
        }

        if (dataType == MaterialData.class) {
            if (data == null) {
                return SERVER_IS_1_8 ? new int[1] : new int[]{1, 0};
            }

            MaterialData materialData = (MaterialData) data;
            if (SERVER_IS_1_8) {
                return new int[]{materialData.getItemType().getId() + (materialData.getData() << 12)};
            } else {
                return new int[]{materialData.getItemType().getId(), materialData.getData()};
            }
        }

        return new int[0];
    }

    private static Class<?> getClassNMS(String name) throws ClassNotFoundException {
        return Class.forName(PACKAGE_NAME_NMS + "." + name);
    }

    private static Class<?> getClassOCB(String name) throws ClassNotFoundException {
        return Class.forName(PACKAGE_NAME_OCB + "." + name);
    }
}
