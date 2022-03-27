package data;

import org.jetbrains.annotations.Nullable;

public enum Color {
    RED,
    BLUE,
    YELLOW,
    ORANGE,
    WHITE;

    @Nullable
    public static Color checkElement(String el) {
        for (Color col: Color.values()) {
            if (el.toUpperCase().equals(col.name()))
                return col;
        }
        return null;
    }
}
