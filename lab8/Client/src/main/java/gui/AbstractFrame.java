package gui;

import commands.CommandManager;
import localization.MyBundle;
import localization.MyLocale;

import javax.swing.*;

public abstract class AbstractFrame extends JFrame implements Localisable {
    protected final CommandManager cm;

    public final static MyBundle bundle = MyBundle.getBundle("gui");
    protected final JMenu language = new JMenu(bundle.getString("language"));

    protected AbstractFrame(CommandManager cm) {
        this.cm = cm;
    }

    public abstract void updateLabels();

    protected void initMenuItems() {
        JMenuItem ru_item = new JMenuItem("Русский");
        JMenuItem en_item = new JMenuItem("English (IN)");
        JMenuItem no_item = new JMenuItem("Norsk");
        JMenuItem sl_item = new JMenuItem("Shqiptare");

        language.add(ru_item);
        language.add(en_item);
        language.add(no_item);
        language.add(sl_item);

        ru_item.addActionListener(event -> {
            bundle.setMyLocale(MyLocale.RUSSIAN);
            updateLabels();
        });
        en_item.addActionListener(event -> {
            bundle.setMyLocale(MyLocale.ENGLISH);
            updateLabels();
        });
        no_item.addActionListener(event -> {
            bundle.setMyLocale(MyLocale.NORWAY);
            updateLabels();
        });
        sl_item.addActionListener(event -> {
            bundle.setMyLocale(MyLocale.ALBANIAN);
            updateLabels();
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(language);
        setJMenuBar(menuBar);
    }


}
