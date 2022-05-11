package gui.main_frame;

import commands.CommandManager;
import gui.addition.MyLayout;
import localization.MyBundle;
import network.CommandResponse;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;

public class TopPanel extends JPanel {
    private final MyBundle bundle = MyBundle.getBundle("gui");
    private final CommandManager cm;
    private final Mediator mediator;

    private final JButton clearButton = new JButton(bundle.getString("clearButton"));
    private final JButton execScriptButton = new JButton(bundle.getString("execScriptButton"));
    private final JButton fileChooseButton = new JButton(bundle.getString("fileNotChooseButton"));
    private File chosenFile;
    private final JFileChooser fc = new JFileChooser();
    private final JButton exitButton = new JButton(bundle.getString("exitButton"));

    protected CommandResponse lastCommandResponse;

    public TopPanel(String username, Mediator mediator) {
        this.cm = mediator.getCommandManager();
        this.mediator = mediator;
        makeLayout(username);
        initElements();
        addListeners();
    }

    private void initElements() {
        try {
            fc.setCurrentDirectory(
                    new File(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (URISyntaxException ignored) {}

        execScriptButton.setEnabled(false);
    }

    private void makeLayout(String username) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        clearButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JLabel loginInfo = new JLabel(bundle.getString("user") + ": " + username);
        loginInfo.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        exitButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        add(clearButton);
        add(MyLayout.hspace(10));
        add(execScriptButton);
        add(fileChooseButton);
        add(Box.createHorizontalGlue());
        add(loginInfo);
        add(MyLayout.hspace(8));
        add(exitButton);
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
    }

    private void addListeners() {
        clearButton.addActionListener(event -> new Thread(() -> {
            mediator.notify(this, "disableButtons");
            lastCommandResponse = cm.runCommand("clear");
            mediator.notify(this, "printToLabel");
            mediator.notify(this, "enableButtons");
        }).start());

        execScriptButton.addActionListener(event -> new Thread(() -> {
            mediator.notify(this, "disableButtons");
            lastCommandResponse = cm.runCommand("execute_script", chosenFile.getAbsolutePath());
            mediator.notify(this, "printToLabel");
            mediator.notify(this, "enableButtons");
        }).start());

        fileChooseButton.addActionListener(event -> new Thread(() -> {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                chosenFile = fc.getSelectedFile();
                fileChooseButton.setText(chosenFile.getName());
                execScriptButton.setEnabled(true);
            }
        }).start());

        exitButton.addActionListener(event -> new Thread(() -> {
            cm.runCommand("/sign_out");
            mediator.notify(this, "signOut");
        }).start());
    }

    protected void setEnabledButtons(boolean b) {
        clearButton.setEnabled(b);
        fileChooseButton.setEnabled(b);
        if (chosenFile == null) {
            execScriptButton.setEnabled(false);
        } else {
            execScriptButton.setEnabled(b);
        }
    }
    }

