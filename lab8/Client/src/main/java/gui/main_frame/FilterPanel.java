package gui.main_frame;

import commands.CommandManager;
import data.Movie;
import data.MovieGenre;
import data.MpaaRating;

import javax.swing.*;
import java.awt.*;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class FilterPanel extends JPanel {
    private final CommandManager cm;
    private final Refreshable toRefresh;

    private final JPanel filterPanel = new JPanel();
    private final JTextField idFilter = new JTextField();
    private final JTextField authorFilter = new JTextField();
    private final JTextField nameFilter = new JTextField();
    private final JTextField creationDateFilter = new JTextField();
    private final JComboBox<MovieGenre> genreFilter = new JComboBox<>(MovieGenre.values());
    private final JComboBox<MpaaRating> ratingFilter = new JComboBox<>(MpaaRating.values());
    private final JComboBox<Integer> oscarsFilter = new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11});
    private final JTextField xFilter = new JTextField();
    private final JTextField yFilter = new JTextField();

    private void addFilter(JComponent filter) {
        filter.setMaximumSize(new Dimension(119, 50));
        filter.setPreferredSize(new Dimension(119, 30));
        filterPanel.add(filter);
    }

    private void makeLayout() {
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        addFilter(idFilter);
        addFilter(authorFilter);
        addFilter(nameFilter);
        addFilter(creationDateFilter);
        addFilter(genreFilter);
        addFilter(ratingFilter);
        addFilter(oscarsFilter);
        addFilter(xFilter);
        addFilter(yFilter);
        filterPanel.add(Box.createHorizontalGlue());
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
    }

    public FilterPanel(CommandManager cm, Refreshable toRefresh) {
        this.cm = cm;
        this.toRefresh = toRefresh;
        makeLayout();
    }

    private void applyFilters() {
        PriorityQueue<Movie> filteredList = cm.getServerCollection().stream()
                .filter(movie -> idFilter.getText().isEmpty() || Integer.toString(movie.getId()).equals(idFilter.getText()))
                .filter(movie -> authorFilter.getText().isEmpty() || movie.getUsername().startsWith(authorFilter.getText()))
                .filter(movie -> nameFilter.getText().isEmpty() || movie.getName().startsWith(nameFilter.getText()))
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        toRefresh.refresh(filteredList);
    }
}
