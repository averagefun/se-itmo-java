package gui.main_frame;

import commands.CommandManager;
import data.MovieGenre;
import data.MpaaRating;
import gui.addition.MyLayout;
import gui.addition.MyStyle;
import gui.addition.IndentedRenderer;
import network.CommandResponse;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

import static gui.AbstractFrame.bundle;

public class SidePanel extends JPanel {
    private final Refreshable toRefresh;
    private final String username;
    private final CommandManager cm;

    // labels
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
    private final JPanel updateButtonPanel = new JPanel();
    private final JLabel printLabel = new JLabel();

    // fields
    private final JTextField idField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField creationDateField = new JTextField();
    private final JComboBox<MovieGenre> genreComboBox = new JComboBox<>(MovieGenre.values());
    private final JComboBox<MpaaRating> ratingComboBox = new JComboBox<>(MpaaRating.values());
    private final JComboBox<Integer> oscarsComboBox = new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11});
    private final JTextField xField = new JTextField();
    private final JTextField yField = new JTextField();

    // Buttons
    private final JButton addButton = new JButton(bundle.getString("addButton"));
    private final JButton toAddButton = new JButton(bundle.getString("toAddButton"));
    private final JButton updateButton = new JButton(bundle.getString("updateButton"));

    public SidePanel(String username, CommandManager cm, Refreshable toRefresh) {
        this.username = username;
        this.cm = cm;
        this.toRefresh = toRefresh;
        makeLayout();
        addListeners();
        setAddMode();
    }

    private void initElements() {

        // set font
        Font font = new Font("Arial", Font.PLAIN, 14);
        Font fontFields = new Font("Arial", Font.PLAIN, 15);
        MyStyle.setMultiplyFont(font, idLabel, authorLabel, nameLabel,
                creationDateLabel, genreLabel, genreComboBox, ratingLabel, ratingComboBox,
                oscarsLabel, oscarsComboBox, xLabel, yLabel);
        MyStyle.setMultiplyFont(fontFields, idField, authorField, nameField, creationDateField, xField, yField);

        // add paddings to all text fields
        MyStyle.setPaddings(5, idField, authorField, nameField, creationDateField, xField, yField);

        // add padding to combo boxes
        IndentedRenderer indentedRenderer = new IndentedRenderer();
        genreComboBox.setRenderer(indentedRenderer);
        ratingComboBox.setRenderer(indentedRenderer);
        oscarsComboBox.setRenderer(indentedRenderer);

        // set white back to combo boxes
        genreComboBox.setBackground(Color.WHITE);
        ratingComboBox.setBackground(Color.WHITE);
        oscarsComboBox.setBackground(Color.WHITE);

        Font fontTitle = new Font("Arial", Font.BOLD, 18);
        statusLabel.setFont(fontTitle);

        Font buttonFont = new Font("Arial", Font.BOLD, 15);
        addButton.setFont(buttonFont);
        toAddButton.setFont(buttonFont);
        updateButton.setFont(buttonFont);

        // font for print label
        Font printFont = new Font("Arial", Font.BOLD, 16);
        printLabel.setFont(printFont);
    }

    private JPanel createLabelFieldPanel(JLabel label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        field.setPreferredSize(new Dimension(190, 30));
        field.setMaximumSize(new Dimension(190, 30));
        panel.add(MyLayout.hspace(7));
        panel.add(label, Component.LEFT_ALIGNMENT);
        panel.add(Box.createHorizontalGlue());
        panel.add(field, Component.RIGHT_ALIGNMENT);
        return panel;
    }

    private void makeLayout() {
        initElements();

        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel idPanel = createLabelFieldPanel(idLabel, idField);
        JPanel authorPanel = createLabelFieldPanel(authorLabel, authorField);
        JPanel namePanel = createLabelFieldPanel(nameLabel, nameField);
        JPanel creationDatePanel = createLabelFieldPanel(creationDateLabel, creationDateField);
        JPanel genrePanel = createLabelFieldPanel(genreLabel, genreComboBox);
        JPanel ratingPanel = createLabelFieldPanel(ratingLabel, ratingComboBox);
        JPanel oscarsPanel = createLabelFieldPanel(oscarsLabel, oscarsComboBox);
        JPanel xPanel = createLabelFieldPanel(xLabel, xField);
        JPanel yPanel = createLabelFieldPanel(yLabel, yField);

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        updateButtonPanel.setLayout(new BoxLayout(updateButtonPanel, BoxLayout.X_AXIS));
        updateButtonPanel.add(updateButton);
        updateButtonPanel.add(MyLayout.hspace(20));
        updateButtonPanel.add(toAddButton);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 5));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        separator.setForeground(Color.BLACK);

        printLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(350, 1000));
        setMaximumSize(new Dimension(350, 1000));

        add(MyLayout.vspace(20));
        add(statusLabel);
        add(MyLayout.vspace(15));
        add(idPanel);
        add(MyLayout.vspace(10));
        add(authorPanel);
        add(MyLayout.vspace(10));
        add(namePanel);
        add(MyLayout.vspace(5));
        add(creationDatePanel);
        add(MyLayout.vspace(5));
        add(genrePanel);
        add(MyLayout.vspace(5));
        add(ratingPanel);
        add(MyLayout.vspace(5));
        add(oscarsPanel);
        add(MyLayout.vspace(5));
        add(xPanel);
        add(MyLayout.vspace(5));
        add(yPanel);
        add(MyLayout.vspace(10));
        add(addButton);
        add(updateButtonPanel);
        add(MyLayout.vspace(12));
        add(separator);
        add(MyLayout.vspace(15));
        add(printLabel);
    }

    private void updateServerCollectionByFieldsValues(Supplier<CommandResponse> supplier) {
        Queue<String> input = new ArrayDeque<>();
        input.add(nameField.getText());
        input.add(xField.getText());
        input.add(yField.getText());
        input.add(Objects.requireNonNull(oscarsComboBox.getSelectedItem()).toString());
        input.add(Objects.requireNonNull(genreComboBox.getSelectedItem()).toString());
        input.add(Objects.requireNonNull(ratingComboBox.getSelectedItem()).toString());
        input.add("Director Name"); // Name
        input.add("1"); // Weight
        input.add("WHITE"); // Hair Color
        input.add("1"); // Location X
        input.add("1"); // Location Y
        input.add("Location Name"); // Location name

        cm.setInputValues(input);
        CommandResponse cRes = supplier.get();
        if (cRes.getExitCode() == 0) {
            toRefresh.refresh(cm.getServerCollection());
            printToLabel(cRes.getMessage(), true);
            clearFields();
        } else {
            String message = cRes.getMessage();
            if (message.contains("::")) {
                String[] splitMessage = message.split("::");
                String fieldName = bundle.getString(splitMessage[0]).isEmpty() ? splitMessage[0] : bundle.getString(splitMessage[0]);
                String errorText = bundle.getString(splitMessage[1]).isEmpty() ? splitMessage[1] : bundle.getString(splitMessage[1]);
                message = fieldName + ": " + errorText.substring(0,1).toLowerCase() + errorText.substring(1);
            }
            printToLabel(message, false);
        }
    }

    protected void clearPrintLabel() {
        printLabel.setText("");
    }

    private void printToLabel(String message, boolean isSuccess) {
        if (isSuccess) {
            printLabel.setForeground(Color.GREEN);
        } else {
            printLabel.setForeground(Color.RED);
        }

        String bundleMessage = bundle.getString(message).isEmpty() ? message : bundle.getString(message);
        String htmlWrapLines = "<html><body style='width: %1spx'>%1s";
        printLabel.setText(String.format(htmlWrapLines, 250, bundleMessage));
    }

    private void addListeners() {
        addButton.addActionListener(event -> new Thread(() -> {
            addButton.setEnabled(false);
            updateServerCollectionByFieldsValues(() -> cm.runCommand("add"));
            addButton.setEnabled(true);
        }).start());

        toAddButton.addActionListener(event -> new Thread(this::setAddMode).start());

        updateButton.addActionListener(event -> new Thread(() -> {
            updateButton.setEnabled(false);
            updateServerCollectionByFieldsValues(() -> cm.runCommand("update", idField.getText()));
            updateButton.setEnabled(true);
        }).start());
    }

    protected void setFields(Map<String, Object> rowValues) {
        addButton.setVisible(false);

        String author = rowValues.get("Author").toString();
        if (!author.equals(username)) {
            setEditableFields(false);
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

        updateButtonPanel.setVisible(true);
        toAddButton.setVisible(true);
        if (author.equals(username)) {
            setEditableFields(true);
            statusLabel.setText(bundle.getString("statusUpdate"));
            updateButton.setVisible(true);
        } else {
            statusLabel.setText(bundle.getString("statusView"));
            updateButton.setVisible(false);
        }
    }

    private void setEditableFields(boolean b) {
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

    private void clearFields() {
        idField.setText("");
        authorField.setText("");
        nameField.setText("");
        creationDateField.setText("");
        genreComboBox.setSelectedIndex(0);
        ratingComboBox.setSelectedIndex(0);
        oscarsComboBox.setSelectedIndex(0);
        xField.setText("");
        yField.setText("");
    }

    private void setAddMode() {
        // todo row selection clear

        clearFields();
        statusLabel.setText(bundle.getString("statusAdd"));
        toAddButton.setVisible(false);
        updateButtonPanel.setVisible(false);
        addButton.setVisible(true);
        setEditableFields(true);
        authorField.setText(username);
    }
}
