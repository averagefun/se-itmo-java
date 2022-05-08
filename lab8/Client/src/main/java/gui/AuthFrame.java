package gui;

import commands.CommandManager;
import network.CommandResponse;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class AuthFrame extends AbstractFrame {
    private final JLabel usernameText = new JLabel(bundle.getString("username"));
    private final JTextField usernameField = new JTextField();
    private final JLabel passwordText = new JLabel(bundle.getString("password"));
    private final JPasswordField passwordField = new JPasswordField();

    private final JLabel authErrorLabel = new JLabel();
    private String authErrorLabelKey;

    private final JButton logInButton = new JButton(bundle.getString("logIn"));
    private final JButton signUpButton = new JButton(bundle.getString("signUp"));

    public AuthFrame(CommandManager cm) {
        super(cm);
        initElements();
        layoutFrame();
    }

    protected void updateLabels() {
        setTitle(bundle.getString("titleAuth"));

        language.setText(bundle.getString("language"));
        usernameText.setText(bundle.getString("username"));
        passwordText.setText(bundle.getString("password"));
        setAuthErrorLabel(authErrorLabelKey);

        logInButton.setText(bundle.getString("logInButton"));
        signUpButton.setText(bundle.getString("signUpButton"));
    }

    public void display() {
        setTitle(bundle.getString("titleAuth"));
        setSize(360, 240);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setAuthErrorLabel(String key) {
        if (key == null) {
            authErrorLabelKey = null;
            authErrorLabel.setVisible(false);
        } else {
            authErrorLabelKey = key;
            String newLabel = bundle.getString(authErrorLabelKey);
            authErrorLabel.setText(newLabel.isEmpty() ? "Error" : newLabel);
            authErrorLabel.setVisible(true);
        }
    }

    private void initElements() {
        usernameText.setHorizontalAlignment(SwingConstants.CENTER);
        passwordText.setHorizontalAlignment(SwingConstants.CENTER);
        authErrorLabel.setForeground(Color.RED);
        usernameField.setPreferredSize(new Dimension(130, 20));
        passwordField.setPreferredSize(new Dimension(130, 20));
        authErrorLabel.setVisible(false);

        initButtons();
        initMenuItems();
    }

    private void activeAuthInterface(boolean active) {
        usernameField.setEditable(active);
        passwordField.setEditable(active);
        logInButton.setEnabled(active);
        signUpButton.setEnabled(active);
    }

    private void buttonCallback(CommandResponse response) {
        switch (response.getExitCode()) {
            case 0:
                // SUCCESS -> OPEN MAIN FRAME
                setVisible(false);
                Frame mainFrame = new MainFrame(cm, Color.BLUE, usernameField.getText());
                mainFrame.setVisible(true);
                break;
            case 2:
                setAuthErrorLabel("noConnection");
                break;
            case 12:
                setAuthErrorLabel("emptyUsername");
                break;
            default:
                setAuthErrorLabel(response.getMessage());
        }
    }

    private void initButtons() {
        logInButton.addActionListener(event -> {
            activeAuthInterface(false);
            new Thread(() -> {
                Queue<String> input = new ArrayDeque<>();
                input.add(usernameField.getText());
                input.add(new String(passwordField.getPassword()));
                cm.setUIMode(input);
                CommandResponse response = cm.runCommand("/sign_in");
                buttonCallback(response);
                activeAuthInterface(true);
            }).start();
        });

        signUpButton.addActionListener(event -> {
            activeAuthInterface(false);
            new Thread(() -> {
                Queue<String> input = new ArrayDeque<>();
                input.add(usernameField.getText());
                String password = new String(passwordField.getPassword());
                input.add(password);
                input.add(password);
                cm.setUIMode(input);
                CommandResponse response = cm.runCommand("/sign_up");
                buttonCallback(response);
                activeAuthInterface(true);
            }).start();
        });
    }

    private void layoutFrame() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 2, 5, 2);

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        add(usernameText, c);

        c.gridx++;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.EAST;
        add(usernameField, c);

        c.gridx = 0;
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        add(passwordText, c);

        c.gridx++;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.EAST;
        add(passwordField, c);

        c.gridx = 0;
        c.gridy++;
        add(signUpButton, c);

        c.gridx++;
        add(logInButton, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridheight = 5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(authErrorLabel, c);
    }
}
