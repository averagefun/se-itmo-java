package gui;

import client.Client;
import commands.CommandManager;
import console.Console;
import data.Movie;
import data.MovieGenre;
import data.MpaaRating;
import network.CommandResponse;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;

class MainFrame extends AbstractFrame {
    private final String login;

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean clearSelection;

    private static final JLabel connectionText = new JLabel();
    private static final boolean isConnected = false;
    private final JLabel statusLabel = new JLabel(bundle.getString("statusAdd") + ":");
    private final JLabel idLabel = new JLabel(bundle.getString("id") + ":");
    private final JLabel authorLabel = new JLabel(bundle.getString("author") + ":");
    private final JLabel nameLabel = new JLabel(bundle.getString("name") + ":");
    private final JLabel creationDateLabel = new JLabel(bundle.getString("creationDate") + ":");
    private final JLabel oscarsLabel = new JLabel(bundle.getString("oscars") + ":");
    private final JLabel genreLabel = new JLabel(bundle.getString("genre") + ":");
    private final JLabel ratingLabel = new JLabel(bundle.getString("rating") + ":");
    private final JLabel xLabel = new JLabel(bundle.getString("x") + ":");
    private final JLabel yLabel = new JLabel(bundle.getString("y") + ":");


    private final JTextField idField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField creationDateField = new JTextField();
    private final JComboBox<MovieGenre> genreComboBox = new JComboBox<>(MovieGenre.values());
    private final JComboBox<MpaaRating> ratingComboBox = new JComboBox<>(MpaaRating.values());
    private final JComboBox<Integer> oscarsComboBox = new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11});
    private final JTextField xField = new JTextField();
    private final JTextField yField = new JTextField();

    JPanel graphicsPanel;

    private final JLabel infoText = new JLabel(" " + bundle.getString("greeting"));

    private final JTextField idFilter = new JTextField();
    private final JTextField authorFilter = new JTextField();
    private final JTextField nameFilter = new JTextField();
    private final JTextField creationDateFilter = new JTextField();
    private final JComboBox<MovieGenre> genreFilter = new JComboBox<>(MovieGenre.values());
    private final JComboBox<MpaaRating> ratingFilter = new JComboBox<>(MpaaRating.values());
    private final JComboBox<Integer> oscarsFilter = new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11});
    private final JTextField xFilter = new JTextField();
    private final JTextField yFilter = new JTextField();

    private final JButton addButton = new JButton(bundle.getString("addButton"));
    private final JButton toAddButton = new JButton(bundle.getString("toAddButton"));
    private final JButton updateButton = new JButton(bundle.getString("updateButton"));
    private final JButton exitButton = new JButton(bundle.getString("exitButton"));


    JPanel p4extended;
    JPanel filterPanel,p3, sidePanel;

    private JComboBox<String> locationComboBox;
    private JComboBox<String> colorComboBox;
    private final DateTimeFormatter filterDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");

    private final Font font1 = new Font("Calibri", Font.BOLD, 16);
    private final Font defaultFont = language.getFont();

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName("localhost");
        Client client = new Client(inetAddress);

        Scanner sc = new Scanner(System.in);
        Console console = new Console(sc, true);
        CommandManager cm = new CommandManager(console, client, "client.cfg");
        //Frame f = new MainFrame(cm, Color.BLUE, "jw");
        //f.setVisible(true);
    }

    protected void updateLabels() {
        setTitle(bundle.getString("titleMain"));

        language.setText(bundle.getString("language"));
    }

    public MainFrame(CommandManager cm, String login) {
        super(cm);
        this.login = login;
        layoutFrame(login);
        refreshTable(getCollection());
    }

    private boolean notANumeric(String str) {
        return !str.matches("[-+]?\\d+");
    }

    private void printText(String message, boolean isError) {
        if (isError) infoText.setForeground(Color.RED);
        else infoText.setForeground(Color.GREEN);
        infoText.setText(message);
    }

    private PriorityQueue<Movie> getCollection() {
        CommandResponse cRes = cm.runCommand("$get");
        @SuppressWarnings("unchecked")
        PriorityQueue<Movie> pq = (PriorityQueue<Movie>) cRes.getObject();
        return pq;
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
            m.getId(), m.getUsername(), m.getName(), m.getCreationDate(), m.getMovieGenre(),
                m.getMpaaRating(), m.getOscarsCount(), m.getCoordinates().getX(), m.getCoordinates().getY()
        });
    }

    private void initListeners() {
        exitButton.addActionListener(event -> new Thread(() -> {
            cm.runCommand("/sign_out");
            dispose();
            AuthFrame authFrame = new AuthFrame(cm);
            authFrame.display();
        }).start());

        addButton.addActionListener(event -> new Thread(() -> {
            Queue<String> input = new ArrayDeque<>();
            input.add(nameField.getText());
            input.add(xField.getText());
            input.add(yField.getText());
            input.add(Objects.requireNonNull(oscarsComboBox.getSelectedItem()).toString());
            input.add(Objects.requireNonNull(genreComboBox.getSelectedItem()).toString());
            input.add(Objects.requireNonNull(ratingComboBox.getSelectedItem()).toString());
            input.add("Director name");
            input.add("1");
            input.add("WHITE");
            input.add("1");
            input.add("1");
            input.add("Location name");
            cm.setInputValues(input);
            CommandResponse cRes = cm.runCommand("add");
            if (cRes.getExitCode() == 0) {
                refreshTable(getCollection());
                clearRightPanel();
            } else {
                System.out.println(cRes.getExitCode() + " " + cRes.getMessage());
            }
            updateButton.setEnabled(true);
        }).start());

        toAddButton.addActionListener(event -> new Thread(this::setAddModeRightPanel).start());

        updateButton.addActionListener(event -> new Thread(() -> {
            updateButton.setEnabled(false);
            Queue<String> input = new ArrayDeque<>();
            input.add(nameField.getText());
            input.add(xField.getText());
            input.add(yField.getText());
            input.add(Objects.requireNonNull(oscarsComboBox.getSelectedItem()).toString());
            input.add(Objects.requireNonNull(genreComboBox.getSelectedItem()).toString());
            input.add(Objects.requireNonNull(ratingComboBox.getSelectedItem()).toString());
            input.add("<");
            input.add("<");
            input.add("<");
            input.add("<");
            input.add("<");
            input.add("<");
            cm.setInputValues(input);
            CommandResponse cRes = cm.runCommand("update", idField.getText());
            if (cRes.getExitCode() == 0) {
                refreshTable(getCollection());
            }
            updateButton.setEnabled(true);
        }).start());

        // double value validator
        xField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!
                        ((c >= '1' && c <= '9') ||
                        (c == '0' && !xField.getText().isEmpty()) ||
                        (c == '-' && !xField.getText().contains("-")) ||
                        (c == '.' && !xField.getText().isEmpty() && !xField.getText().contains(".")) ||
                        (c == KeyEvent.VK_BACK_SPACE))
                ){
                    e.consume();  // if it's not a digit or dot, ignore the event
                }
            }
        });

        // integer value validator
        yField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!
                        ((c >= '1' && c <= '9') ||
                        (c == '0' && !yField.getText().isEmpty()) ||
                        (c == '-' && !yField.getText().contains("-")) ||
                        (c == KeyEvent.VK_BACK_SPACE))
                ){
                    e.consume();  // if it's not a digit, ignore the event
                }
            }
        });
    }

    private void layoutFrame(String login) {
        initElements();
        createPanels();

        JLabel loginInfo = new JLabel(bundle.getString("user") + ": " + login);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.X_AXIS));
        exitButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        loginInfo.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        userInfo.add(Box.createHorizontalGlue());
        userInfo.add(loginInfo);
        userInfo.add(Box.createRigidArea(new Dimension(8, 0)));
        userInfo.add(exitButton);
        userInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel p3extended = new JPanel();
        p3extended.setLayout(new BorderLayout());
        p3extended.add(filterPanel, BorderLayout.NORTH);
        p3extended.add(p3, BorderLayout.CENTER);

        p4extended = new JPanel();
        p4extended.setLayout(new BoxLayout(p4extended, BoxLayout.X_AXIS));
        p4extended.add(Box.createRigidArea(new Dimension(15, 0)));
        p4extended.add(sidePanel);
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

        initListeners();
    }

    private void createTable() {
        String[] columns = {
                "Id", "Author", "Name", "Creation Date", "Genre", "Rating", "Oscars", "X", "Y"
        };

        tableModel = new MyDefaultTableModel();
        tableModel.setColumnIdentifiers(columns);
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);

        // sorting table
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        Comparator<Integer> integerComparator = Comparator.comparingInt(value -> value);
        Comparator<Long> longComparator = Comparator.comparingLong(value -> value);
        Comparator<Float> doubleComparator = Comparator.comparingDouble(value -> (double) value);
        Comparator<? extends Enum<?>> enumComparator = Comparator.comparing(Enum::name);
        Comparator<LocalDate> localDateComparator = Comparator.comparing(date -> date,
                (date1, date2) -> {
                    if (date1.isBefore(date2)) {return -1;}
                    else if (date1.isEqual(date2)) {return 0;}
                    else {return 1;}
                });

        sorter.setComparator(table.getColumnModel().getColumnIndex("Id"), integerComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("X"), doubleComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Y"), longComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Genre"), enumComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Rating"), enumComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Creation Date"), localDateComparator);

        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Id")).setPreferredWidth(10);
//        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Rating")).setPreferredWidth(15);
//        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Oscars")).setPreferredWidth(15);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!clearSelection) {
                new Thread(() -> {
                    int index = table.getSelectedRow();
                    setRightPanelFields(index);
                }).start();
            }
        });
    }

    private Map<String, Object> getTableRow(int index) {
        int columnCount = table.getModel().getColumnCount();
        Map<String, Object> rowStringValues = new HashMap<>(columnCount);
        for (int i=0; i < columnCount; i++) {
            rowStringValues.put(table.getColumnName(i), table.getModel().getValueAt(index, i));
        }
        return rowStringValues;
    }

    private void setRightPanelFields(int index) {
        addButton.setVisible(false);

        Map<String, Object> rowValues = getTableRow(index);
        String author = rowValues.get("Author").toString();
        if (!author.equals(login)) {
            setEditableRightPanel(false);
        }

        idField.setText(rowValues.get("Id").toString());
        authorField.setText(author);
        nameField.setText(rowValues.get("Name").toString());
        creationDateField.setText(rowValues.get("Creation Date").toString());
        genreComboBox.setSelectedItem(rowValues.get("Genre"));
        ratingComboBox.setSelectedItem(rowValues.get("Rating"));
        oscarsComboBox.setSelectedItem(rowValues.get("Oscars"));
        xField.setText(rowValues.get("X").toString());
        yField.setText(rowValues.get("Y").toString());

        if (author.equals(login)) {
            setEditableRightPanel(true);
            statusLabel.setText(bundle.getString("statusUpdate") + " " + bundle.getString("or"));
            toAddButton.setVisible(true);
            updateButton.setVisible(true);
        } else {
            statusLabel.setText(bundle.getString("statusView") + " " + bundle.getString("or"));
            toAddButton.setVisible(true);
            updateButton.setVisible(false);
        }
    }

    private JPanel createSmallPanel(JLabel label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        field.setPreferredSize(new Dimension(190, 30));
        field.setMaximumSize(new Dimension(190, 30));
        panel.add(Box.createRigidArea(new Dimension(7, 0)));
        panel.add(label, Component.LEFT_ALIGNMENT);
        panel.add(Box.createHorizontalGlue());
        panel.add(field, Component.RIGHT_ALIGNMENT);
        return panel;
    }

    private void createPanels() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(Box.createRigidArea(new Dimension(0, 70)));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 70)));
        statusPanel.add(toAddButton);

        JPanel idPanel = createSmallPanel(idLabel, idField);
        JPanel authorPanel = createSmallPanel(authorLabel, authorField);
        JPanel namePanel = createSmallPanel(nameLabel, nameField);
        JPanel creationDatePanel = createSmallPanel(creationDateLabel, creationDateField);
        JPanel genrePanel = createSmallPanel(genreLabel, genreComboBox);
        JPanel ratingPanel = createSmallPanel(ratingLabel, ratingComboBox);
        JPanel oscarsPanel = createSmallPanel(oscarsLabel, oscarsComboBox);
        JPanel xPanel = createSmallPanel(xLabel, xField);
        JPanel yPanel = createSmallPanel(yLabel, yField);


//        JPanel p5 = new JPanel();
//        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
//        p5.add(updateButton);
//        p5.add(Box.createRigidArea(new Dimension(5, 0)));
//        p5.add(newCreatureButton);
//        p5.add(Box.createRigidArea(new Dimension(5, 0)));
//        p5.add(clearButton);
//        p5.add(Box.createRigidArea(new Dimension(5, 0)));
//        p5.add(cancelButton);
//
//        JPanel p55 = new JPanel();
//        p55.setLayout(new BoxLayout(p55, BoxLayout.X_AXIS));
//        p55.add(addButton);
//        p55.add(Box.createRigidArea(new Dimension(10, 0)));
//        p55.add(addIfMaxButton);
//        p55.add(Box.createRigidArea(new Dimension(10, 0)));
//        p55.add(removeButton);

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(350, 1000));
        sidePanel.setMaximumSize(new Dimension(350, 1000));
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(statusPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(idPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(authorPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(namePanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(creationDatePanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(genrePanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(ratingPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(oscarsPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(xPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(yPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(addButton);
        sidePanel.add(updateButton);
//        p55.setLayout(new BoxLayout(p55, BoxLayout.X_AXIS));
//        p55.add(addButton);
//        p55.add(Box.createRigidArea(new Dimension(10, 0)));
//        p55.add(addIfMaxButton);


//        rightPanel.add(p5);
//        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        rightPanel.add(p55);
//        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

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
                        nameFilter.setEnabled(true);
                        creationDateFilter.setEnabled(true);
                        genreFilter.setEnabled(true);
                        locationComboBox.setEnabled(true);
                        ratingFilter.setEnabled(true);
                        colorComboBox.setEnabled(true);
                    } else {
                        clearSelection = true;
                        table.getSelectionModel().clearSelection();
                        clearSelection = false;
                        nameFilter.setEnabled(false);
                        creationDateFilter.setEnabled(false);
                        genreFilter.setEnabled(false);
                        locationComboBox.setEnabled(false);
                        ratingFilter.setEnabled(false);
                        colorComboBox.setEnabled(false);
                    }
                }).start());

        //Panels
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));

        idFilter.setMaximumSize(new Dimension(119, 50));
        idFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(idFilter);

        authorFilter.setMaximumSize(new Dimension(119, 50));
        authorFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(authorFilter);

        nameFilter.setMaximumSize(new Dimension(119, 50));
        nameFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(nameFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        creationDateFilter.setMaximumSize(new Dimension(119, 50));
        creationDateFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(creationDateFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        genreFilter.setMaximumSize(new Dimension(119, 50));
        genreFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(genreFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        ratingFilter.setMaximumSize(new Dimension(119, 50));
        ratingFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(ratingFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        oscarsFilter.setMaximumSize(new Dimension(119, 50));
        oscarsFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(oscarsFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        xFilter.setMaximumSize(new Dimension(119, 50));
        xFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(xFilter);

        filterPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        yFilter.setMaximumSize(new Dimension(119, 50));
        yFilter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(yFilter);

        filterPanel.add(Box.createHorizontalGlue());
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel pInfoExtended = new JPanel();
        pInfoExtended.setLayout(new BorderLayout());
    }

    private void setEditableRightPanel(boolean b) {
        idField.setEditable(false);
        authorField.setEditable(false);
        nameField.setEditable(b);
        creationDateField.setEditable(false);

        genreComboBox.setEditable(false);
        genreComboBox.setEnabled(b);
        ratingComboBox.setEditable(false);
        ratingComboBox.setEnabled(b);
        oscarsComboBox.setEditable(false);
        oscarsComboBox.setEnabled(b);

        xField.setEditable(b);
        yField.setEditable(b);
    }

    private void clearRightPanel() {
        nameField.setText("");
        genreComboBox.setSelectedIndex(0);
        ratingComboBox.setSelectedIndex(0);
        oscarsComboBox.setSelectedIndex(0);
        xField.setText("");
        yField.setText("");
    }

    private void setAddModeRightPanel() {
        if (clearSelection) {
            table.getSelectionModel().clearSelection();
            clearSelection = false;
        }
        clearRightPanel();
        statusLabel.setText(bundle.getString("statusAdd"));
        toAddButton.setVisible(false);
        updateButton.setVisible(false);
        addButton.setVisible(true);
        setEditableRightPanel(true);
        idField.setText(bundle.getString("defaultAddField"));
        authorField.setText(login);
        creationDateField.setText(bundle.getString("defaultAddField"));
    }

    private void initElements() {
        DateTimeFormatter displayDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(
                bundle.getMyLocale().getLocale());

        setAddModeRightPanel();

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

        genreFilter.setToolTipText(bundle.getString("format") + ": dd.MM.yy HH:mm:ss");

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

        JComboBox<String> locationBox = new JComboBox<>(locationsArray1);
        locationBox.setEditable(false);
        locationBox.setEnabled(false);
        locationComboBox = new JComboBox<>(locationsArray);
        colorComboBox = new JComboBox<>(colorsArray);

        nameFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });
//        nameTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        familyTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        hungerTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        timeTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        xTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        yTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
//        sizeTo.getDocument().addDocumentListener((FiltersListener) this::checkFilters);
        //final ActionListener actionListener = e -> new Thread(this::checkFilters).start();
        //colorComboBox.addActionListener(actionListener);
        //locationComboBox.addActionListener(actionListener);

        createTable();
    }

    private void applyFilters() {
        PriorityQueue<Movie> filteredList = new PriorityQueue<>();
        getCollection().stream()
                .filter(movie -> movie.getName().startsWith(nameFilter.getText()))
                .forEach(filteredList::add);

        refreshTable(filteredList);
    }
}