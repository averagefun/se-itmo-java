package data;

import org.jetbrains.annotations.Nullable;

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
}
