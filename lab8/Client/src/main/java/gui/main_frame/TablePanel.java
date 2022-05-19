package gui.main_frame;

import data.Movie;
import gui.Localisable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;

public class TablePanel extends JPanel implements Localisable {
    private JTable table;
    private DefaultTableModel tableModel;

    private final Mediator mediator;

    public JTable getTable() {
        return table;
    }

    private void initElements() {
        Font tableFont = new Font("Arial", Font.PLAIN, 14);
        table.setFont(tableFont);
        table.setRowHeight(20);
        addSorter();
    }

    private void makeLayout() {
        String[] columns = {
                "Id", "Author", "Name", "Creation Date", "Genre", "Rating", "Oscars", "X", "Y"
        };

        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 5252895839257527196L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columns);
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setMinWidth(100);
            table.getColumnModel().getColumn(i).setPreferredWidth(127);
            table.getColumnModel().getColumn(i).setMaxWidth(150);
        }
    }

    private void addSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        Comparator<String> stringComparator = Comparator.comparing(value -> value);
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
        sorter.setComparator(table.getColumnModel().getColumnIndex("Author"), stringComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Name"), stringComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Creation Date"), localDateComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Genre"), enumComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Rating"), enumComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Oscars"), integerComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("X"), doubleComparator);
        sorter.setComparator(table.getColumnModel().getColumnIndex("Y"), longComparator);

        table.setRowSorter(sorter);

        sorter.addRowSorterListener(event -> clearSelection());
    }

    private void addListeners() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!table.getSelectionModel().isSelectionEmpty()) {
                new Thread(() -> mediator.notify(this, "rowSelected")).start();
            }
        });
    }

    public TablePanel(Mediator mediator) {
        this.mediator = mediator;
        makeLayout();
        initElements();
        addListeners();
    }

    private void addRowToTable(Movie m) {
        tableModel.addRow(new Object[] {
                m.getId(), m.getUsername(), m.getName(), m.getCreationDate(), m.getMovieGenre(),
                m.getMpaaRating(), m.getOscarsCount(), m.getCoordinates().getX(), m.getCoordinates().getY()
        });
    }

    protected Map<String, Object> getSelectedRow() {
        int newIndex = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
        int columnCount = table.getModel().getColumnCount();
        Map<String, Object> rowStringValues = new HashMap<>(columnCount);
        for (int i=0; i < columnCount; i++) {

            rowStringValues.put(table.getColumnName(i), table.getModel().getValueAt(newIndex, i));
        }
        return rowStringValues;
    }

    public void refresh(@NotNull PriorityQueue<Movie> pq) {
        tableModel.setRowCount(0);
        while (!pq.isEmpty())
            addRowToTable(pq.poll());
        tableModel.fireTableDataChanged();
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public void updateLabels() {

    }
}
