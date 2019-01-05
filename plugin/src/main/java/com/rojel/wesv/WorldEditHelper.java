package com.rojel.wesv;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.regions.factory.CuboidRegionFactory;
import com.sk89q.worldedit.regions.factory.SphereRegionFactory;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.item.ItemTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

            final Region currentClipboard = getClipboardRegion(player);

            if (!compareRegion(plugin.getLastClipboardRegions().get(player.getUniqueId()), currentClipboard)) {
                if (currentClipboard != null) {
                    plugin.getLastClipboardRegions().put(player.getUniqueId(), currentClipboard);
                } else {
                    plugin.getLastClipboardRegions().remove(player.getUniqueId());
                }

                plugin.getServer().getPluginManager().callEvent(new WorldEditClipboardChangeEvent(player, currentClipboard));

                if (plugin.isClipboardShown(player)) {
                    plugin.showClipboard(player);
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

    public Region getClipboardRegion(final Player player) {
        final LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());

        if (session != null) {
            try {
                ClipboardHolder holder = session.getClipboard();
                if (holder != null) {
                    Clipboard clipboard = holder.getClipboard();
                    Region region = clipboard.getRegion().clone();

                    BlockVector3 regionOrigin = clipboard.getOrigin();

                    Location location = player.getLocation();
                    BlockVector3 playerPosition = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                    BlockVector3 translateVector = playerPosition.subtract(regionOrigin);

                    region.shift(translateVector);

                    if (!holder.getTransform().isIdentity()) {
                        Transform transform = holder.getTransform();

                        Region toTransform = region.clone();

                        Vector3 relativeCenter = toTransform.getCenter().subtract(playerPosition.toVector3());

                        Vector3 normalizeShift = relativeCenter.subtract(toTransform.getCenter());
                        Vector3 originShift = toTransform.getCenter().subtract(relativeCenter);

                        toTransform.shift(normalizeShift.toBlockPoint());

                        TransformRegion transformRegion = new TransformRegion(toTransform, transform);

                        if (region instanceof CuboidRegion) {
                            region = CuboidRegion.makeCuboid(transformRegion);
                            region.shift(originShift.toBlockPoint());
                        } else if (region instanceof CylinderRegion) {
                            CylinderRegion cylinderRegion = (CylinderRegion) region.clone();
                            int newMinY;
                            int newMaxY;

                            if (cylinderRegion.getMinimumPoint() != transformRegion.getMinimumPoint() || cylinderRegion.getMaximumPoint() != transformRegion.getMaximumPoint()) {
                                newMinY = cylinderRegion.getMinimumY();
                                newMaxY = cylinderRegion.getMinimumY();
                            } else {
                                newMinY = cylinderRegion.getMinimumY();
                                newMaxY = cylinderRegion.getMaximumY();
                            }
                            region = new CylinderRegion(transformRegion.getCenter().toBlockPoint(), cylinderRegion.getRadius(), newMinY, newMaxY);
                            region.shift(originShift.toBlockPoint());
                        } else if (region instanceof EllipsoidRegion) {
                            EllipsoidRegion ellipsoidRegion = (EllipsoidRegion) region.clone();
                            ellipsoidRegion.setCenter(transformRegion.getCenter().toBlockPoint());
                            region = ellipsoidRegion;
                            region.shift(originShift.toBlockPoint());
                        } else {
                            player.sendMessage("Can't display this kind of transformed region yet");
                        }
                    }

                    region.setWorld(clipboard.getRegion().getWorld());

                    return region;
                }
            } catch (EmptyClipboardException e) {
                return null;
            } catch (RegionOperationException e) {
                plugin.getLogger().warning("Error on clipboard's region shift: RegionOperationException");
                return null;
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

        if (region1.getCenter() == region2.getCenter()) {
            return false;
        }

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
