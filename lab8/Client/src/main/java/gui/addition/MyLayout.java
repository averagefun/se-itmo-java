package gui.addition;

import javax.swing.*;
import java.awt.*;

public class MyLayout {
    public static Component space(int width, int height) {
        return Box.createRigidArea(new Dimension(width, height));
    }

    public static Component hspace(int width) {
        return space(width, 0);
    }

    public static Component vspace(int height) {
        return space(0, height);
    }
}
