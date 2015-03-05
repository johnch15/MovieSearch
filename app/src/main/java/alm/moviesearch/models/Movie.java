package alm.moviesearch.models;

/**
 * Basic data model for a movie
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class Movie {
    private final String mTitle;
    private final String mYear;
    private final String mImdbId;
    private final String mType;

    public static final String TITLE = "Title";
    public static final String YEAR = "Year";
    public static final String IMDBID = "imdbID";
    public static final String TYPE = "Type";

    public Movie(String title, String year, String imdbId, String type) {
        mTitle = title;
        mYear = year;
        mImdbId = imdbId;
        mType = type;
    }

    public String getYear() {
        return mYear;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public String getType() {
        return mType;
    }

    public String getTitle() {

        return mTitle;
    }
}
