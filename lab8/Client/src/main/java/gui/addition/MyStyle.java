package gui.addition;

import javax.swing.*;
import java.awt.*;

public class MyStyle {
    public static void setMultiplyFont(Font font, JComponent... components) {
        for (Component component: components) {
            component.setFont(font);
        }
    }

    public static void setPaddings(int hPadding, JComponent... components) {
        for (JComponent component: components) {
                component.setBorder(BorderFactory.createCompoundBorder(
                        component.getBorder(),
                        BorderFactory.createEmptyBorder(0, hPadding, 0, hPadding)));
        }
    }
}
