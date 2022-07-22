package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
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
import java.util.logging.Level;

/**
 * Helper class to help support multiples Spigot and WorldEdit versions
 */
public class CompatibilityHelper {

    private final WorldEditSelectionVisualizer plugin;

    private final boolean supportOffHand = isOffhandSupported();
    private final boolean supportActionBar = isActionBarSupported();
    private final boolean worldEdit7 = isWorldEdit7();

    private @Nullable Field wandItemField;

    public CompatibilityHelper(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        plugin.getLogger().info("Using WorldEdit " + getWorldEditVersion() + " api");

        try {
            this.wandItemField = LocalConfiguration.class.getField("wandItem");
        } catch (NoSuchFieldException e) {
            plugin.getLogger().warning("No field 'wandItem' in LocalConfiguration");
        }

        if (this.wandItemField != null
                && this.wandItemField.getType() != int.class
                && this.wandItemField.getType() != String.class) {
            plugin.getLogger().warning("Unsupported WorldEdit configuration, try to update WorldEdit (and FAWE if you have it)");

            this.wandItemField = null;
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

    @SuppressWarnings("deprecation") // WorldEdit 6 support
    public boolean isSelectionItem(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (this.wandItemField == null) {
            return true;
        }

        try {
            LocalConfiguration config = WorldEdit.getInstance().getConfiguration();

            if (this.wandItemField.getType() == int.class) { // WorldEdit 6
                return item.getType().getId() == this.wandItemField.getInt(config);
            }

            if (this.wandItemField.getType() == String.class) { // WorldEdit 7
                String wandItem = (String) this.wandItemField.get(config);

                return BukkitAdapter.adapt(item).getType().equals(ItemTypes.get(wandItem));
            }
        } catch (ReflectiveOperationException e) {
            this.plugin.getLogger().log(Level.WARNING, "An error occurred on isHoldingSelectionItem", e);
        }

        return true;
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
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
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
