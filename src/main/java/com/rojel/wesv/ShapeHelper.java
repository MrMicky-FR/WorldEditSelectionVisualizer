/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  com.sk89q.worldedit.BlockVector2D
 *  com.sk89q.worldedit.Vector
 *  com.sk89q.worldedit.regions.CuboidRegion
 *  com.sk89q.worldedit.regions.CylinderRegion
 *  com.sk89q.worldedit.regions.EllipsoidRegion
 *  com.sk89q.worldedit.regions.Polygonal2DRegion
 *  com.sk89q.worldedit.regions.Region
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 */

package com.rojel.wesv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

public class ShapeHelper {
    private final Configuration config;

    public ShapeHelper(final Configuration config) {
        this.config = config;
    }

    public Collection<Location> getLocationsFromRegion(final Region region) {
        final ArrayList<Vector> vectors = new ArrayList<Vector>();
        if (region != null) {
            final Vector min = region.getMinimumPoint();
            final Vector max = region.getMaximumPoint().add(1, 1, 1);
            final int width = region.getWidth();
            final int length = region.getLength();
            final int height = region.getHeight();
            if (region instanceof CuboidRegion) {
                final ArrayList<Vector> bottomCorners = new ArrayList<Vector>();
                bottomCorners.add(new Vector(min.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), max.getZ()));
                bottomCorners.add(new Vector(min.getX(), min.getY(), max.getZ()));
                for (int i = 0; i < bottomCorners.size(); ++i) {
                    final Vector p1 = bottomCorners.get(i);
                    final Vector p2 = i + 1 < bottomCorners.size() ? (Vector) bottomCorners.get(i + 1)
                            : (Vector) bottomCorners.get(0);
                    final Vector p3 = p1.add(0, height, 0);
                    final Vector p4 = p2.add(0, height, 0);
                    vectors.addAll(this.plotLine(p1, p2));
                    vectors.addAll(this.plotLine(p3, p4));
                    vectors.addAll(this.plotLine(p1, p3));
                    if (!this.config.getCuboidLines()) {
                        continue;
                    }
                    for (double offset = this.config.getVerticalGap(); offset < height; offset += this.config
                            .getVerticalGap()) {
                        final Vector p5 = p1.add(0.0, offset, 0.0);
                        final Vector p6 = p2.add(0.0, offset, 0.0);
                        vectors.addAll(this.plotLine(p5, p6));
                    }
                }
            } else if (region instanceof Polygonal2DRegion) {
                final Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;
                final ArrayList<Vector> bottomCorners = new ArrayList<Vector>();
                for (final BlockVector2D vec2D : polyRegion.getPoints()) {
                    bottomCorners.add(new Vector(vec2D.getX() + 0.5, min.getY(), vec2D.getZ() + 0.5));
                }
                for (int i = 0; i < bottomCorners.size(); ++i) {
                    final Vector p1 = bottomCorners.get(i);
                    final Vector p2 = i + 1 < bottomCorners.size() ? (Vector) bottomCorners.get(i + 1)
                            : (Vector) bottomCorners.get(0);
                    final Vector p3 = p1.add(0, height, 0);
                    final Vector p4 = p2.add(0, height, 0);
                    vectors.addAll(this.plotLine(p1, p2));
                    vectors.addAll(this.plotLine(p3, p4));
                    vectors.addAll(this.plotLine(p1, p3));
                    if (!this.config.getPolygonLines()) {
                        continue;
                    }
                    for (double offset = this.config.getVerticalGap(); offset < height; offset += this.config
                            .getVerticalGap()) {
                        final Vector p5 = p1.add(0.0, offset, 0.0);
                        final Vector p6 = p2.add(0.0, offset, 0.0);
                        vectors.addAll(this.plotLine(p5, p6));
                    }
                }
            } else if (region instanceof CylinderRegion) {
                final CylinderRegion cylRegion = (CylinderRegion) region;
                final Vector center = new Vector(cylRegion.getCenter().getX() + 0.5, min.getY(),
                        cylRegion.getCenter().getZ() + 0.5);
                final double rx = width / 2.0;
                final double rz = length / 2.0;
                final List<Vector> bottomCorners = this.plotEllipse(center, new Vector(rx, 0.0, rz));
                vectors.addAll(bottomCorners);
                for (final Vector vec : bottomCorners) {
                    vectors.add(vec.add(0, height, 0));
                }
                final Vector p1 = new Vector((max.getX() + min.getX()) / 2.0, min.getY(), min.getZ());
                final Vector p2 = new Vector((max.getX() + min.getX()) / 2.0, min.getY(), max.getZ());
                final Vector p3 = new Vector(min.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                final Vector p4 = new Vector(max.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                vectors.addAll(this.plotLine(p1, p1.add(0, height, 0)));
                vectors.addAll(this.plotLine(p2, p2.add(0, height, 0)));
                vectors.addAll(this.plotLine(p3, p3.add(0, height, 0)));
                vectors.addAll(this.plotLine(p4, p4.add(0, height, 0)));
                if (this.config.getCylinderLines()) {
                    for (double offset = this.config.getVerticalGap(); offset < height; offset += this.config
                            .getVerticalGap()) {
                        for (final Vector vec2 : bottomCorners) {
                            vectors.add(vec2.add(0.0, offset, 0.0));
                        }
                    }
                }
            } else if (region instanceof EllipsoidRegion) {
                final EllipsoidRegion ellRegion = (EllipsoidRegion) region;
                final Vector ellRadius = ellRegion.getRadius().add(0.5, 0.5, 0.5);
                final Vector center = new Vector(min.getX() + width / 2.0, min.getY() + height / 2.0,
                        min.getZ() + length / 2.0);
                vectors.addAll(this.plotEllipse(center, new Vector(0.0, ellRadius.getY(), ellRadius.getZ())));
                vectors.addAll(this.plotEllipse(center, new Vector(ellRadius.getX(), 0.0, ellRadius.getZ())));
                vectors.addAll(this.plotEllipse(center, new Vector(ellRadius.getX(), ellRadius.getY(), 0.0)));
                if (this.config.getEllipsoidLines()) {
                    for (double offset = this.config.getVerticalGap(); offset < ellRadius.getY(); offset += this.config
                            .getVerticalGap()) {
                        final Vector center1 = new Vector(center.getX(), center.getY() - offset, center.getZ());
                        final Vector center2 = new Vector(center.getX(), center.getY() + offset, center.getZ());
                        final double difference = Math.abs(center1.getY() - center.getY());
                        final double radiusRatio = Math.cos(Math.asin(difference / ellRadius.getY()));
                        final double rx = ellRadius.getX() * radiusRatio;
                        final double rz = ellRadius.getZ() * radiusRatio;
                        vectors.addAll(this.plotEllipse(center1, new Vector(rx, 0.0, rz)));
                        vectors.addAll(this.plotEllipse(center2, new Vector(rx, 0.0, rz)));
                    }
                }
            }
        }
        final ArrayList<Location> locations = new ArrayList<Location>();
        if (vectors.size() > 0 && region != null && region.getWorld() != null) {
            final org.bukkit.World world = Bukkit.getWorld(region.getWorld().getName());
            for (final Vector vector : vectors) {
                locations.add(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
            }
        }
        return locations;
    }

    private List<Vector> plotLine(final Vector p1, final Vector p2) {
        final ArrayList<Vector> vectors = new ArrayList<Vector>();
        final int points = (int) (p1.distance(p2) / this.config.getGapBetweenPoints()) + 1;
        final double length = p1.distance(p2);
        final double gap = length / (points - 1);
        final Vector gapVector = p2.subtract(p1).normalize().multiply(gap);
        for (int i = 0; i < points; ++i) {
            final Vector currentPoint = p1.add(gapVector.multiply(i));
            vectors.add(currentPoint);
        }
        return vectors;
    }

    private List<Vector> plotEllipse(final Vector center, final Vector radius) {
        final ArrayList<Vector> vectors = new ArrayList<Vector>();
        final double biggestR = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        final double circleCircumference = 2.0 * biggestR * 3.141592653589793;
        final double deltaTheta = this.config.getGapBetweenPoints() / circleCircumference;
        for (double i = 0.0; i < 1.0; i += deltaTheta) {
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();
            if (radius.getX() == 0.0) {
                y = center.getY() + Math.cos(i * 2.0 * 3.141592653589793) * radius.getY();
                z = center.getZ() + Math.sin(i * 2.0 * 3.141592653589793) * radius.getZ();
            } else if (radius.getY() == 0.0) {
                x = center.getX() + Math.cos(i * 2.0 * 3.141592653589793) * radius.getX();
                z = center.getZ() + Math.sin(i * 2.0 * 3.141592653589793) * radius.getZ();
            } else if (radius.getZ() == 0.0) {
                x = center.getX() + Math.cos(i * 2.0 * 3.141592653589793) * radius.getX();
                y = center.getY() + Math.sin(i * 2.0 * 3.141592653589793) * radius.getY();
            }
            final Vector loc = new Vector(x, y, z);
            vectors.add(loc);
        }
        return vectors;
    }
}
