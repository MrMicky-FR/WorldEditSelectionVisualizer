package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.ClipboardAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.RegionAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.ClipboardAdapter7;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.RegionAdapter7;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 * Helper class to help supporting multiples Spigot and WorldEdit versions
 */
public class CompatibilityHelper {

    private final WorldEditSelectionVisualizer plugin;

    private final boolean supportOffHand = isOffhandSupported();
    private final boolean supportActionBar = isActionBarSupported();

    private final boolean worldEdit7 = isWorldEdit7();

    private boolean supportFawe;

    @Nullable
    private Field wandItemField;

    public CompatibilityHelper(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        plugin.getLogger().info("Using WorldEdit " + getWorldEditVersion() + " api");

        if (Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            supportFawe = true;
            plugin.getLogger().info("FastAsyncWorldEdit support enabled");
        }

        try {
            wandItemField = LocalConfiguration.class.getField("wandItem");
        } catch (NoSuchFieldException e) {
            plugin.getLogger().warning("No field 'wandItem' in LocalConfiguration");
        }

        if (wandItemField != null && wandItemField.getType() != int.class && wandItemField.getType() != String.class) {
            plugin.getLogger().warning("Unsupported WorldEdit configuration, try to update WorldEdit (and FAWE if you have it)");

            wandItemField = null;
        }
    }

    public RegionAdapter adaptRegion(Region region) {
        return worldEdit7 ? new RegionAdapter7(region) : new RegionAdapter6(region);
    }

    public ClipboardAdapter adaptClipboard(Clipboard clipboard) {
        return worldEdit7 ? new ClipboardAdapter7(clipboard) : new ClipboardAdapter6(clipboard);
    }

    public int getWorldEditVersion() {
        return worldEdit7 ? 7 : 6;
    }

    public boolean isHoldingSelectionItem(Player player) {
        return isSelectionItem(getItemInMainHand(player)) || isSelectionItem(getItemInOffHand(player));
    }

    public boolean isUsingFawe() {
        return supportFawe;
    }

    public void sendActionBar(Player player, String message) {
        if (!supportActionBar) {
            player.sendMessage(message);
            return;
        }

        SpigotActionBarAdapter.sendActionBar(player, message);
    }

    public boolean isSelectionItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        if (wandItemField == null) {
            return true;
        }

        try {
            if (wandItemField.getType() == int.class) { // WorldEdit under 1.13
                //noinspection deprecation
                return item.getType().getId() == wandItemField.getInt(WorldEdit.getInstance().getConfiguration());
            }

            if (wandItemField.getType() == String.class) { // WorldEdit 1.13+
                String wandItem = (String) wandItemField.get(WorldEdit.getInstance().getConfiguration());

                return BukkitAdapter.adapt(item).getType().getId().equals(wandItem);
            }

        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred on isHoldingSelectionItem", e);
        }

        return true;
    }

    private ItemStack getItemInMainHand(Player player) {
        //noinspection deprecation - for 1.7/1.8 servers
        return player.getItemInHand();
    }

    private ItemStack getItemInOffHand(Player player) {
        if (!supportOffHand) {
            return null;
        }
        return player.getInventory().getItemInOffHand();
    }

    private boolean isActionBarSupported() {
        try {
            Player.class.getMethod("spigot");
            SpigotActionBarAdapter.checkSupported();

            return true;
        } catch (NoSuchMethodException e) {
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
