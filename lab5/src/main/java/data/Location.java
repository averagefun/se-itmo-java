package data;

public class Location {
    private final Double x; // Поле не может быть null
    private final double y;
    private final String name; // Поле может быть null

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
}
