package data;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum MpaaRating {
    G,
    PG,
    PG_13,
    NC_17;

    @Nullable
    public static MpaaRating checkElement(String el) {
        for (MpaaRating mr: MpaaRating.values()) {
            if (el.toUpperCase().equals(mr.name()))
                return mr;
        }
        return null;
    }

    public static String[] getStringValues(String defaultValue) {
        List<String> values = new ArrayList<>(MpaaRating.values().length + 1);
        if (defaultValue != null) values.add(defaultValue);
        for (MpaaRating mpaaRating: MpaaRating.values()) {
            values.add(mpaaRating.toString());
        }
        return values.toArray(new String[0]);
    }
}
