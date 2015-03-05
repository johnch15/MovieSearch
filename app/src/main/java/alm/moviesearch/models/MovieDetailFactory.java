package alm.moviesearch.models;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Create an object of MovieDetail by retrieving data from an JSONObject.
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieDetailFactory {
    private static final String TAG = "MovieDetailFactory";

    public static MovieDetail fromJSONObject(JSONObject json) {
        if (json == null) return null;
        try {
            final String posterUrlString = json.getString(MovieDetail.POSTER);
            URL posterUrl = null;
            if (!TextUtils.isEmpty(posterUrlString)) {
                try {
                    posterUrl = new URL(posterUrlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            return new MovieDetail(
                    json.getString(Movie.TITLE),
                    json.getString(Movie.YEAR),
                    json.getString(Movie.IMDBID),
                    json.getString(Movie.TYPE),
                    posterUrl,
                    json.getString(MovieDetail.GENRE),
                    json.getString(MovieDetail.RELEASED),
                    json.getString(MovieDetail.PLOT));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse error: ", e);
        }
        return null;
    }
}
