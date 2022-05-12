package gui.main_frame;

import commands.CommandManager;
import console.FileManager;
import console.MyFile;
import gui.AbstractFrame;
import gui.AuthFrame;
import gui.addition.MyLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends AbstractFrame implements Mediator {
    // Top Panel
    private final TopPanel topPanel;

    // Side Panel
    private final SidePanel sidePanel;
    private final JPanel sidePanelWrapper = new JPanel();

    // Table Panel
    private final TablePanel tablePanel;
    // Graphics Panel
    private final JPanel graphicsPanel = new JPanel();
    // Table & Graphics
    private final JPanel tableGraphicsPanelsWrapper = new JPanel();

    // Filter Panel
    private final FilterPanel filterPanel;
    // TableGraphics Panel & Filter Panel
    private final JPanel tableGraphicsFilterPanelsWrapper = new JPanel();

    private void initElements() {
        initMenuItems();

        setIconImage(new ImageIcon("Client/src/main/java/gui/static/netflix.png").getImage());

        JScrollPane scrollPane = new JScrollPane(tablePanel.table);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        graphicsPanel.setLayout(null);

        tableGraphicsPanelsWrapper.setLayout(new BorderLayout());
        Font font3 = new Font("Verdana", Font.PLAIN, 12);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(font3);
        JLabel tableP = new JLabel(bundle.getString("table"));
        tabbedPane.addTab(tableP.getText(), scrollPane);
        JLabel graphics = new JLabel(bundle.getString("graphics"));
        tabbedPane.addTab(graphics.getText(), graphicsPanel);
        tableGraphicsPanelsWrapper.add(tabbedPane, BorderLayout.CENTER);
    }

    private void makeLayout() {
        initElements();

        tableGraphicsFilterPanelsWrapper.setLayout(new BorderLayout());
        tableGraphicsFilterPanelsWrapper.add(tableGraphicsPanelsWrapper, BorderLayout.CENTER);
        tableGraphicsFilterPanelsWrapper.add(filterPanel, BorderLayout.NORTH);

        sidePanelWrapper.setLayout(new BoxLayout(sidePanelWrapper, BoxLayout.X_AXIS));
        sidePanelWrapper.add(MyLayout.hspace(15));
        sidePanelWrapper.add(sidePanel);
        sidePanelWrapper.add(MyLayout.hspace(10));
        sidePanelWrapper.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));

        // Main Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tableGraphicsFilterPanelsWrapper, BorderLayout.CENTER);
        add(sidePanelWrapper, BorderLayout.EAST);
        setTitle(bundle.getString("titleMain"));
        setMinimumSize(new Dimension(1252,650));
        setSize(1500, 800);
        setMaximumSize(new Dimension(1702, 900));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public MainFrame(String username, CommandManager cm) {
        super(cm);

        this.topPanel = new TopPanel(username, this);
        this.tablePanel = new TablePanel();
        this.filterPanel = new FilterPanel(this);
        this.sidePanel = new SidePanel(username, this);

        makeLayout();
        addListeners();
    }

    public void updateLabels() {
        language.setText(bundle.getString("language"));
        filterPanel.updateLabels();
        topPanel.updateLabels();
        sidePanel.updateLabels();
        tablePanel.updateLabels();

        setTitle(bundle.getString("titleMain"));
    }

    private void addListeners() {
        tablePanel.table.getSelectionModel().addListSelectionListener(event -> {
            if (!tablePanel.table.getSelectionModel().isSelectionEmpty()) {
                new Thread(() -> {
                    int index = tablePanel.table.getSelectedRow();
                    sidePanel.setFields(tablePanel.getTableRow(index));
                    sidePanel.clearPrintLabel();
                }).start();
            }
        });
    }

    public CommandManager getCommandManager() {
        return cm;
    }

    public void notify(JPanel sender, String event) {
        if (event.equals("enableButtons")) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return;
            }
            sidePanel.setEnabledButtons(true);
            topPanel.setEnabledButtons(true);
        } else if (event.equals("disableButtons")) {
            sidePanel.setEnabledButtons(false);
            topPanel.setEnabledButtons(false);
        } else if (event.equals("signOut")) {
            filterPanel.refresher.interrupt();
            dispose();
            new AuthFrame(cm);
        } else if (sender == filterPanel) {
            switch (event) {
                case "gotCollection":
                    if (!cm.isConnected) {
                        cm.isConnected = true;
                        sidePanel.clearPrintLabel();
                        setTitle(bundle.getString("titleMain"));
                    }
                    break;
                case "filtersApplied":
                    tablePanel.refresh(filterPanel.filteredQueue);
                    break;
                case "noConnection":
                    cm.isConnected = false;
                    setTitle(bundle.getString("titleMain") + " - " + bundle.getString("waitingConnection"));
                    break;
            }
        } else if (sender == topPanel && event.equals("printToLabel")) {
            sidePanel.printToLabel(topPanel.lastCommandResponse.getMessage(),
                    topPanel.lastCommandResponse.getExitCode() == 0);
        } else if (sender == sidePanel && event.equals("clearTableSelection")) {
            tablePanel.clearSelection();
        }
    }
}