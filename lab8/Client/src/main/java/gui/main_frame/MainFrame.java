package gui.main_frame;

import commands.CommandManager;
import gui.AbstractFrame;
import gui.AuthFrame;
import gui.addition.MyLayout;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends AbstractFrame {
    private final String username;

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

    // Buttons
    private final JButton exitButton = new JButton(bundle.getString("exitButton"));

    private void initElements() {
        initMenuItems();

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

//        tabbedPane.addChangeListener(e ->
//                new Thread(() -> {
//                    JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
//                    int index = sourceTabbedPane.getSelectedIndex();
//                    //cancel();
//                    if (index == 0) {
//                        nameFilter.setEnabled(true);
//                        creationDateFilter.setEnabled(true);
//                        genreFilter.setEnabled(true);
//                        ratingFilter.setEnabled(true);
//                    } else {
//                        isRowSelected = true;
//                        table.getSelectionModel().clearSelection();
//                        isRowSelected = false;
//                        nameFilter.setEnabled(false);
//                        creationDateFilter.setEnabled(false);
//                        genreFilter.setEnabled(false);
//                        ratingFilter.setEnabled(false);
//                    }
//                }).start());
    }

    private void makeLayout() {
        initElements();

        JLabel loginInfo = new JLabel(bundle.getString("user") + ": " + username);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.X_AXIS));
        exitButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        loginInfo.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        userInfo.add(Box.createHorizontalGlue());
        userInfo.add(loginInfo);
        userInfo.add(MyLayout.hspace(8));
        userInfo.add(exitButton);
        userInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

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
        add(userInfo, BorderLayout.NORTH);
        add(tableGraphicsFilterPanelsWrapper, BorderLayout.CENTER);
        add(sidePanelWrapper, BorderLayout.EAST);
        setTitle(bundle.getString("titleMain"));
        setSize(1500, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public MainFrame(String username, CommandManager cm) {
        super(cm);
        this.username = username;

        this.tablePanel = new TablePanel();
        tablePanel.refresh(cm.getServerCollection());
        this.sidePanel = new SidePanel(username, cm, tablePanel);
        this.filterPanel = new FilterPanel(cm, tablePanel);

        makeLayout();
        addListeners();
    }

    protected void updateLabels() {
        // todo update all labels
        setTitle(bundle.getString("titleMain"));
        language.setText(bundle.getString("language"));
    }

    private void addListeners() {
        exitButton.addActionListener(event -> new Thread(() -> {
            cm.runCommand("/sign_out");
            dispose();
            new AuthFrame(cm);
        }).start());

        tablePanel.table.getSelectionModel().addListSelectionListener(event -> {
            if (!tablePanel.isRowSelected) {
                new Thread(() -> {
                    int index = tablePanel.table.getSelectedRow();
                    sidePanel.setFields(tablePanel.getTableRow(index));
                    sidePanel.
                }).start();
            }
        });

//        // double value validator
//        xField.addKeyListener(new KeyAdapter() {
//            public void keyTyped(KeyEvent e) {
//                char c = e.getKeyChar();
//                if (!
//                        ((c >= '1' && c <= '9') ||
//                        (c == '0' && !xField.getText().isEmpty()) ||
//                        (c == '-' && !xField.getText().contains("-")) ||
//                        (c == '.' && !xField.getText().isEmpty() && !xField.getText().contains(".")) ||
//                        (c == KeyEvent.VK_BACK_SPACE))
//                ){
//                    e.consume();  // if it's not a digit or dot, ignore the event
//                }
//            }
//        });
//
//        // integer value validator
//        yField.addKeyListener(new KeyAdapter() {
//            public void keyTyped(KeyEvent e) {
//                char c = e.getKeyChar();
//                if (!
//                        ((c >= '1' && c <= '9') ||
//                        (c == '0' && !yField.getText().isEmpty()) ||
//                        (c == '-' && !yField.getText().contains("-")) ||
//                        (c == KeyEvent.VK_BACK_SPACE))
//                ){
//                    e.consume();  // if it's not a digit, ignore the event
//                }
//            }
//        });
    }
}