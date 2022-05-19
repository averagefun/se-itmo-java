package gui.addition;

import javax.swing.*;
import java.awt.*;

public class ImageManager {
    private final static String staticPath = "Client/src/main/java/gui/static/";

    public static Image getImage(String name) {
        return new ImageIcon(staticPath + name).getImage();
    }
}
