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

    public ShapeHelper(Configuration config) {
        this.config = config;
    }

    public Collection<Location> getLocationsFromRegion(Region region) {
        ArrayList<Vector> vectors = new ArrayList<Vector>();
        if (region != null) {
            Vector min = region.getMinimumPoint();
            Vector max = region.getMaximumPoint().add(1, 1, 1);
            int width = region.getWidth();
            int length = region.getLength();
            int height = region.getHeight();
            if (region instanceof CuboidRegion) {
                ArrayList<Vector> bottomCorners = new ArrayList<Vector>();
                bottomCorners.add(new Vector(min.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), max.getZ()));
                bottomCorners.add(new Vector(min.getX(), min.getY(), max.getZ()));
                for (int i = 0; i < bottomCorners.size(); ++i) {
                    Vector p1 = bottomCorners.get(i);
                    Vector p2 = i + 1 < bottomCorners.size() ? (Vector)bottomCorners.get(i + 1) : (Vector)bottomCorners.get(0);
                    Vector p3 = p1.add(0, height, 0);
                    Vector p4 = p2.add(0, height, 0);
                    vectors.addAll(this.plotLine(p1, p2));
                    vectors.addAll(this.plotLine(p3, p4));
                    vectors.addAll(this.plotLine(p1, p3));
                    if (!this.config.cuboidLines()) {
						continue;
					}
                    for (double offset = this.config.verticalGap(); offset < height; offset += this.config.verticalGap()) {
                        Vector p5 = p1.add(0.0, offset, 0.0);
                        Vector p6 = p2.add(0.0, offset, 0.0);
                        vectors.addAll(this.plotLine(p5, p6));
                    }
                }
            } else if (region instanceof Polygonal2DRegion) {
                Polygonal2DRegion polyRegion = (Polygonal2DRegion)region;
                ArrayList<Vector> bottomCorners = new ArrayList<Vector>();
                for (BlockVector2D vec2D : polyRegion.getPoints()) {
                    bottomCorners.add(new Vector(vec2D.getX() + 0.5, min.getY(), vec2D.getZ() + 0.5));
                }
                for (int i = 0; i < bottomCorners.size(); ++i) {
                    Vector p1 = bottomCorners.get(i);
                    Vector p2 = i + 1 < bottomCorners.size() ? (Vector)bottomCorners.get(i + 1) : (Vector)bottomCorners.get(0);
                    Vector p3 = p1.add(0, height, 0);
                    Vector p4 = p2.add(0, height, 0);
                    vectors.addAll(this.plotLine(p1, p2));
                    vectors.addAll(this.plotLine(p3, p4));
                    vectors.addAll(this.plotLine(p1, p3));
                    if (!this.config.polygonLines()) {
						continue;
					}
                    for (double offset = this.config.verticalGap(); offset < height; offset += this.config.verticalGap()) {
                        Vector p5 = p1.add(0.0, offset, 0.0);
                        Vector p6 = p2.add(0.0, offset, 0.0);
                        vectors.addAll(this.plotLine(p5, p6));
                    }
                }
            } else if (region instanceof CylinderRegion) {
                CylinderRegion cylRegion = (CylinderRegion)region;
                Vector center = new Vector(cylRegion.getCenter().getX() + 0.5, min.getY(), cylRegion.getCenter().getZ() + 0.5);
                double rx = width / 2.0;
                double rz = length / 2.0;
                List<Vector> bottomCorners = this.plotEllipse(center, new Vector(rx, 0.0, rz));
                vectors.addAll(bottomCorners);
                for (Vector vec : bottomCorners) {
                    vectors.add(vec.add(0, height, 0));
                }
                Vector p1 = new Vector((max.getX() + min.getX()) / 2.0, min.getY(), min.getZ());
                Vector p2 = new Vector((max.getX() + min.getX()) / 2.0, min.getY(), max.getZ());
                Vector p3 = new Vector(min.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                Vector p4 = new Vector(max.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                vectors.addAll(this.plotLine(p1, p1.add(0, height, 0)));
                vectors.addAll(this.plotLine(p2, p2.add(0, height, 0)));
                vectors.addAll(this.plotLine(p3, p3.add(0, height, 0)));
                vectors.addAll(this.plotLine(p4, p4.add(0, height, 0)));
                if (this.config.cylinderLines()) {
                    for (double offset = this.config.verticalGap(); offset < height; offset += this.config.verticalGap()) {
                        for (Vector vec2 : bottomCorners) {
                            vectors.add(vec2.add(0.0, offset, 0.0));
                        }
                    }
                }
            } else if (region instanceof EllipsoidRegion) {
                EllipsoidRegion ellRegion = (EllipsoidRegion)region;
                Vector ellRadius = ellRegion.getRadius().add(0.5, 0.5, 0.5);
                Vector center = new Vector(min.getX() + width / 2.0, min.getY() + height / 2.0, min.getZ() + length / 2.0);
                vectors.addAll(this.plotEllipse(center, new Vector(0.0, ellRadius.getY(), ellRadius.getZ())));
                vectors.addAll(this.plotEllipse(center, new Vector(ellRadius.getX(), 0.0, ellRadius.getZ())));
                vectors.addAll(this.plotEllipse(center, new Vector(ellRadius.getX(), ellRadius.getY(), 0.0)));
                if (this.config.ellipsoidLines()) {
                    for (double offset = this.config.verticalGap(); offset < ellRadius.getY(); offset += this.config.verticalGap()) {
                        Vector center1 = new Vector(center.getX(), center.getY() - offset, center.getZ());
                        Vector center2 = new Vector(center.getX(), center.getY() + offset, center.getZ());
                        double difference = Math.abs(center1.getY() - center.getY());
                        double radiusRatio = Math.cos(Math.asin(difference / ellRadius.getY()));
                        double rx = ellRadius.getX() * radiusRatio;
                        double rz = ellRadius.getZ() * radiusRatio;
                        vectors.addAll(this.plotEllipse(center1, new Vector(rx, 0.0, rz)));
                        vectors.addAll(this.plotEllipse(center2, new Vector(rx, 0.0, rz)));
                    }
                }
            }
        }
        ArrayList<Location> locations = new ArrayList<Location>();
        if (vectors.size() > 0 && region != null && region.getWorld() != null) {
            org.bukkit.World world = Bukkit.getWorld(region.getWorld().getName());
            for (Vector vector : vectors) {
                locations.add(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
            }
        }
        return locations;
    }

    private List<Vector> plotLine(Vector p1, Vector p2) {
        ArrayList<Vector> vectors = new ArrayList<Vector>();
        int points = (int)(p1.distance(p2) / this.config.gapBetweenPoints()) + 1;
        double length = p1.distance(p2);
        double gap = length / (points - 1);
        Vector gapVector = p2.subtract(p1).normalize().multiply(gap);
        for (int i = 0; i < points; ++i) {
            Vector currentPoint = p1.add(gapVector.multiply(i));
            vectors.add(currentPoint);
        }
        return vectors;
    }

    private List<Vector> plotEllipse(Vector center, Vector radius) {
        ArrayList<Vector> vectors = new ArrayList<Vector>();
        double biggestR = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double circleCircumference = 2.0 * biggestR * 3.141592653589793;
        double deltaTheta = this.config.gapBetweenPoints() / circleCircumference;
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
            Vector loc = new Vector(x, y, z);
            vectors.add(loc);
        }
        return vectors;
    }
}

