package console;

import data.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.PriorityQueue;

/**
 * Class work with json files, convert from json and convert to json
 */
public class JsonParser {
    private final Gson gson;

    public JsonParser() {
        this.gson = new Gson();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public PriorityQueue<Movie> jsonToCollection(String text) throws JsonSyntaxException {
        return gson.fromJson(text, new TypeToken<PriorityQueue<Movie>>(){}.getType());
    }
}
