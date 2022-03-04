package console;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * Class represents File.
 * Main key is 2 MyFiles equals if their canonical path equals.
 */
public class MyFile extends File {
    private final String canonicalPath;

    public MyFile(@NotNull String pathname) throws IOException {
        super(pathname);
        this.canonicalPath = getCanonicalPath();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof MyFile)) {
            MyFile f = (MyFile) obj;
            return this.canonicalPath.equals(f.canonicalPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return canonicalPath.hashCode();
    }
}
