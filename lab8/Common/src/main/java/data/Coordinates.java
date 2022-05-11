package data;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = -274609875803730299L;
    private final Float x;
    private final long y;

    public Coordinates(Float x, long y) {
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y && x.equals(that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
