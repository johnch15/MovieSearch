package alm.moviesearch.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Create an object of Movie by retrieving data from an JSONObject.
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieFactory {
    private static final String SEARCH = "Search";
    private static final String TAG = "MovieFactory";

    public static List<Movie> fromJSONObject(JSONObject json) {
        if (json == null) return null;
        final JSONArray jArray = json.optJSONArray(SEARCH);
        final List<Movie> list = new ArrayList<>();
        if (jArray != null) {
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    list.add(getMovie(new JSONObject(jArray.get(i).toString())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private static Movie getMovie(JSONObject json) {
        try {
            return new Movie(
                    json.getString(Movie.TITLE),
                    json.getString(Movie.YEAR),
                    json.getString(Movie.IMDBID),
                    json.getString(Movie.TYPE));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse error: ", e);
        }
        return null;
    }
}
