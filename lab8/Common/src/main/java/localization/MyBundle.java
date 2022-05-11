package localization;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MyBundle {
    private static final List<MyBundle> myBundles = new ArrayList<>();

    private ResourceBundle bundle;
    private MyLocale myLocale;

    public static MyBundle getBundle(String baseName) {
        for (MyBundle mb: myBundles) {
            if (mb.getBaseName().equals(baseName)) {
                return mb;
            }
        }
        MyBundle mb = new MyBundle(baseName);
        myBundles.add(mb);
        return mb;
    }

    public static MyBundle getBundle(String baseName, MyLocale myLocale) {
        MyBundle myBundle = getBundle(baseName);
        myBundle.setMyLocale(myLocale);
        return myBundle;
    }

    public String getBaseName() {
        return bundle.getBaseBundleName();
    }

    public MyLocale getMyLocale() {
        return myLocale;
    }

    private MyBundle(String baseName) {
        this.bundle = ResourceBundle.getBundle(baseName);
        this.myLocale = MyLocale.getMyLocale(bundle.getLocale());
    }

    public void setMyLocale(MyLocale myLocale) {
        this.myLocale = myLocale;
        String baseName = bundle.getBaseBundleName();
        bundle = ResourceBundle.getBundle(baseName, this.myLocale.getLocale());
    }

    @NotNull
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "";
        }
    }
}
