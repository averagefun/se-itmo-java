package gui.main_frame;

import commands.CommandManager;
import gui.Localisable;
import gui.addition.MyLayout;
import localization.MyBundle;
import network.CommandResponse;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class TopPanel extends JPanel implements Localisable {
    private final MyBundle bundle = MyBundle.getBundle("gui");
    private final CommandManager cm;
    private final Mediator mediator;

    private final JLabel topPanelLabel = new JLabel(bundle.getString("topPanelLabel"));
    private final JButton clearButton = new JButton(bundle.getString("clearButton"));
    private final JButton execScriptButton = new JButton(bundle.getString("execScriptButton"));
    private final JButton fileChooseButton = new JButton(bundle.getString("fileNotChooseButton"));
    private File chosenFile;
    private final JFileChooser fc = new JFileChooser();

    private final String username;
    private final JLabel loginInfo = new JLabel();
    private final JButton exitButton = new JButton(bundle.getString("exitButton"));

    protected CommandResponse lastCommandResponse;

    public void updateLabels() {
        topPanelLabel.setText(bundle.getString("topPanelLabel"));
        clearButton.setText(bundle.getString("clearButton"));
        execScriptButton.setText(bundle.getString("execScriptButton"));
        fileChooseButton.setText(bundle.getString("fileNotChooseButton"));
        loginInfo.setText(bundle.getString("user") + ": " + username);
        exitButton.setText(bundle.getString("exitButton"));
    }

    public TopPanel(String username, Mediator mediator) {
        this.username = username;
        this.cm = mediator.getCommandManager();
        this.mediator = mediator;
        makeLayout();
        initElements(username);
        addListeners();
    }

    private void initElements(String username) {
        loginInfo.setText(bundle.getString("user") + ": " + username);

        try {
            fc.setCurrentDirectory(
                    new File(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (URISyntaxException ignored) {}

        execScriptButton.setEnabled(false);
    }

    private void makeLayout() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        topPanelLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        clearButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        loginInfo.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        exitButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        add(MyLayout.hspace(5));
        add(topPanelLabel);
        add(MyLayout.hspace(10));
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
        clearButton.addActionListener(event -> {
            mediator.notify(this, "disableButtons");

            new SwingWorker<CommandResponse, Void>() {
                @Override
                protected CommandResponse doInBackground() {
                    return cm.runCommand("clear");
                }

                @Override
                protected void done() {
                    try {
                        lastCommandResponse = get();
                    } catch (InterruptedException | ExecutionException ignored) {}
                    finally {
                        mediator.notify(TopPanel.this, "printToLabel");
                        mediator.notify(TopPanel.this, "enableButtons");
                    }
                }
            }.execute();
        });

        execScriptButton.addActionListener(event -> {
            mediator.notify(this, "disableButtons");

            new SwingWorker<CommandResponse, Void>() {
                @Override
                protected CommandResponse doInBackground() {
                    return cm.runCommand("execute_script", chosenFile.getAbsolutePath());
                }

                @Override
                protected void done() {
                    try {
                        lastCommandResponse = get();
                    } catch (InterruptedException | ExecutionException ignored) {}
                    finally {
                        mediator.notify(TopPanel.this, "printToLabel");
                        mediator.notify(TopPanel.this, "enableButtons");
                    }
                }
            }.execute();
        });

        fileChooseButton.addActionListener(event -> {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                chosenFile = fc.getSelectedFile();
                fileChooseButton.setText(chosenFile.getName());
                execScriptButton.setEnabled(true);
            }
        });

        exitButton.addActionListener(event -> new SwingWorker<CommandResponse, Void>() {
            @Override
            protected CommandResponse doInBackground() {
                return cm.runCommand("/sign_out");
            }

            @Override
            protected void done() {
                mediator.notify(TopPanel.this, "signOut");
            }
        }.execute());
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

