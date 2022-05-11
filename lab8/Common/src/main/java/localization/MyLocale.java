package localization;

import java.util.Locale;

public enum MyLocale {
    RUSSIAN ("ru", "RU"),
    ENGLISH ("en", "IN"),
    NORWAY ("nb", "NO"),
    ALBANIAN ("sq", "AL");

    private final Locale locale;

    MyLocale(String language, String country) {
        locale = new Locale(language, country);
    }

    public Locale getLocale() {
        return locale;
    }

    public static MyLocale getMyLocale(Locale locale) {
        for (MyLocale ml: MyLocale.values()) {
            if (ml.getLocale().getLanguage().equals(locale.getLanguage()) &&
            ml.getLocale().getCountry().equals(locale.getCountry())) return ml;
        }
        return MyLocale.ENGLISH;
    }
}
