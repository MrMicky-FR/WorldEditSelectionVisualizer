package fr.mrmicky.worldeditselectionvisualizer.display;

import fr.mrmicky.fastparticles.ParticleData;
import fr.mrmicky.fastparticles.ParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Particle {

    public static final Particle FALLBACK = new Particle(ParticleType.of("FLAME"));

    @NotNull
    private final ParticleType type;
    @Nullable
    private final ParticleData data;

    public Particle(@NotNull ParticleType type) {
        this(type, null);
    }

    public Particle(@NotNull ParticleType type, @Nullable ParticleData data) {
        this.type = Objects.requireNonNull(type, "type");
        this.data = data;
    }

    @NotNull
    public ParticleType getType() {
        return type;
    }

    @Nullable
    public ParticleData getData() {
        return data;
    }
}
