package com.rojel.wesv;

import com.sk89q.worldedit.regions.Region;

import java.util.List;

/**
 * @author MrMicky
 */
public interface RegionWrapper extends Iterable<ImmutableVector> {

    ImmutableVector getMinimumPoint();

    ImmutableVector getMaximumPoint();

    ImmutableVector getCenter();

    List<ImmutableVector> getPolygonalRegionPoints();

    ImmutableVector getEllipsoidRegionRadius();

    List<ImmutableVector[]> getConvexRegionTriangles();

    Region getRegion();
}
