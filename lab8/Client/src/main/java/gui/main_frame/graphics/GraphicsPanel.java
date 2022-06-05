package gui.main_frame.graphics;

import data.Movie;
import gui.main_frame.Mediator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphicsPanel extends JPanel {
    private ExecutorService graphicsPool;
    private final Mediator mediator;

    private volatile Movie selectedMovie;

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

        if (graphicsPool != null && !graphicsPool.isShutdown()) {
            graphicsPool.shutdown();
        }
        graphicsPool = Executors.newFixedThreadPool(100);

        removeAll();
        repaint();
        revalidate();
        pq.forEach(
                movie -> graphicsPool.execute(() -> {
                    MovieGraphics mg = new MovieGraphics(movie, graphicsPanelSize);
                    add(mg);

                    mg.addActionListener(event -> {
                        selectedMovie = movie;
                        new Thread(() -> mediator.notify(this, "graphicsSelected")).start();
                    });

                    while (true) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(40); // 30-35 FPS
                        } catch (InterruptedException e) {
                            break;
                        }
                        mg.transitionStep();
                        repaint();
                        revalidate();
                    }
                })
        );
    }
}