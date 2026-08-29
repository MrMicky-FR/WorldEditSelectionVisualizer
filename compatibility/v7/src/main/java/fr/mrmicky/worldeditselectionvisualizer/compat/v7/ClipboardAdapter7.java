package fr.mrmicky.worldeditselectionvisualizer.compat.v7;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import fr.mrmicky.worldeditselectionvisualizer.compat.ClipboardAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils.Vectors7;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class ClipboardAdapter7 implements ClipboardAdapter {

    private final Clipboard clipboard;

    public ClipboardAdapter7(Clipboard clipboard) {
        this.clipboard = Objects.requireNonNull(clipboard, "clipboard");
    }

    @Override
    public Vector3d getOrigin() {
        return Vectors7.toVector3d(this.clipboard.getOrigin());
    }

    @Override
    public Clipboard getClipboard() {
        return this.clipboard;
    }
}
