package gui;

import client.Client;
import commands.CommandManager;
import console.Console;
import data.Movie;
import network.CommandResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

class MainFrame extends AbstractFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean clearSelection;
    private boolean isTable = true;
    boolean isExit = false;
    private final boolean isnChange = false;
    private final CopyOnWriteArrayList<Movie> creatureList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Movie> filteredList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Movie> winniePoohList;

    private static final JLabel connectionText = new JLabel();
    private static final boolean isConnected = false;
    private final JLabel infoObjectText = new JLabel(bundle.getString("info") + ":");
    private final JLabel nameText = new JLabel(bundle.getString("name") + ":");
    private final JLabel familyText = new JLabel(bundle.getString("family") + ":");
    private final JLabel hungerText = new JLabel(bundle.getString("hunger") + ":");
    private final JLabel locationText = new JLabel(bundle.getString("location") + ":");
    private final JLabel timeText = new JLabel(bundle.getString("creationTime") + ":");
    private final JLabel colorText = new JLabel(bundle.getString("color") + ":");
    private final JLabel inventoryText = new JLabel(bundle.getString("inventory") + ":");

    private final JTextField nameValue = new JTextField();
    private final JTextField familyValue = new JTextField();
    private final JTextField hungerValue = new JTextField();
    private final JTextField timeValue = new JTextField();
    private final JTextField colorValue = new JTextField();
    private final JTextField inventoryValue = new JTextField();

    JPanel graphicsPanel;

    private final JLabel infoText = new JLabel(" " + bundle.getString("greeting"));

    private final JLabel nameFromTo = new JLabel(bundle.getString("name"));
    private final JTextField nameTo = new JTextField();
    private final JLabel familyFromTo = new JLabel(bundle.getString("family"));
    private final JTextField familyTo = new JTextField();
    private final JLabel timeFromTo = new JLabel(bundle.getString("time"));
    private final JTextField timeTo = new JTextField();
    private final JTextField xTo = new JTextField();
    private final JTextField yTo = new JTextField();
    private final JTextField sizeTo = new JTextField();
    private final JLabel xText = new JLabel("X:");
    private final JLabel yText = new JLabel("Y:");

    private final JLabel locationFromTo = new JLabel(bundle.getString("location"));
    private final JLabel infoConnectionText = new JLabel("<html><h1 align=\"center\">" + bundle.getString("kek") + "</h1></html>");
    private final JLabel hungerFromTo = new JLabel(bundle.getString("hunger"));
    private final JTextField hungerTo = new JTextField();
    private final JLabel size = new JLabel(bundle.getString("size") + ":");
    private final JLabel sizeText = new JLabel(bundle.getString("size") + ":");
    private final JLabel xFrom = new JLabel("0");

    private final JSlider xFromSlider = new JSlider();
    private final JSlider yFromSlider = new JSlider();
    private final JSlider sizeFromSlider = new JSlider();

    private final JButton changeButton = new JButton(bundle.getString("change"));
    private final JButton newCreatureButton = new JButton(bundle.getString("newCreature"));
    private final JButton addButton = new JButton(bundle.getString("add"));
    private final JButton clearButton = new JButton(bundle.getString("clear"));
    private final JButton addIfMaxButton = new JButton(bundle.getString("add_if_max"));
    private final JButton removeButton = new JButton(bundle.getString("remove"));
    private final JButton cancelButton = new JButton(bundle.getString("cancel"));
    private final JButton refreshButton = new JButton(bundle.getString("refresh"));

    JPanel p4extended;
    JPanel p1,p2,p3,p4;

    private JComboBox<String> locationBox;
    private JComboBox<String> locationComboBox;
    private JComboBox<String> colorComboBox;
    private Movie chosenCreature;
    private final DateTimeFormatter filterDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");

    private final Font font1 = new Font("Calibri", Font.BOLD, 16);
    private final Font defaultFont = language.getFont();

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName("localhost");
        Client client = new Client(inetAddress);

        Scanner sc = new Scanner(System.in);
        Console console = new Console(sc, true);
        CommandManager cm = new CommandManager(console, client, "client.cfg");
        Frame f = new MainFrame(cm, Color.BLUE, "jw");
        f.setVisible(true);
    }

    protected void updateLabels() {
        setTitle(bundle.getString("titleMain"));

        language.setText(bundle.getString("language"));
    }

    public MainFrame(CommandManager cm, Color color, String login) {
        super(cm);
        layoutFrame();
        CommandResponse cRes = cm.runCommand("$get");

        @SuppressWarnings("unchecked")
        PriorityQueue<Movie> pq = (PriorityQueue<Movie>) cRes.getObject();
        refreshTable(pq);
    }

    private void checkFilters() {
        if (!xTo.getText().isEmpty() && (notANumeric(xTo.getText()) || (Integer.valueOf(xTo.getText()) > 1000 || Integer.valueOf(xTo.getText()) < 0)))
            printText(bundle.getString("incorrectX"), true);
        else if (!yTo.getText().isEmpty() && (notANumeric(yTo.getText()) || (Integer.valueOf(yTo.getText()) > 1000 || Integer.valueOf(yTo.getText()) < 0)))
            printText(bundle.getString("incorrectY"), true);
        else if (!sizeTo.getText().isEmpty() && (notANumeric(sizeTo.getText()) || (Integer.valueOf(sizeTo.getText()) < 10 || Integer.valueOf(sizeTo.getText()) > 99)))
            printText(bundle.getString("incorrectSize"), true);
        else if (!hungerTo.getText().isEmpty() && (notANumeric(hungerTo.getText()) || Integer.valueOf(hungerTo.getText()) < 1))
            printText(bundle.getString("incorrectHunger"), true);
        else if (!timeTo.getText().isEmpty()) {
            try {
                filterDateTimeFormatter.parse(timeTo.getText());
                printText("", false);
                //applyFilters();
            } catch (DateTimeParseException e) {
                printText(bundle.getString("incorrectDate") + ": dd.MM.yy HH:mm:ss!", true);
            }

        } else {
            boolean isFiltered = true;
            if (isFiltered)
                printText("", false);
            //applyFilters();
        }
    }

    private boolean notANumeric(String str) {
        return !str.matches("[-+]?\\d+");
    }

    private void printText(String message, boolean isError) {
        if (isError) infoText.setForeground(Color.RED);
        else infoText.setForeground(Color.GREEN);
        infoText.setText(message);
    }

    private void refreshTable(PriorityQueue<Movie> pq) {
        clearSelection = true;
        table.clearSelection();
        tableModel.setRowCount(0);
        while (!pq.isEmpty())
            addToTable(pq.poll());
        clearSelection = false;
        table.repaint();
        table.revalidate();
    }

    private void addToTable(Movie m) {
        tableModel.addRow(new Object[] {
            m.getId(), m.getUsername(), m.getName()
        });
    }

    private void layoutFrame() {
        initElements();
        createPanels();
        initListeners();

        String login = "jw";
        JLabel loginInfo = new JLabel(bundle.getString("user") + ": " + login);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.X_AXIS));
        JButton exitButton = new JButton(bundle.getString("exitButton"));
        exitButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        loginInfo.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        userInfo.add(Box.createHorizontalGlue());
        userInfo.add(loginInfo);
        userInfo.add(Box.createRigidArea(new Dimension(8, 0)));
        userInfo.add(exitButton);
        userInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel p3extended = new JPanel();
        p3extended.setLayout(new BorderLayout());
        p3extended.add(p2, BorderLayout.NORTH);
        p3extended.add(p3, BorderLayout.CENTER);

        p4extended = new JPanel();
        p4extended.setLayout(new BoxLayout(p4extended, BoxLayout.X_AXIS));
        p4extended.add(Box.createRigidArea(new Dimension(15, 0)));
        p4extended.add(p4);
        p4extended.add(Box.createRigidArea(new Dimension(10, 0)));
        p4extended.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));

        setLayout(new BorderLayout());
        add(userInfo, BorderLayout.NORTH);
        add(p3extended, BorderLayout.CENTER);
        add(p4extended, BorderLayout.EAST);
        setTitle(bundle.getString("titleMain"));
        setSize(1500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initListeners() {
    }

    private void createTable() {
        String[] columns = {
                "Id", "Author", "Name"};
        tableModel = new MyDefaultTableModel();
        tableModel.setColumnIdentifiers(columns);
        table = new JTable(tableModel);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
//        table.getColumnModel().getColumn(3).setPreferredWidth(120);
//        table.getColumnModel().getColumn(4).setPreferredWidth(120);
//        table.getColumnModel().getColumn(5).setPreferredWidth(120);
//        table.getColumnModel().getColumn(6).setPreferredWidth(120);
//        table.getColumnModel().getColumn(7).setPreferredWidth(120);
//        table.getColumnModel().getColumn(8).setPreferredWidth(120);

//        table.getSelectionModel().addListSelectionListener(e -> {
//            if (!clearSelection) {
//                new Thread(() -> {
//                    //cancel();
//                    int index = table.getSelectedRow();
//                    String name = (String) table.getModel().getValueAt(index, 0);
//                    String family = (String) table.getModel().getValueAt(index, 1);
////                    for (Creature cr : creatureList) {
////                        if (cr.getName().equals(name) && cr.getFamily().equals(family)) {
////                            setCreatureInfo(cr);
////                            break;
////                        }
////                    }
//                }).start();
//            }
//        });
    }

    private void createPanels() {
        JPanel p17 = new JPanel();
        p17.setMaximumSize(new Dimension(500, 10));
        p17.setLayout(new GridLayout());
        p17.add(connectionText);
        JPanel refreshButtonPanel = new JPanel();
        refreshButtonPanel.setLayout(new BoxLayout(refreshButtonPanel, BoxLayout.X_AXIS));
        refreshButtonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        refreshButtonPanel.add(refreshButton);
        p17.add(refreshButton);

        JPanel p16 = new JPanel();
        p16.setLayout(new BorderLayout());
        p16.setMaximumSize(new Dimension(500, 50));
        p16.setPreferredSize(new Dimension(500, 50));
        p16.add(infoText);

        JPanel p15 = new JPanel();
        p15.setLayout(new BoxLayout(p15, BoxLayout.X_AXIS));
        infoConnectionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        p15.add(infoConnectionText);

        JPanel p13 = new JPanel();
        p13.setLayout(new BoxLayout(p13, BoxLayout.X_AXIS));
        p13.add(Box.createRigidArea(new Dimension(0, 70)));
        p13.add(infoObjectText);

        JPanel p12 = new JPanel();
        p12.setLayout(new BoxLayout(p12, BoxLayout.X_AXIS));
        nameValue.setPreferredSize(new Dimension(190, 30));
        nameValue.setMaximumSize(new Dimension(190, 30));
        p12.add(Box.createRigidArea(new Dimension(7, 0)));
        p12.add(nameText, Component.LEFT_ALIGNMENT);
        p12.add(Box.createHorizontalGlue());
        p12.add(nameValue, Component.RIGHT_ALIGNMENT);

        JPanel p11 = new JPanel();
        p11.setLayout(new BoxLayout(p11, BoxLayout.X_AXIS));
        familyValue.setPreferredSize(new Dimension(190, 30));
        familyValue.setMaximumSize(new Dimension(190, 30));
        p11.add(Box.createRigidArea(new Dimension(7, 0)));
        p11.add(familyText, Component.LEFT_ALIGNMENT);
        p11.add(Box.createHorizontalGlue());
        p11.add(familyValue, Component.RIGHT_ALIGNMENT);

        JPanel p111 = new JPanel();
        p111.setLayout(new BoxLayout(p111, BoxLayout.X_AXIS));
        hungerValue.setPreferredSize(new Dimension(190, 30));
        hungerValue.setMaximumSize(new Dimension(190, 30));
        p111.add(Box.createRigidArea(new Dimension(7, 0)));
        p111.add(hungerText, Component.LEFT_ALIGNMENT);
        p111.add(Box.createHorizontalGlue());
        p111.add(hungerValue, Component.RIGHT_ALIGNMENT);

        JPanel p10 = new JPanel();
        p10.setLayout(new BoxLayout(p10, BoxLayout.X_AXIS));
        timeValue.setPreferredSize(new Dimension(190, 30));
        timeValue.setMaximumSize(new Dimension(190, 30));
        p10.add(Box.createRigidArea(new Dimension(7, 0)));
        p10.add(timeText, Component.LEFT_ALIGNMENT);
        p10.add(Box.createHorizontalGlue());
        p10.add(timeValue, Component.RIGHT_ALIGNMENT);

        JPanel p9 = new JPanel();
        p9.setLayout(new BoxLayout(p9, BoxLayout.X_AXIS));
        locationBox.setPreferredSize(new Dimension(190, 30));
        locationBox.setMaximumSize(new Dimension(190, 30));
        p9.add(Box.createRigidArea(new Dimension(7, 0)));
        p9.add(locationText, Component.LEFT_ALIGNMENT);
        p9.add(Box.createHorizontalGlue());
        p9.add(locationBox, Component.RIGHT_ALIGNMENT);

        JPanel p99 = new JPanel();
        p99.setLayout(new BoxLayout(p99, BoxLayout.X_AXIS));
        colorValue.setPreferredSize(new Dimension(190, 30));
        colorValue.setMaximumSize(new Dimension(190, 30));
        p99.add(Box.createRigidArea(new Dimension(7, 0)));
        p99.add(colorText, Component.LEFT_ALIGNMENT);
        p99.add(Box.createHorizontalGlue());
        p99.add(colorValue, Component.RIGHT_ALIGNMENT);

        JPanel p999 = new JPanel();
        p999.setLayout(new BoxLayout(p999, BoxLayout.X_AXIS));
        p999.add(Box.createRigidArea(new Dimension(7, 0)));
        p999.add(inventoryText, Component.LEFT_ALIGNMENT);
        p999.add(Box.createHorizontalGlue());
        inventoryValue.setPreferredSize(new Dimension(190, 30));
        inventoryValue.setMaximumSize(new Dimension(190, 30));
        p999.add(inventoryValue, Component.RIGHT_ALIGNMENT);

        JPanel p8 = new JPanel();
        p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
        xFromSlider.setMaximumSize(new Dimension(150, 17));
        JLabel x = new JLabel("X:");
        p8.add(x, Component.LEFT_ALIGNMENT);
        p8.add(Box.createRigidArea(new Dimension(100, 10)));
        p8.add(xFromSlider);
        p8.add(Box.createHorizontalGlue());
        p8.add(xFrom, Component.RIGHT_ALIGNMENT);

        JPanel p7 = new JPanel();
        p7.setLayout(new BoxLayout(p7, BoxLayout.X_AXIS));
        yFromSlider.setMaximumSize(new Dimension(150, 17));
        JLabel y = new JLabel("Y:");
        p7.add(y, Component.LEFT_ALIGNMENT);
        p7.add(Box.createRigidArea(new Dimension(100, 10)));
        p7.add(yFromSlider);
        p7.add(Box.createHorizontalGlue());
        //p7.add(yFrom, Component.RIGHT_ALIGNMENT);

        JPanel p6 = new JPanel();
        p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
        sizeFromSlider.setMaximumSize(new Dimension(250, 17));
        p6.add(size, Component.LEFT_ALIGNMENT);
        p6.add(Box.createHorizontalGlue());
        p6.add(sizeFromSlider);
        p6.add(Box.createRigidArea(new Dimension(25, 0)));
        //p6.add(sizeFrom, Component.RIGHT_ALIGNMENT);

        JPanel p5 = new JPanel();
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        p5.add(changeButton);
        p5.add(Box.createRigidArea(new Dimension(5, 0)));
        p5.add(newCreatureButton);
        p5.add(Box.createRigidArea(new Dimension(5, 0)));
        p5.add(clearButton);
        p5.add(Box.createRigidArea(new Dimension(5, 0)));
        p5.add(cancelButton);

        JPanel p55 = new JPanel();
        p55.setLayout(new BoxLayout(p55, BoxLayout.X_AXIS));
        p55.add(addButton);
        p55.add(Box.createRigidArea(new Dimension(10, 0)));
        p55.add(addIfMaxButton);
        p55.add(Box.createRigidArea(new Dimension(10, 0)));
        p55.add(removeButton);

        p4 = new JPanel();
        p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
        p4.setPreferredSize(new Dimension(350, 1000));
        p4.setMaximumSize(new Dimension(350, 1000));
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p17);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p16);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p15.setAlignmentX(Component.CENTER_ALIGNMENT);
        p4.add(p15);
        p4.add(Box.createVerticalGlue());
        p4.add(p13);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p12);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p11);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p111);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p10);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p9);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p99);
        p4.add(Box.createRigidArea(new Dimension(0, 5)));
        p4.add(p999);
        p4.add(Box.createRigidArea(new Dimension(0, 30)));
        p4.add(p8);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p7);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p6);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p5);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));
        p4.add(p55);
        p4.add(Box.createRigidArea(new Dimension(0, 10)));

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        graphicsPanel = new JPanel();
        graphicsPanel.setLayout(null);


        p3 = new JPanel();
        p3.setLayout(new BorderLayout());
        Font font3 = new Font("Verdana", Font.PLAIN, 12);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(font3);
        JLabel tableP = new JLabel(bundle.getString("table"));
        tabbedPane.addTab(tableP.getText(), scrollPane);
        JLabel graphics = new JLabel(bundle.getString("graphics"));
        tabbedPane.addTab(graphics.getText(), graphicsPanel);
        p3.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addChangeListener(e ->
                new Thread(() -> {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    //cancel();
                    if (index == 0) {
                        isTable = true;
                        nameTo.setEnabled(true);
                        familyTo.setEnabled(true);
                        hungerTo.setEnabled(true);
                        timeTo.setEnabled(true);
                        locationComboBox.setEnabled(true);
                        xTo.setEnabled(true);
                        yTo.setEnabled(true);
                        sizeTo.setEnabled(true);
                        colorComboBox.setEnabled(true);
                    } else {
                        isTable = false;
                        clearSelection = true;
                        table.getSelectionModel().clearSelection();
                        clearSelection = false;
                        nameTo.setEnabled(false);
                        familyTo.setEnabled(false);
                        hungerTo.setEnabled(false);
                        timeTo.setEnabled(false);
                        locationComboBox.setEnabled(false);
                        xTo.setEnabled(false);
                        yTo.setEnabled(false);
                        sizeTo.setEnabled(false);
                        colorComboBox.setEnabled(false);
                    }
                }).start());

        //Panels
        p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        nameTo.setMaximumSize(new Dimension(119, 50));
        nameTo.setPreferredSize(new Dimension(119, 30));
        p2.add(nameTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        familyTo.setMaximumSize(new Dimension(119, 50));
        familyTo.setPreferredSize(new Dimension(119, 30));
        p2.add(familyTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        hungerTo.setMaximumSize(new Dimension(118, 50));
        hungerTo.setPreferredSize(new Dimension(118, 30));
        p2.add(hungerTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        timeTo.setMaximumSize(new Dimension(118, 50));
        timeTo.setPreferredSize(new Dimension(118, 30));
        p2.add(timeTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        locationComboBox.setMaximumSize(new Dimension(118, 50));
        locationComboBox.setPreferredSize(new Dimension(118, 30));
        p2.add(locationComboBox);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        xTo.setMaximumSize(new Dimension(118, 50));
        xTo.setPreferredSize(new Dimension(118, 30));
        p2.add(xTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        yTo.setMaximumSize(new Dimension(118, 50));
        yTo.setPreferredSize(new Dimension(118, 30));
        p2.add(yTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        sizeTo.setMaximumSize(new Dimension(118, 50));
        sizeTo.setPreferredSize(new Dimension(118, 30));
        p2.add(sizeTo);
        p2.add(Box.createRigidArea(new Dimension(5, 0)));
        colorComboBox.setMaximumSize(new Dimension(118, 50));
        colorComboBox.setPreferredSize(new Dimension(118, 30));
        p2.add(colorComboBox);
        p2.add(Box.createHorizontalGlue());
        p2.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel pInfoExtended = new JPanel();
        pInfoExtended.setLayout(new BorderLayout());
    }

    private void initElements() {
        DateTimeFormatter displayDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(
                bundle.getMyLocale().getLocale());

        nameValue.setEditable(false);
        familyValue.setEditable(false);
        hungerValue.setEditable(false);
        timeValue.setEditable(false);
        colorValue.setEditable(false);
        inventoryValue.setEditable(false);
        JTextField xValue = new JTextField();
        xValue.setEditable(false);
        JTextField yValue = new JTextField();
        yValue.setEditable(false);
        JTextField sizeValue = new JTextField();
        sizeValue.setEditable(false);

        initMenuItems();

        // Right panel elements
        connectionText.setBackground(Color.BLACK);
        connectionText.setOpaque(true);
        connectionText.setText("");
        connectionText.setBackground(Color.BLACK);
        Font font = new Font("Sans-Serif", Font.PLAIN, 16);
        connectionText.setFont(font);
        infoText.setBackground(Color.BLACK);
        infoText.setOpaque(true);
        infoText.setFont(font);
        infoText.setForeground(Color.GREEN);

        timeTo.setToolTipText(bundle.getString("format") + ": dd.MM.yy HH:mm:ss");
        xFromSlider.setMinimum(0);
        xFromSlider.setMaximum(1000);
        xFromSlider.setValue(0);

        String topFloorComboBox = bundle.getString("TopFloor");
        String groundFloorComboBox = bundle.getString("GroundFloor");
        String yardComboBox = bundle.getString("Yard");
        String hillComboBox = bundle.getString("Hill");
        String hangarComboBox = bundle.getString("Hangar");
        String footPathComboBox = bundle.getString("FootPath");
        String lightHouseComboBox = bundle.getString("LightHouse");
        String nanComboBox = bundle.getString("NaN");
        String all = bundle.getString("all");
        String[] locationsArray = {
                all,
                topFloorComboBox,
                groundFloorComboBox,
                yardComboBox,
                hillComboBox,
                hangarComboBox,
                footPathComboBox,
                lightHouseComboBox,
                nanComboBox
        };
        String[] locationsArray1 = {
                "",
                topFloorComboBox,
                groundFloorComboBox,
                yardComboBox,
                hillComboBox,
                hangarComboBox,
                footPathComboBox,
                lightHouseComboBox,
                nanComboBox
        };
        String fernGreenComboBox = bundle.getString("fernGreen");
        String blackComboBox = bundle.getString("black");
        String whiteComboBox = bundle.getString("white");
        String pareGoldComboBox = bundle.getString("pareGold");
        String deepRedComboBox = bundle.getString("deepRed");
        String purpleComboBox = bundle.getString("purple");
        String otherComboBox = bundle.getString("other");
        String[] colorsArray = {
                all,
                fernGreenComboBox,
                blackComboBox,
                whiteComboBox,
                pareGoldComboBox,
                deepRedComboBox,
                purpleComboBox,
                otherComboBox
        };

        locationBox = new JComboBox<>(locationsArray1);
        locationBox.setEditable(false);
        locationBox.setEnabled(false);
        locationComboBox = new JComboBox<>(locationsArray);
        colorComboBox = new JComboBox<>(colorsArray);

//        nameTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        familyTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        hungerTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        timeTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        xTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        yTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        sizeTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
        final ActionListener actionListener = e -> new Thread(this::checkFilters).start();
        colorComboBox.addActionListener(actionListener);
        locationComboBox.addActionListener(actionListener);

        createTable();
    }
}