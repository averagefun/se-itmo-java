package console;

import collection.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.PriorityQueue;

public class JsonParser {
    private final Gson gson;

    public JsonParser() {
        this.gson = new Gson();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public PriorityQueue<Movie> jsonToCollection(String text) {
        return gson.fromJson(text, new TypeToken<PriorityQueue<Movie>>(){}.getType());
    }
}
