package data;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum MovieGenre {
    ADVENTURE,
    THRILLER,
    HORROR;

    @Nullable
    public static MovieGenre checkElement(String el) {
        for (MovieGenre mg: MovieGenre.values()) {
            if (el.toUpperCase().equals(mg.name()))
                return mg;
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static String[] getStringValues(String defaultValue) {
        List<String> values = new ArrayList<>(MovieGenre.values().length + 1);
        if (defaultValue != null) values.add(defaultValue);
        for (MovieGenre movieGenre: MovieGenre.values()) {
            values.add(movieGenre.toString());
        }
        return values.toArray(new String[0]);
    }
}
