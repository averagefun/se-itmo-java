package data;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Comparable<Person>, Serializable {
    private static final long serialVersionUID = 7456027385422589175L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Double.compare(person.weight, weight) == 0
                && name.equals(person.name)
                && hairColor == person.hairColor
                && location.equals(person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, hairColor, location);
    }
}