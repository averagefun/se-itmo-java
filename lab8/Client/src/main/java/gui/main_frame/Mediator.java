package gui.main_frame;

import commands.CommandManager;

import javax.swing.*;

public interface Mediator {
    CommandManager getCommandManager();
    void notify(JPanel sender, String event);
}
