package gui.main_frame;

import data.Movie;
import data.MovieGenre;
import data.MpaaRating;
import gui.addition.FilterListener;
import localization.MyBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FilterPanel extends JPanel implements Refreshable {
    private final MyBundle bundle = MyBundle.getBundle("gui");
    private final Supplier<PriorityQueue<Movie>> collectionSupplier;
    private final Refreshable toRefresh;

    private final JTextField idFilter = new JTextField();
    private final JTextField authorFilter = new JTextField();
    private final JTextField nameFilter = new JTextField();
    private final JTextField creationDateFilter = new JTextField();
    private final JComboBox<String> genreFilter = new JComboBox<>(
            MovieGenre.getStringValues(bundle.getString("any")));
    private final JComboBox<String> ratingFilter = new JComboBox<>(
            MpaaRating.getStringValues(bundle.getString("any")));
    private final JComboBox<String> oscarsFilter = new JComboBox<>(
            new String[]{bundle.getString("any"), "0","1","2","3","4","5","6","7","8","9","10","11"});

    private final JButton dropFiltersButton = new JButton(bundle.getString("dropFiltersButton"));

    private void addTextFilter(JTextField filter) {
        filter.setMinimumSize(new Dimension(100, 30));
        filter.setPreferredSize(new Dimension(127, 30));
        filter.setMaximumSize(new Dimension(150, 50));
        add(filter);
        filter.getDocument().addDocumentListener(new FilterListener(this::refreshFromServer));
    }

    private <T> void addComboFilter(JComboBox<T> filter) {
        filter.setMinimumSize(new Dimension(100, 30));
        filter.setPreferredSize(new Dimension(127, 30));
        filter.setMaximumSize(new Dimension(150, 50));
        add(filter);
        filter.addActionListener(event -> this.refreshFromServer());
    }

    private void makeLayout() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        addTextFilter(idFilter);
        addTextFilter(authorFilter);
        addTextFilter(nameFilter);
        addTextFilter(creationDateFilter);
        addComboFilter(genreFilter);
        addComboFilter(ratingFilter);
        addComboFilter(oscarsFilter);

        dropFiltersButton.setMinimumSize(new Dimension(202, 30));
        dropFiltersButton.setPreferredSize(new Dimension(256, 30));
        dropFiltersButton.setMaximumSize(new Dimension(302, 50));
        add(dropFiltersButton);
    }

    public FilterPanel(Supplier<PriorityQueue<Movie>> collectionSupplier, Refreshable toRefresh) {
        this.collectionSupplier = collectionSupplier;
        this.toRefresh = toRefresh;
        makeLayout();
        addListeners();
        createRefresher();
    }

    private void addListeners() {
        dropFiltersButton.addActionListener(event -> new Thread(() -> {
            idFilter.setText("");
            authorFilter.setText("");
            nameFilter.setText("");
            creationDateFilter.setText("");
            genreFilter.setSelectedIndex(0);
            ratingFilter.setSelectedIndex(0);
            oscarsFilter.setSelectedIndex(0);
        }).start());

        // integer value validator
        idFilter.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || (c == KeyEvent.VK_BACK_SPACE))){
                    e.consume();  // if it's not a digit, ignore the event
                }
            }
        });

        creationDateFilter.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || (c == '-') || (c == KeyEvent.VK_BACK_SPACE))){
                    e.consume();  // if it's not a digit or '-', ignore the event
                }
            }
        });
    }

    public void createRefresher() {
        Thread refresher = new Thread(() -> {
            while(true) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                System.out.println("updating");
                refreshFromServer();
            }
        });
        refresher.setDaemon(true);
        refresher.start();
    }

    private void refreshFromServer() {
        refresh(collectionSupplier.get());
    }

    public void refresh(PriorityQueue<Movie> pq) {
        PriorityQueue<Movie> filteredQueue = pq.stream()
                .filter(movie -> idFilter.getText().isEmpty() || Integer.toString(movie.getId()).equals(idFilter.getText()))
                .filter(movie -> authorFilter.getText().isEmpty() || movie.getUsername().startsWith(authorFilter.getText()))
                .filter(movie -> nameFilter.getText().isEmpty() || movie.getName().startsWith(nameFilter.getText()))
                .filter(movie -> creationDateFilter.getText().isEmpty() || movie.getCreationDate().toString().equals(creationDateFilter.getText()))
                .filter(movie -> genreFilter.getSelectedItem() == bundle.getString("any") ||
                        movie.getMovieGenre().toString().equalsIgnoreCase(Objects.requireNonNull(genreFilter.getSelectedItem()).toString()))
                .filter(movie -> ratingFilter.getSelectedItem() == bundle.getString("any") ||
                        movie.getMpaaRating().toString().equalsIgnoreCase(Objects.requireNonNull(ratingFilter.getSelectedItem()).toString()))
                .filter(movie -> oscarsFilter.getSelectedItem() ==  bundle.getString("any") ||
                        Integer.toString(movie.getOscarsCount()).equalsIgnoreCase(Objects.requireNonNull(oscarsFilter.getSelectedItem()).toString()))
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        toRefresh.refresh(filteredQueue);
    }
}
