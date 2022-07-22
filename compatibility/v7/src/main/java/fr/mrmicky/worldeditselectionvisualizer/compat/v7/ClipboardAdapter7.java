package fr.mrmicky.worldeditselectionvisualizer.compat.v7;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.compat.ClipboardAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils.Vectors7;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClipboardAdapter7 implements ClipboardAdapter {

    private final @NotNull Clipboard clipboard;

    public ClipboardAdapter7(@NotNull Clipboard clipboard) {
        this.clipboard = Objects.requireNonNull(clipboard, "clipboard");
    }

    @Override
    public @NotNull Vector3d getOrigin() {
        return Vectors7.toVector3d(this.clipboard.getOrigin());
    }

    @Override
    public @NotNull Clipboard getClipboard() {
        return this.clipboard;
    }

    @Override
    public @NotNull Region shiftRegion(@NotNull Vector3d change)
            throws RegionOperationException {
        Region region = this.clipboard.getRegion().clone();
        region.shift(Vectors7.toBlockVector3(change));
        return region;
    }
}
