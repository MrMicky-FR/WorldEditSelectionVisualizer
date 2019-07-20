package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;

public interface ClipboardAdapter {

    ImmutableVector getOrigin();

    Clipboard getClipboard();

    Region getShiftedRegion(ImmutableVector vector) throws RegionOperationException;
}
