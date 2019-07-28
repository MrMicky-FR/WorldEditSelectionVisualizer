package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionInfos {

    @NotNull
    private final Vector3d minimum;
    @NotNull
    private final Vector3d maximum;

    private final int width;
    private final int length;
    private final int height;
    private final int area;
    private final int points;

    public RegionInfos(RegionAdapter regionAdapter) {
        Region region = regionAdapter.getRegion();

        minimum = regionAdapter.getMinimumPoint();
        maximum = regionAdapter.getMinimumPoint();

        width = region.getWidth();
        length = region.getLength();
        height = region.getHeight();
        area = region.getArea();
        points = region instanceof ConvexPolyhedralRegion ? ((ConvexPolyhedralRegion) region).getTriangles().size() : 0;
    }

    public RegionInfos(@NotNull Vector3d minimum, @NotNull Vector3d maximum, int width, int length, int height, int area, int points) {
        this.minimum = Objects.requireNonNull(minimum, "minimum");
        this.maximum = Objects.requireNonNull(maximum, "maximum");
        this.width = width;
        this.length = length;
        this.height = height;
        this.area = area;
        this.points = points;
    }

    @NotNull
    public Vector3d getMinimum() {
        return minimum;
    }

    @NotNull
    public Vector3d getMaximum() {
        return maximum;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return area;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegionInfos)) {
            return false;
        }

        RegionInfos that = (RegionInfos) o;
        return width == that.width
                && length == that.length
                && height == that.height
                && area == that.area
                && points == that.points
                && minimum.equals(that.minimum)
                && maximum.equals(that.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum, width, length, height, area, points);
    }

    @Override
    public String toString() {
        return "RegionInfos{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ", area=" + area +
                ", points=" + points +
                '}';
    }
}
