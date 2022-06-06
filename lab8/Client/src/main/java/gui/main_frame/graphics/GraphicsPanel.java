package gui.main_frame.graphics;

import data.Movie;
import gui.main_frame.Mediator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphicsPanel extends JPanel {
    private final Mediator mediator;

    private Movie selectedMovie;
    private List<Timer> timers;

    public GraphicsPanel(Mediator mediator) {
        this.mediator = mediator;
    }

    public Map<String, Object> getSelectedGraphics() {
        Map<String, Object> selectedGraphics = new HashMap<>();
        selectedGraphics.put("Author", selectedMovie.getUsername());
        selectedGraphics.put("Id", selectedMovie.getId());
        selectedGraphics.put("Name", selectedMovie.getName());
        selectedGraphics.put("Creation Date", selectedMovie.getCreationDate());
        selectedGraphics.put("Genre", selectedMovie.getMovieGenre());
        selectedGraphics.put("Rating", selectedMovie.getMpaaRating());
        selectedGraphics.put("Oscars", selectedMovie.getOscarsCount());
        selectedGraphics.put("X", selectedMovie.getCoordinates().getX());
        selectedGraphics.put("Y", selectedMovie.getCoordinates().getY());
        return selectedGraphics;
    }

    public void refresh(@NotNull PriorityQueue<Movie> pq) {
        Dimension graphicsPanelSize = new Dimension(getVisibleRect().width, getVisibleRect().height);

        if (timers != null) {
            for (Timer timer: timers)
                timer.stop();
            timers.clear();
        }
        removeAll();
        repaint();
        revalidate();

        timers = new ArrayList<>(pq.size());
        pq.forEach(
                movie -> {
                    MovieGraphics mg = new MovieGraphics(movie, graphicsPanelSize);
                    add(mg);

                    mg.addActionListener(event -> {
                        selectedMovie = movie;
                        mediator.notify(this, "graphicsSelected");
                    });

                    Timer timer = new Timer(40, e -> {
                        mg.transitionStep();
                        repaint();
                        revalidate();
                    });
                    timers.add(timer);
                    timer.start();
                }
        );
    }
}
