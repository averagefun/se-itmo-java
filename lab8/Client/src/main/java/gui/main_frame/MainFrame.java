package gui.main_frame;

import commands.CommandManager;
import gui.AbstractFrame;
import gui.AuthFrame;
import gui.addition.ImageManager;
import gui.addition.MyLayout;
import gui.main_frame.graphics.GraphicsPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends AbstractFrame implements Mediator {
    // Top Panel
    private final TopPanel topPanel;

    // Side Panel
    private final SidePanel sidePanel;
    private final JPanel sidePanelWrapper = new JPanel();

    // Table Panel
    private final TablePanel tablePanel;
    // Graphics Panel
    private final GraphicsPanel graphicsPanel;
    // Table & Graphics
    private final JPanel tableGraphicsPanelsWrapper = new JPanel();

    // Filter Panel
    private final FilterPanel filterPanel;

    // Tabbed Pane
    private final JTabbedPane tabbedPane = new JTabbedPane();

    // TableGraphics Panel & Filter Panel
    private final JPanel tableGraphicsFilterPanelsWrapper = new JPanel();

    private void initElements() {
        initMenuItems();

        setIconImage(ImageManager.getImage("netflix.png"));
    }

    private void makeLayout() {
        graphicsPanel.setLayout(null);

        tableGraphicsPanelsWrapper.setLayout(new BorderLayout());
        tabbedPane.addTab(bundle.getString("tableTabLabel"), new JScrollPane(tablePanel.getTable()));
        tabbedPane.addTab(bundle.getString("graphicsTabLabel"), graphicsPanel);
        tableGraphicsPanelsWrapper.add(tabbedPane, BorderLayout.CENTER);

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
        this.tablePanel = new TablePanel(this);
        this.graphicsPanel = new GraphicsPanel(this);
        this.filterPanel = new FilterPanel(this);
        this.sidePanel = new SidePanel(username, this);

        initElements();
        makeLayout();
    }

    public void updateLabels() {
        language.setText(bundle.getString("language"));
        tabbedPane.setTitleAt(0, bundle.getString("tableTabLabel"));
        tabbedPane.setTitleAt(1, bundle.getString("graphicsTabLabel"));

        filterPanel.updateLabels();
        topPanel.updateLabels();
        sidePanel.updateLabels();
        tablePanel.updateLabels();

        setTitle(bundle.getString("titleMain"));
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
                    tablePanel.refresh(filterPanel.getFilteredQueue());
                    graphicsPanel.refresh(filterPanel.getFilteredQueue());
                    break;
                case "noConnection":
                    cm.isConnected = false;
                    setTitle(bundle.getString("titleMain") + " - " + bundle.getString("waitingConnection"));
                    break;
            }
        } else if (sender == topPanel && event.equals("printToLabel")) {
            sidePanel.updatePrintLabel(topPanel.lastCommandResponse.getMessage(),
                    topPanel.lastCommandResponse.getExitCode() == 0);
        } else if (sender == sidePanel && event.equals("clearTableSelection")) {
            tablePanel.clearSelection();
        } else if (sender == tablePanel && event.equals("rowSelected")) {
            assert sidePanel != null;
            sidePanel.setFields(tablePanel.getSelectedRow());
            sidePanel.clearPrintLabel();
        } else if (sender == graphicsPanel && event.equals("graphicsSelected")) {
            assert sidePanel != null;
            assert graphicsPanel != null;
            sidePanel.setFields(graphicsPanel.getSelectedGraphics());
            sidePanel.clearPrintLabel();
            tablePanel.clearSelection();
        }
    }
}