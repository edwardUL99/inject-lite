package io.github.edwardUL99.inject.lite.internal.dependency;

import java.util.Objects;

/**
 * Represents a class dependency
 */
public class Dependency {
    /**
     * The name of the dependency
     */
    private final String name;
    /**
     * The type of the dependency
     */
    private final Class<?> type;

    /**
     * Create the dependency with the provided type
     * @param name the name of the dependency
     * @param type the type of the dependency
     */
    public Dependency(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get the dependency name
     * @return name of the dependency
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the dependency
     * @return the dependency type
     */
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
