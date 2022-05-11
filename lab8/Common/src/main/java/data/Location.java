package data;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    private static final long serialVersionUID = -851027998536731800L;
    private final Double x; // Notnull
    private final double y;
    private final String name; // Nullable

    public Location(Double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.y, y) == 0 && x.equals(location.x) && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name);
    }
}
