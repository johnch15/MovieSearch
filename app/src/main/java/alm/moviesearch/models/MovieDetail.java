package alm.moviesearch.models;

import java.net.URL;

/**
 * Basic data model for a movie. Only retrieve the data that we need
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieDetail extends Movie {
    private final URL mPoster;
    private final String mGenre;
    private final String mReleased;
    private final String mPlot;

    public static final String POSTER = "Poster";
    public static final String GENRE = "Genre";
    public static final String RELEASED = "Released";
    public static final String PLOT = "Plot";

    public MovieDetail(String title, String year, String imdbId, String type, URL poster,
                       String genre, String released, String plot) {
        super(title, year, imdbId, type);
        mPoster = poster;
        mGenre = genre;
        mReleased = released;
        mPlot = plot;
    }

    public URL getPoster() {
        return mPoster;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getReleased() {
        return mReleased;
    }

    public String getPlot() {
        return mPlot;
    }
}
