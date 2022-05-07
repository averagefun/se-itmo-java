package gui;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private final JFrame frame;

    public GUI() {
        // Creating the Frame
        this.frame = new JFrame("Client app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void addToFrame(Component component) {
        frame.add(component);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();

        JPanel panel = new JPanel();
        gui.addToFrame(panel);

        JButton button = new JButton("submit");
        panel.add(button);

        button.addActionListener(e -> {
            if (panel.getBackground() != Color.GREEN) {
                panel.setBackground(Color.GREEN);
            } else {
                panel.setBackground(Color.BLACK);
            }
        });
    }
}
