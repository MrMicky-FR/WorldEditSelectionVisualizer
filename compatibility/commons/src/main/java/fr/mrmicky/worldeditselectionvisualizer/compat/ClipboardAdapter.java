package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

public interface ClipboardAdapter {

    Vector3d getOrigin();

    Clipboard getClipboard();

    Region getShiftedRegion(Vector3d vector) throws RegionOperationException;
}
