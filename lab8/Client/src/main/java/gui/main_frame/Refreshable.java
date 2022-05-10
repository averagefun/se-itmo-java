package gui.main_frame;

import data.Movie;
import java.util.PriorityQueue;

public interface Refreshable {
    void refresh (PriorityQueue<Movie> pq);
}
