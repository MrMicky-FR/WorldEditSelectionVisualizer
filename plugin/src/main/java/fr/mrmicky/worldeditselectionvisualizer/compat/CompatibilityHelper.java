package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.ClipboardAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.RegionAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.ClipboardAdapter7;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.RegionAdapter7;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Helper class to help support multiples Spigot and WorldEdit versions
 */
public class CompatibilityHelper {

    private final WorldEditSelectionVisualizer plugin;

    private final boolean supportOffHand = isOffhandSupported();
    private final boolean supportActionBar = isActionBarSupported();
    private final boolean worldEdit7 = isWorldEdit7();

    private @Nullable Predicate<ItemStack> selectionItemPredicate;

    public CompatibilityHelper(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        plugin.getLogger().info("Using WorldEdit " + getWorldEditVersion() + " api");

        init();
    }

    @SuppressWarnings("deprecation") // WorldEdit 6 support
    public void init() {
        try {
            Field field = LocalConfiguration.class.getField("wandItem");
            LocalConfiguration config = WorldEdit.getInstance().getConfiguration();

            if (field.getType() == int.class) { // WorldEdit 6
                int itemId = field.getInt(config);

                this.selectionItemPredicate = item -> item.getType().getId() == itemId;
                return;
            }

            if (field.getType() == String.class) { // WorldEdit 7
                ItemType itemType = ItemTypes.get((String) field.get(config));
                Material type = itemType != null ? BukkitAdapter.adapt(itemType) : null;

                this.selectionItemPredicate = item -> item.getType() == type;
                return;
            }

            this.plugin.getLogger().warning("Unsupported item type in WorldEdit config, try to update WorldEdit.");
        } catch (ReflectiveOperationException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to get WorldEdit wand item, try to update WorldEdit.", e);
        }
    }

    public RegionAdapter adaptRegion(Region region) {
        return this.worldEdit7 ? new RegionAdapter7(region) : new RegionAdapter6(region);
    }

    public ClipboardAdapter adaptClipboard(Clipboard clipboard) {
        return this.worldEdit7 ? new ClipboardAdapter7(clipboard) : new ClipboardAdapter6(clipboard);
    }

    public int getWorldEditVersion() {
        return this.worldEdit7 ? 7 : 6;
    }

    public boolean isHoldingSelectionItem(@NotNull Player player) {
        return isSelectionItem(getItemInMainHand(player)) || isSelectionItem(getItemInOffHand(player));
    }

    public void sendActionBar(@NotNull Player player, @NotNull String message) {
        if (!this.supportActionBar) {
            player.sendMessage(message);
            return;
        }

        SpigotActionBarAdapter.sendActionBar(player, message);
    }

    public boolean isSelectionItem(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (this.selectionItemPredicate == null) {
            return true;
        }

        return this.selectionItemPredicate.test(item);
    }

    @SuppressWarnings("deprecation") // 1.7.10/1.8 servers support
    private @NotNull ItemStack getItemInMainHand(Player player) {
        return player.getItemInHand();
    }

    private @Nullable ItemStack getItemInOffHand(Player player) {
        if (!this.supportOffHand) {
            return null;
        }

        return player.getInventory().getItemInOffHand();
    }

    private boolean isActionBarSupported() {
        try {
            Class.forName("net.md_5.bungee.api.ChatMessageType");
            Player.class.getMethod("spigot");
            SpigotActionBarAdapter.checkSupported();

            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    private boolean isOffhandSupported() {
        try {
            PlayerInventory.class.getMethod("getItemInOffHand");

            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private boolean isWorldEdit7() {
        try {
            Class.forName("com.sk89q.worldedit.math.Vector3");

            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
