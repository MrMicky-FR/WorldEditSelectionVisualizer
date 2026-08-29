package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ClipboardAdapter {

    Vector3d getOrigin();

    Clipboard getClipboard();
}
