package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionInfos {

    @NotNull
    private final ImmutableVector minimum;
    @NotNull
    private final ImmutableVector maximum;

    private final int width;
    private final int length;
    private final int height;
    private final int area;

    public RegionInfos(RegionAdapter region) {
        minimum = region.getMinimumPoint();
        maximum = region.getMinimumPoint();
        width = region.getRegion().getWidth();
        length = region.getRegion().getLength();
        height = region.getRegion().getHeight();
        area = region.getRegion().getArea();
    }

    public RegionInfos(@NotNull ImmutableVector minimum, @NotNull ImmutableVector maximum, int width, int length, int height, int area) {
        this.minimum = Objects.requireNonNull(minimum, "minimum");
        this.maximum = Objects.requireNonNull(maximum, "maximum");
        this.width = width;
        this.length = length;
        this.height = height;
        this.area = area;
    }

    @NotNull
    public ImmutableVector getMinimum() {
        return minimum;
    }

    @NotNull
    public ImmutableVector getMaximum() {
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
                && minimum.equals(that.minimum)
                && maximum.equals(that.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum, width, length, height, area);
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
                '}';
    }
}
