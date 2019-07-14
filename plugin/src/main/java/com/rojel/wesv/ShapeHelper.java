package com.rojel.wesv;

import com.boydti.fawe.object.regions.FuzzyRegion;
import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.boydti.fawe.object.regions.Triangle;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ShapeHelper {

    private static final double DOUBLE_PI = Math.PI * 2;

    private final WorldEditSelectionVisualizer plugin;
    private final Configuration config;

    public ShapeHelper(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        config = plugin.getCustomConfig();
    }

    public Collection<ImmutableVector> getVectorsFromRegion(RegionWrapper regionWrapper) {
        if (regionWrapper != null) {
            Region region = regionWrapper.getRegion();
            Set<ImmutableVector> vectors = new LinkedHashSet<>();

            ImmutableVector min = regionWrapper.getMinimumPoint();
            ImmutableVector max = regionWrapper.getMaximumPoint().add(1, 1, 1);
            int width = region.getWidth();
            int length = region.getLength();
            int height = region.getHeight();

            if (region instanceof CuboidRegion) {
                List<ImmutableVector> bottomCorners = new ArrayList<>();

                bottomCorners.add(min);
                bottomCorners.add(min.withX(max.getX()));
                bottomCorners.add(max.withY(min.getY()));
                bottomCorners.add(min.withZ(max.getZ()));

                for (int i = 0; i < bottomCorners.size(); i++) {
                    ImmutableVector p1 = bottomCorners.get(i);
                    ImmutableVector p2 = bottomCorners.get(i + 1 < bottomCorners.size() ? i + 1 : 0);
                    ImmutableVector p3 = p1.add(0, height, 0);
                    ImmutableVector p4 = p2.add(0, height, 0);

                    vectors.addAll(plotLine(p1, p2));
                    vectors.addAll(plotLine(p3, p4));
                    vectors.addAll(plotLine(p1, p3));

                    if (config.isCuboidLinesEnabled()) {
                        for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                            ImmutableVector p5 = p1.add(0.0, offset, 0.0);
                            ImmutableVector p6 = p2.add(0.0, offset, 0.0);
                            vectors.addAll(plotLine(p5, p6));
                        }
                    }
                }

                if (config.isCuboidTopAndBottomEnabled()) {
                    for (double offset = config.getVerticalGap(); offset < width; offset += config.getVerticalGap()) {
                        vectors.addAll(plotLine(min.add(offset, 0, 0), min.add(offset, 0, length)));
                        vectors.addAll(plotLine(min.add(offset, height, 0), min.add(offset, height, length)));
                    }
                }
            } else if (region instanceof Polygonal2DRegion) {
                List<ImmutableVector> bottomCorners = new ArrayList<>();

                for (ImmutableVector vector : regionWrapper.getPolygonalRegionPoints()) {
                    bottomCorners.add(new ImmutableVector(vector.getX() + 0.5, min.getY(), vector.getZ() + 0.5));
                }

                for (int i = 0; i < bottomCorners.size(); ++i) {
                    ImmutableVector p1 = bottomCorners.get(i);
                    ImmutableVector p2 = bottomCorners.get(i + 1 < bottomCorners.size() ? i + 1 : 0);
                    ImmutableVector p3 = p1.add(0, height, 0);
                    ImmutableVector p4 = p2.add(0, height, 0);

                    vectors.addAll(plotLine(p1, p2));
                    vectors.addAll(plotLine(p3, p4));
                    vectors.addAll(plotLine(p1, p3));

                    if (config.isPolygonLinesEnabled()) {
                        for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                            ImmutableVector p5 = p1.add(0.0, offset, 0.0);
                            ImmutableVector p6 = p2.add(0.0, offset, 0.0);
                            vectors.addAll(plotLine(p5, p6));
                        }
                    }
                }
            } else if (region instanceof CylinderRegion) {
                ImmutableVector centerDown = regionWrapper.getCenter().withY(min.getY()).add(0.5, 0.0, 0.5);
                double radiusX = width / 2.0;
                double radiusZ = length / 2.0;
                List<ImmutableVector> bottomCorners = plotEllipse(centerDown, new ImmutableVector(radiusX, 0.0, radiusZ));
                List<ImmutableVector> bottomRadius = new ArrayList<>();

                if (config.isCylinderTopAndBottomEnabled()) {
                    boolean xHigherZ = radiusX > radiusZ;
                    double multiplier = xHigherZ ? width / (double) length : length / (double) width;
                    for (double offset = (xHigherZ ? radiusZ : radiusX) - config.getVerticalGap(); offset > 0; offset -= config.getVerticalGap()) {
                        double offset1 = offset * multiplier;
                        ImmutableVector vec = new ImmutableVector(xHigherZ ? offset1 : offset, 0.0, xHigherZ ? offset : offset1);
                        bottomRadius.addAll(plotEllipse(centerDown, vec));
                    }
                    vectors.addAll(bottomRadius);
                }

                vectors.addAll(bottomCorners);

                ImmutableVector p1 = new ImmutableVector((max.getX() + min.getX()) / 2.0, min.getY(), min.getZ());
                ImmutableVector p2 = new ImmutableVector((max.getX() + min.getX()) / 2.0, min.getY(), max.getZ());
                ImmutableVector p3 = new ImmutableVector(min.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                ImmutableVector p4 = new ImmutableVector(max.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);

                vectors.addAll(plotLine(p1, p1.add(0, height, 0)));
                vectors.addAll(plotLine(p2, p2.add(0, height, 0)));
                vectors.addAll(plotLine(p3, p3.add(0, height, 0)));
                vectors.addAll(plotLine(p4, p4.add(0, height, 0)));

                if (config.isCylinderLinesEnabled()) {
                    for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                        for (ImmutableVector vector : bottomCorners) {
                            vectors.add(vector.add(0.0, offset, 0.0));
                        }
                    }
                }

                bottomCorners.addAll(bottomRadius);

                for (ImmutableVector vector : bottomCorners) {
                    vectors.add(vector.add(0, height, 0));
                }
            } else if (region instanceof EllipsoidRegion) {
                ImmutableVector radius = regionWrapper.getEllipsoidRegionRadius().add(0.5, 0.5, 0.5);
                ImmutableVector center = regionWrapper.getCenter().add(0.5, 0.5, 0.5);

                vectors.addAll(plotEllipse(center, new ImmutableVector(0.0, radius.getY(), radius.getZ())));
                vectors.addAll(plotEllipse(center, new ImmutableVector(radius.getX(), 0.0, radius.getZ())));
                vectors.addAll(plotEllipse(center, new ImmutableVector(radius.getX(), radius.getY(), 0.0)));

                if (config.isEllipsoidLinesEnabled()) {
                    for (double offset = config.getVerticalGap(); offset < radius.getY(); offset += config.getVerticalGap()) {
                        ImmutableVector center1 = new ImmutableVector(center.getX(), center.getY() - offset, center.getZ());
                        ImmutableVector center2 = new ImmutableVector(center.getX(), center.getY() + offset, center.getZ());
                        double difference = Math.abs(center1.getY() - center.getY());
                        double radiusRatio = Math.cos(Math.asin(difference / radius.getY()));
                        double radiusX = radius.getX() * radiusRatio;
                        double radiusZ = radius.getZ() * radiusRatio;

                        ImmutableVector newRadius = new ImmutableVector(radiusX, 0.0, radiusZ);
                        vectors.addAll(plotEllipse(center1, newRadius));
                        vectors.addAll(plotEllipse(center2, newRadius));
                    }
                }
            } else if (region instanceof ConvexPolyhedralRegion) {
                List<ImmutableVector> corners = new ArrayList<>();

                for (ImmutableVector[] triangle : regionWrapper.getConvexRegionTriangles()) {
                    for (int i = 0; i < 3; i++) {
                        corners.add(triangle[i].add(0.5, 0.5, 0.5));
                    }
                }

                for (int i = 0; i < corners.size(); i++) {
                    vectors.addAll(plotLine(corners.get(i), corners.get(i + 1 < corners.size() ? i + 1 : 0)));
                }
            } else if (plugin.isFaweEnabled()) {
                handleFaweRegions(region, regionWrapper, vectors);
            }
            return vectors;
        }
        return null;
    }

    private void handleFaweRegions(Region region, RegionWrapper regionWrapper, Collection<ImmutableVector> vectors) {
        if (region instanceof PolyhedralRegion) {
            PolyhedralRegion polyhedralRegion = (PolyhedralRegion) region;
            List<ImmutableVector> corners = new ArrayList<>();

            for (Triangle triangle : polyhedralRegion.getTriangles()) {
                for (int i = 0; i < 3; i++) {
                    Vector vec = triangle.getVertex(i).add(0.5, 0.5, 0.5);

                    corners.add(new ImmutableVector(vec.getX(), vec.getY(), vec.getZ()));
                }
            }

            for (int i = 0; i < corners.size(); i++) {
                vectors.addAll(plotLine(corners.get(i), corners.get(i + 1 < corners.size() ? i + 1 : 0)));
            }
        } else if (region instanceof FuzzyRegion) {
            Set<ImmutableVector> vectorSet = new HashSet<>();

            for (ImmutableVector vector : regionWrapper) {
                for (int x = 0; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) {
                        for (int z = 0; z <= 1; z++) {
                            vectorSet.add(vector.add(x, y, z));
                        }
                    }
                }
            }

            vectors.addAll(vectorSet);
        }
    }

    private List<ImmutableVector> plotLine(ImmutableVector startVector, ImmutableVector endVector) {
        List<ImmutableVector> vectors = new ArrayList<>();
        int points = (int) (startVector.distance(endVector) / config.getGapBetweenPoints()) + 1;
        double length = startVector.distance(endVector);
        double gap = length / (points - 1);
        ImmutableVector gapVector = endVector.subtract(startVector).normalize().multiply(gap);

        for (int i = 0; i < points; ++i) {
            vectors.add(startVector.add(gapVector.multiply(i)));
        }

        return vectors;
    }

    private List<ImmutableVector> plotEllipse(ImmutableVector center, ImmutableVector radius) {
        List<ImmutableVector> vectors = new ArrayList<>();
        double maxRadius = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double deltaTheta = config.getGapBetweenPoints() / (maxRadius * DOUBLE_PI);

        for (double i = 0.0; i < 1.0; i += deltaTheta) {
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();

            if (radius.getX() == 0.0) {
                y = center.getY() + Math.cos(i * DOUBLE_PI) * radius.getY();
                z = center.getZ() + Math.sin(i * DOUBLE_PI) * radius.getZ();
            } else if (radius.getY() == 0.0) {
                x = center.getX() + Math.cos(i * DOUBLE_PI) * radius.getX();
                z = center.getZ() + Math.sin(i * DOUBLE_PI) * radius.getZ();
            } else if (radius.getZ() == 0.0) {
                x = center.getX() + Math.cos(i * DOUBLE_PI) * radius.getX();
                y = center.getY() + Math.sin(i * DOUBLE_PI) * radius.getY();
            }

            vectors.add(new ImmutableVector(x, y, z));
        }
        return vectors;
    }
}
