package com.rojel.wesv;

import com.rojel.wesv.event.WorldEditClipboardChangeEvent;
import com.rojel.wesv.event.WorldEditSelectionChangeEvent;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.item.ItemTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
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
                if (currentRegion == null) {
                    plugin.getLastSelectedRegions().remove(player.getUniqueId());
                } else {
                    plugin.getLastSelectedRegions().put(player.getUniqueId(), currentRegion.clone());
                }

                plugin.getServer().getPluginManager().callEvent(new WorldEditSelectionChangeEvent(player, currentRegion));

                if (plugin.isSelectionShown(player)) {
                    plugin.showSelection(player);
                }
            }

            final Region currentClipboard = getClipboardRegion(player);

            if (!compareRegion(plugin.getLastClipboardRegions().get(player.getUniqueId()), currentClipboard)) {
                if (currentClipboard == null) {
                    plugin.getLastClipboardRegions().remove(player.getUniqueId());
                } else {
                    plugin.getLastClipboardRegions().put(player.getUniqueId(), currentClipboard);
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
                final ClipboardHolder holder = session.getClipboard();
                if (holder != null) {
                    final Clipboard clipboard = holder.getClipboard();
                    Region region = clipboard.getRegion().clone();

                    final BlockVector3 regionOrigin = clipboard.getOrigin();

                    final Location location = player.getLocation();
                    final BlockVector3 playerPosition = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                    final BlockVector3 translateVector = playerPosition.subtract(regionOrigin);

                    region.shift(translateVector);

                    if (!holder.getTransform().isIdentity()) {
                        final Transform transform = holder.getTransform();

                        BlockVector3 origCenter = region.getCenter().toBlockPoint();
                        BlockVector3 origMinimumPoint = region.getMinimumPoint();
                        BlockVector3 origMaximumPoint = region.getMaximumPoint();

                        BlockVector3 newCenter = null;
                        BlockVector3 newMinimumPoint = null;
                        BlockVector3 newMaximumPoint = null;

                        if (origCenter != null) {
                            newCenter = origCenter.subtract(playerPosition);
                            newCenter = transform.apply(newCenter.toVector3()).toBlockPoint();
                            newCenter = newCenter.add(playerPosition);
                        }

                        if (origMinimumPoint != null) {
                            newMinimumPoint = origMinimumPoint.subtract(playerPosition);
                            newMinimumPoint = transform.apply(newMinimumPoint.toVector3()).toBlockPoint();
                            newMinimumPoint = newMinimumPoint.add(playerPosition);
                        }

                        if (origMaximumPoint != null) {
                            newMaximumPoint = origMaximumPoint.subtract(playerPosition);
                            newMaximumPoint = transform.apply(newMaximumPoint.toVector3()).toBlockPoint();
                            newMaximumPoint = newMaximumPoint.add(playerPosition);
                        }

                        if (region instanceof CuboidRegion) {
                            region = new CuboidRegion(newMinimumPoint, newMaximumPoint);
                        } else if (region instanceof CylinderRegion) {
                            if (newMinimumPoint.getY() > newMaximumPoint.getY()) {
                                BlockVector3 temp = newMinimumPoint;
                                newMinimumPoint = newMaximumPoint;
                                newMaximumPoint = temp;
                            }

                            region = new CylinderRegion(newCenter, ((CylinderRegion) region).getRadius(), newMinimumPoint.getY(), newMaximumPoint.getY());
                        } else if (region instanceof EllipsoidRegion) {
                            final EllipsoidRegion ellipsoidRegion = (EllipsoidRegion) region.clone();
                            Vector3 normTransform = transform.apply(Vector3.at(1,1,1));
                            if (normTransform.getX() * normTransform.getZ() < 0) {
                                ellipsoidRegion.setRadius(Vector3.at(ellipsoidRegion.getRadius().getZ(), ellipsoidRegion.getRadius().getY(), ellipsoidRegion.getRadius().getX()));
                            }
                            ellipsoidRegion.setCenter(newCenter);
                            region = ellipsoidRegion;
                        } else if (region instanceof Polygonal2DRegion){
                            final Polygonal2DRegion polygonal2DRegion = (Polygonal2DRegion) region.clone();

                            List<BlockVector2> polygonPoints = polygonal2DRegion.getPoints();
                            List<BlockVector2> newPolygonPoints = new ArrayList<>();

                            for (BlockVector2 vector : polygonPoints) {
                                BlockVector2 newVector = vector.subtract(playerPosition.toBlockVector2());
                                newVector = transform.apply(newVector.toVector3()).toVector2().toBlockPoint();
                                newVector = newVector.add(playerPosition.toBlockVector2());
                                newPolygonPoints.add(newVector);
                            }

                            region = new Polygonal2DRegion(null, newPolygonPoints, newMinimumPoint.getY(), newMaximumPoint.getY());
                        } else if (region instanceof ConvexPolyhedralRegion){
                            ConvexPolyhedralRegion convexPolyhedralRegion = (ConvexPolyhedralRegion) region.clone();

                            Collection<BlockVector3> vertices = convexPolyhedralRegion.getVertices();

                            convexPolyhedralRegion = new ConvexPolyhedralRegion(convexPolyhedralRegion.getWorld());

                            for (BlockVector3 vertex : vertices) {
                                BlockVector3 newVector = vertex.subtract(playerPosition);
                                newVector = transform.apply(newVector.toVector3()).toBlockPoint();
                                newVector = newVector.add(playerPosition);
                                convexPolyhedralRegion.addVertex(newVector);
                            }

                            region = convexPolyhedralRegion;
                        } else {
                        }
                    }

                    if (region.getWorld() == null) {
                        final BukkitPlayer localPlayer = ((WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit")).wrapPlayer(player);
                        region.setWorld(localPlayer.getWorld());
                    }

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

        if (region1.getCenter() == region2.getCenter()) {
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
