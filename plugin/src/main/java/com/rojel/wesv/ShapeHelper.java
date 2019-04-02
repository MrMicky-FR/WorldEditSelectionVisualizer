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

    private final WorldEditSelectionVisualizer plugin;
    private final Configuration config;

    public ShapeHelper(final WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;

        config = plugin.getCustomConfig();
    }

    public Collection<ImmutableVector> getVectorsFromRegion(final RegionWrapper regionWrapper) {
        if (regionWrapper != null) {
            final Region region = regionWrapper.getRegion();
            final Set<ImmutableVector> vectors = new LinkedHashSet<>();

            final ImmutableVector min = regionWrapper.getMinimumPoint();
            final ImmutableVector max = regionWrapper.getMaximumPoint().add(1, 1, 1);
            final int width = region.getWidth();
            final int length = region.getLength();
            final int height = region.getHeight();

            if (region instanceof CuboidRegion) {
                final List<ImmutableVector> bottomCorners = new ArrayList<>();

                bottomCorners.add(new ImmutableVector(min.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new ImmutableVector(max.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new ImmutableVector(max.getX(), min.getY(), max.getZ()));
                bottomCorners.add(new ImmutableVector(min.getX(), min.getY(), max.getZ()));

                for (int i = 0; i < bottomCorners.size(); i++) {
                    final ImmutableVector p1 = bottomCorners.get(i);
                    final ImmutableVector p2 = bottomCorners.get(i + 1 < bottomCorners.size() ? i + 1 : 0);
                    final ImmutableVector p3 = p1.add(0, height, 0);
                    final ImmutableVector p4 = p2.add(0, height, 0);

                    vectors.addAll(plotLine(p1, p2));
                    vectors.addAll(plotLine(p3, p4));
                    vectors.addAll(plotLine(p1, p3));

                    if (config.isCuboidLinesEnabled()) {
                        for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                            final ImmutableVector p5 = p1.add(0.0, offset, 0.0);
                            final ImmutableVector p6 = p2.add(0.0, offset, 0.0);
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
                final List<ImmutableVector> bottomCorners = new ArrayList<>();

                for (final ImmutableVector vec2D : regionWrapper.getPolygonalRegionPoints()) {
                    bottomCorners.add(new ImmutableVector(vec2D.getX() + 0.5, min.getY(), vec2D.getZ() + 0.5));
                }

                for (int i = 0; i < bottomCorners.size(); ++i) {
                    final ImmutableVector p1 = bottomCorners.get(i);
                    final ImmutableVector p2 = bottomCorners.get(i + 1 < bottomCorners.size() ? i + 1 : 0);
                    final ImmutableVector p3 = p1.add(0, height, 0);
                    final ImmutableVector p4 = p2.add(0, height, 0);

                    vectors.addAll(plotLine(p1, p2));
                    vectors.addAll(plotLine(p3, p4));
                    vectors.addAll(plotLine(p1, p3));

                    if (config.isPolygonLinesEnabled()) {
                        for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                            final ImmutableVector p5 = p1.add(0.0, offset, 0.0);
                            final ImmutableVector p6 = p2.add(0.0, offset, 0.0);
                            vectors.addAll(plotLine(p5, p6));
                        }
                    }
                }
            } else if (region instanceof CylinderRegion) {
                final ImmutableVector centerDown = regionWrapper.getCenter().withY(min.getY()).add(0.5, 0.0, 0.5);
                final double rx = width / 2.0;
                final double rz = length / 2.0;
                final List<ImmutableVector> bottomCorners = plotEllipse(centerDown, new ImmutableVector(rx, 0.0, rz));
                final List<ImmutableVector> bottomRadius = new ArrayList<>();

                if (config.isCylinderTopAndBottomEnabled()) {
                    final boolean xHigherZ = rx > rz;
                    final double multiplier = xHigherZ ? width / (double) length : length / (double) width;
                    for (double offset = (xHigherZ ? rz : rx) - config.getVerticalGap(); offset > 0; offset -= config.getVerticalGap()) {
                        final double offset1 = offset * multiplier;
                        final ImmutableVector vec = new ImmutableVector(xHigherZ ? offset1 : offset, 0.0, xHigherZ ? offset : offset1);
                        bottomRadius.addAll(plotEllipse(centerDown, vec));
                    }
                    vectors.addAll(bottomRadius);
                }

                vectors.addAll(bottomCorners);

                final ImmutableVector p1 = new ImmutableVector((max.getX() + min.getX()) / 2.0, min.getY(), min.getZ());
                final ImmutableVector p2 = new ImmutableVector((max.getX() + min.getX()) / 2.0, min.getY(), max.getZ());
                final ImmutableVector p3 = new ImmutableVector(min.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);
                final ImmutableVector p4 = new ImmutableVector(max.getX(), min.getY(), (max.getZ() + min.getZ()) / 2.0);

                vectors.addAll(plotLine(p1, p1.add(0, height, 0)));
                vectors.addAll(plotLine(p2, p2.add(0, height, 0)));
                vectors.addAll(plotLine(p3, p3.add(0, height, 0)));
                vectors.addAll(plotLine(p4, p4.add(0, height, 0)));

                if (config.isCylinderLinesEnabled()) {
                    for (double offset = config.getVerticalGap(); offset < height; offset += config.getVerticalGap()) {
                        for (final ImmutableVector vec2 : bottomCorners) {
                            vectors.add(vec2.add(0.0, offset, 0.0));
                        }
                    }
                }

                bottomCorners.addAll(bottomRadius);

                for (final ImmutableVector vec : bottomCorners) {
                    vectors.add(vec.add(0, height, 0));
                }
            } else if (region instanceof EllipsoidRegion) {
                final ImmutableVector ellRadius = regionWrapper.getEllipsoidRegionRadius().add(0.5, 0.5, 0.5);
                final ImmutableVector center = regionWrapper.getCenter().add(0.5, 0.5, 0.5);

                vectors.addAll(plotEllipse(center, new ImmutableVector(0.0, ellRadius.getY(), ellRadius.getZ())));
                vectors.addAll(plotEllipse(center, new ImmutableVector(ellRadius.getX(), 0.0, ellRadius.getZ())));
                vectors.addAll(plotEllipse(center, new ImmutableVector(ellRadius.getX(), ellRadius.getY(), 0.0)));

                if (config.isEllipsoidLinesEnabled()) {
                    for (double offset = config.getVerticalGap(); offset < ellRadius.getY(); offset += config.getVerticalGap()) {
                        final ImmutableVector center1 = new ImmutableVector(center.getX(), center.getY() - offset, center.getZ());
                        final ImmutableVector center2 = new ImmutableVector(center.getX(), center.getY() + offset, center.getZ());
                        final double difference = Math.abs(center1.getY() - center.getY());
                        final double radiusRatio = Math.cos(Math.asin(difference / ellRadius.getY()));
                        final double rx = ellRadius.getX() * radiusRatio;
                        final double rz = ellRadius.getZ() * radiusRatio;
                        vectors.addAll(plotEllipse(center1, new ImmutableVector(rx, 0.0, rz)));
                        vectors.addAll(plotEllipse(center2, new ImmutableVector(rx, 0.0, rz)));
                    }
                }
            } else if (region instanceof ConvexPolyhedralRegion) {
                final List<ImmutableVector> corners = new ArrayList<>();

                for (final ImmutableVector[] triangle : regionWrapper.getConvexRegionTriangles()) {
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
            final PolyhedralRegion polyhedralRegion = (PolyhedralRegion) region;
            final List<ImmutableVector> corners = new ArrayList<>();

            for (final Triangle triangle : polyhedralRegion.getTriangles()) {
                for (int i = 0; i < 3; i++) {
                    Vector vec = triangle.getVertex(i).add(0.5, 0.5, 0.5);

                    corners.add(new ImmutableVector(vec.getX(), vec.getY(), vec.getZ()));
                }
            }

            for (int i = 0; i < corners.size(); i++) {
                vectors.addAll(plotLine(corners.get(i), corners.get(i + 1 < corners.size() ? i + 1 : 0)));
            }
        } else if (region instanceof FuzzyRegion) {
            final Set<ImmutableVector> vectorSet = new HashSet<>();

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

    private List<ImmutableVector> plotLine(final ImmutableVector p1, final ImmutableVector p2) {
        final List<ImmutableVector> vectors = new ArrayList<>();
        final int points = (int) (p1.distance(p2) / config.getGapBetweenPoints()) + 1;
        final double length = p1.distance(p2);
        final double gap = length / (points - 1);
        final ImmutableVector gapVector = p2.subtract(p1).normalize().multiply(gap);

        for (int i = 0; i < points; ++i) {
            vectors.add(p1.add(gapVector.multiply(i)));
        }

        return vectors;
    }

    private List<ImmutableVector> plotEllipse(final ImmutableVector center, final ImmutableVector radius) {
        final List<ImmutableVector> vectors = new ArrayList<>();
        final double biggestR = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        final double circleCircumference = biggestR * 2.0 * Math.PI;
        final double deltaTheta = config.getGapBetweenPoints() / circleCircumference;

        for (double i = 0.0; i < 1.0; i += deltaTheta) {
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();

            if (radius.getX() == 0.0) {
                y = center.getY() + Math.cos(i * 2.0 * Math.PI) * radius.getY();
                z = center.getZ() + Math.sin(i * 2.0 * Math.PI) * radius.getZ();
            } else if (radius.getY() == 0.0) {
                x = center.getX() + Math.cos(i * 2.0 * Math.PI) * radius.getX();
                z = center.getZ() + Math.sin(i * 2.0 * Math.PI) * radius.getZ();
            } else if (radius.getZ() == 0.0) {
                x = center.getX() + Math.cos(i * 2.0 * Math.PI) * radius.getX();
                y = center.getY() + Math.sin(i * 2.0 * Math.PI) * radius.getY();
            }

            final ImmutableVector loc = new ImmutableVector(x, y, z);
            vectors.add(loc);
        }
        return vectors;
    }
}
