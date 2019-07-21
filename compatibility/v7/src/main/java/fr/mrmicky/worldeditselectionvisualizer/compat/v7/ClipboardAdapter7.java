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

    @NotNull
    private final Clipboard clipboard;

    public ClipboardAdapter7(@NotNull Clipboard clipboard) {
        this.clipboard = Objects.requireNonNull(clipboard, "clipboard");
    }

    @Override
    public Vector3d getOrigin() {
        return Vectors7.toVector3d(clipboard.getOrigin());
    }

    @NotNull
    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public Region getShiftedRegion(Vector3d vector) throws RegionOperationException {
        Region region = clipboard.getRegion();
        region.shift(Vectors7.toBlockVector3(vector));
        return region;
    }
}
