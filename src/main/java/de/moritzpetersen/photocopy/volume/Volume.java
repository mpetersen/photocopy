package de.moritzpetersen.photocopy.volume;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public class Volume {
    private final String name;
    private boolean ejectFailed;
    private final Collection<Consumer<Boolean>> ejectFailedListeners = new ArrayList<>();

    public Volume(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEjectFailed() {
        return ejectFailed;
    }

    public void setEjectFailed(final boolean ejectFailed) {
        this.ejectFailed = ejectFailed;
        ejectFailedListeners.forEach(listener -> listener.accept(ejectFailed));
    }

    public void addEjectFailedListener(final Consumer<Boolean> listener) {
        ejectFailedListeners.add(listener);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Volume volume = (Volume) o;
        return Objects.equals(name, volume.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
