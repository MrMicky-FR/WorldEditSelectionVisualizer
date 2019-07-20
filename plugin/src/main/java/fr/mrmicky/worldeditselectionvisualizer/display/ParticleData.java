package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.fastparticle.ParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ParticleData {

    @NotNull
    private final ParticleType type;

    @Nullable
    private final Object data;

    public ParticleData(ParticleType type) {
        this(type, null);
    }

    public ParticleData(@NotNull ParticleType type, @Nullable Object data) {
        this.type = Objects.requireNonNull(type, "type");
        this.data = data;
    }

    @NotNull
    public ParticleType getType() {
        return type;
    }

    @Nullable
    public Object getData() {
        return data;
    }
}
