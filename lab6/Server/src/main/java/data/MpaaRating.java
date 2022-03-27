package data;

import org.jetbrains.annotations.Nullable;

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
}
