package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

public interface ClipboardAdapter {

    @NotNull
    Vector3d getOrigin();

    @NotNull
    Clipboard getClipboard();

    @NotNull
    Region getShiftedRegion(Vector3d vector) throws RegionOperationException;
}
