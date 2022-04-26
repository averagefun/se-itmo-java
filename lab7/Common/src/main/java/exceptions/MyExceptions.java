package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyExceptions {
    public static String getStringStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
