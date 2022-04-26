package data;

import java.io.Serializable;

public class Coordinates implements Serializable {
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
}
