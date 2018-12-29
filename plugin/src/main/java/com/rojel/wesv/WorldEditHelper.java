package com.rojel.wesv;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.item.ItemTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

public class WorldEditHelper extends BukkitRunnable {

    private final WorldEditSelectionVisualizer plugin;

    private Field wandItemField;
    private boolean useOffHand;

    public WorldEditHelper(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 0, plugin.getCustomConfig().getUpdateSelectionInterval());

        try {
            PlayerInventory.class.getDeclaredMethod("getItemInOffHand");
            useOffHand = true;  // 1.9+ server
        } catch (NoSuchMethodException e) {
            useOffHand = false; // 1.7-1.8 server
        }

        try {
            wandItemField = LocalConfiguration.class.getField("wandItem");
        } catch (NoSuchFieldException e) {
            plugin.getLogger().warning("No field wandItem in LocalConfiguration");
        }
    }

    @Override
    public void run() {
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            if (!plugin.getStorageManager().isEnabled(player)) {
                continue;
            }

            final Region currentRegion = getSelectedRegion(player);

            if (!compareRegion(plugin.getLastSelectedRegions().get(player.getUniqueId()), currentRegion)) {
                if (currentRegion != null) {
                    plugin.getLastSelectedRegions().put(player.getUniqueId(), currentRegion.clone());
                } else {
                    plugin.getLastSelectedRegions().remove(player.getUniqueId());
                }

                plugin.getServer().getPluginManager().callEvent(new WorldEditSelectionChangeEvent(player, currentRegion));

                if (plugin.isSelectionShown(player)) {
                    plugin.showSelection(player);
                }
            }
        }
    }

    public Region getSelectedRegion(final Player player) {
        final LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());

        if (session != null && session.getSelectionWorld() != null) {
            final RegionSelector selector = session.getRegionSelector(session.getSelectionWorld());

            if (selector.isDefined()) {
                try {
                    return selector.getRegion();
                } catch (final IncompleteRegionException e) {
                    plugin.getLogger().warning("Region still incomplete.");
                }
            }
        }
        return null;
    }

    public boolean compareRegion(final Region region1, final Region region2) {
        if (Objects.equals(region1, region2)) {
            return true;
        }

        if (region1 == null || region2 == null || !Objects.equals(region1.getWorld(), region2.getWorld())) {
            return false;
        }

        Iterator<?> regionIterator1 = region1.iterator();
        Iterator<?> regionIterator2 = region2.iterator();

        while (regionIterator1.hasNext()) {
            if (!regionIterator2.hasNext() || !regionIterator1.next().equals(regionIterator2.next())) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean isHoldingSelectionItem(final Player player) {
        final ItemStack item = player.getItemInHand();
        final ItemStack offHandItem = useOffHand ? player.getInventory().getItemInOffHand() : null;

        return isSelectionItem(item) || isSelectionItem(offHandItem);
    }

    @SuppressWarnings("deprecation")
    public boolean isSelectionItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        try {
            if (wandItemField.getType() == int.class) { // WE & FAWE under 1.13

                return item.getType().getId() == wandItemField.getInt(WorldEdit.getInstance().getConfiguration());
            } else if (wandItemField.getType() == String.class) { // WorldEdit 1.13+
                final String wandItem = (String) wandItemField.get(WorldEdit.getInstance().getConfiguration());

                return BukkitAdapter.adapt(item).getType().getId().equals(wandItem);
            } else if (wandItemField.getType() == ItemTypes.class) { // FAWE 1.13+
                final Object wandItemType = wandItemField.get(WorldEdit.getInstance().getConfiguration());

                BaseItemStack baseItem = BukkitAdapter.adapt(item);

                Object itemType = BaseItem.class.getMethod("getType").invoke(baseItem);

                return wandItemType.equals(itemType);
            } else {
                plugin.getLogger().warning("Unknown type for wandItemField: " + wandItemField.getType().getName());
            }
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred on isHoldingSelectionItem", e);
        }
        return true;
    }
}
