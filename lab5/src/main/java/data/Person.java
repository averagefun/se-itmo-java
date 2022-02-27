package data;

public class Person implements Comparable<Person>{
    private final String name;
    private final double weight;
    private final Color hairColor;
    private final Location location;

    public Person(String name, double weight, Color hairColor, Location location) {
        this.name = name;
        this.weight = weight;
        this.hairColor = hairColor;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int compareTo(Person p) {
        return Double.compare(getWeight(), p.getWeight());
    }
}