package fr.mrmicky.fastparticle;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author MrMicky
 */
@SuppressWarnings("deprecation")
public enum ParticleType {

    // 1.7+
    EXPLOSION_NORMAL("explode"),
    EXPLOSION_LARGE("largeexplode"),
    EXPLOSION_HUGE("hugeexplosion"),
    FIREWORKS_SPARK("fireworksSpark"),
    WATER_BUBBLE("bubble"),
    WATER_SPLASH("splash"),
    WATER_WAKE("wake"),
    SUSPENDED("suspended"),
    SUSPENDED_DEPTH("depthsuspend"),
    CRIT("crit"),
    CRIT_MAGIC("magicCrit"),
    SMOKE_NORMAL("smoke"),
    SMOKE_LARGE("largesmoke"),
    SPELL("spell"),
    SPELL_INSTANT("instantSpell"),
    SPELL_MOB("mobSpell"),
    SPELL_MOB_AMBIENT("mobSpellAmbient"),
    SPELL_WITCH("witchMagic"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    VILLAGER_ANGRY("angryVillager"),
    VILLAGER_HAPPY("happyVillager"),
    TOWN_AURA("townaura"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    FLAME("flame"),
    LAVA("lava"),
    FOOTSTEP("footstep"),
    CLOUD("cloud"),
    REDSTONE("reddust"),
    SNOWBALL("snowballpoof"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    ITEM_CRACK("iconcrack"),
    BLOCK_CRACK("blockcrack"),
    BLOCK_DUST("blockdust"),

    // 1.8+
    BARRIER("barrier", 8),
    WATER_DROP("droplet", 8),
    MOB_APPEARANCE("mobappearance", 8),
    ITEM_TAKE("take", 8),

    // 1.9+
    DRAGON_BREATH("dragonbreath", 9),
    END_ROD("endRod", 9),
    DAMAGE_INDICATOR("damageIndicator", 9),
    SWEEP_ATTACK("sweepAttack", 9),

    // 1.10+
    FALLING_DUST("fallingdust", 10),

    // 1.11+
    TOTEM("totem", 11),
    SPIT("spit", 11),

    // 1.13+
    SQUID_INK("squid_ink", 13),
    BUBBLE_POP("bubble_pop", 13),
    CURRENT_DOWN("current_down", 13),
    BUBBLE_COLUMN_UP("bubble_column_up", 13),
    NAUTILUS("nautilus", 13),
    DOLPHIN("dolphin", 13);

    private static final int SERVER_VERSION_ID;

    static {
        String ver = FastParticle.SERVER_VERSION;
        SERVER_VERSION_ID = Integer.parseInt(ver.charAt(4) == '_' ? Character.toString(ver.charAt(3)) : ver.substring(3, 5));
    }

    private String name;
    private int minimalVersion;

    ParticleType(String name) {
        this(name, -1);
    }

    ParticleType(String name, int minimalVersion) {
        this.name = name;
        this.minimalVersion = minimalVersion;
    }

    public String getName() {
        return name;
    }

    public boolean isCompatibleWithServerVersion() {
        return minimalVersion <= 0 || SERVER_VERSION_ID >= minimalVersion;
    }

    public Class<?> getDataType() {
        switch (this) {
            case ITEM_CRACK:
                return ItemStack.class;
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                return MaterialData.class;
            case REDSTONE:
                return Color.class;
            default:
                return Void.class;
        }
    }

    public static ParticleType getParticle(String particleName) {
        try {
            return ParticleType.valueOf(particleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (ParticleType particle : values()) {
                if (particle.getName().equalsIgnoreCase(particleName)) {
                    return particle;
                }
            }
        }
        return null;
    }
}
