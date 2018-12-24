package com.rojel.wesv;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.logging.Level;

public class WorldEditHelper extends BukkitRunnable {

    private final WorldEditSelectionVisualizer plugin;
    private final WorldEditPlugin we;
    private boolean useOffHand;

    public WorldEditHelper(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");

        runTaskTimer(plugin, 0, plugin.getCustomConfig().getUpdateSelectionInterval());

        try {
            PlayerInventory.class.getDeclaredMethod("getItemInOffHand");
            useOffHand = true;  // 1.9+ server
        } catch (NoSuchMethodException e) {
            useOffHand = false; // 1.7-1.8 server
        }
    }

    @Override
    public void run() {
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            if (!plugin.getCustomConfig().isEnabled(player)) {
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
        RegionSelector selector;
        final LocalSession session = we.getWorldEdit().getSessionManager().findByName(player.getName());

        if (session != null && session.getSelectionWorld() != null
                && (selector = session.getRegionSelector(session.getSelectionWorld())).isDefined()) {
            try {
                return selector.getRegion();
            } catch (final IncompleteRegionException e) {
                plugin.getLogger().warning("Region still incomplete.");
            }
        }
        return null;
    }

    public boolean compareRegion(final Region r1, final Region r2) {
        if (Objects.equals(r1, r2)) {
            return true;
        }

        if (r1 == null || r2 == null || !Objects.equals(r1.getWorld(), r2.getWorld()) || !r1.getClass().equals(r2.getClass())) {
            return false;
        }

        if (!r1.getMinimumPoint().equals(r2.getMinimumPoint()) || !r1.getMaximumPoint().equals(r2.getMaximumPoint())
                || !r1.getCenter().equals(r2.getCenter())) {
            return false;
        }

        if (r1.getWidth() != r2.getWidth() || r1.getHeight() != r2.getHeight() || r1.getLength() != r2.getLength()) {
            return false;
        }

        if (r1 instanceof Polygonal2DRegion && r2 instanceof Polygonal2DRegion) {
            final Polygonal2DRegion r1Poly = (Polygonal2DRegion) r1;
            final Polygonal2DRegion r2Poly = (Polygonal2DRegion) r2;

            if (r1Poly.getPoints().size() != r2Poly.getPoints().size() || !r1Poly.getPoints().equals(r2Poly.getPoints())) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean isHoldingSelectionItem(final Player player) {
        final ItemStack item = player.getItemInHand();
        final ItemStack offHandItem = useOffHand ? player.getInventory().getItemInOffHand() : null;

        if ((item == null || item.getType() == Material.AIR) && (offHandItem == null || offHandItem.getType() == Material.AIR)) {
            return false;
        }

        try {
            final Field wandItemField = LocalConfiguration.class.getDeclaredField("wandItem");

            if (wandItemField.getType() == int.class) { // Legacy servers (under 1.13)
                final int wandItemId = wandItemField.getInt(WorldEdit.getInstance().getConfiguration());

                return (item != null && item.getType().getId() == wandItemId) || (offHandItem != null && offHandItem.getType().getId() == wandItemId);
            } else if (wandItemField.getType() == String.class) { // 1.13+ servers
                final String wandItem = (String) wandItemField.get(WorldEdit.getInstance().getConfiguration());

                if (item != null && BukkitAdapter.adapt(item).getType().getId().equals(wandItem)) {
                    return true;
                }

                return offHandItem != null && BukkitAdapter.adapt(offHandItem).getType().getId().equals(wandItem);
            }
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred on isHoldingSelectionItem", e);
        }
        return false;
    }
}
