package alm.moviesearch;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import alm.moviesearch.fragments.MovieDetailFragment;
import alm.moviesearch.fragments.MovieListFragment;
import alm.moviesearch.models.Movie;


/**
 * An activity representing a list of searched Movies and the detail of a selected movie.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link alm.moviesearch.fragments.MovieListFragment} and the item details
 * (if present) is a {@link alm.moviesearch.fragments.MovieDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link alm.moviesearch.fragments.MovieListFragment.Callbacks} interface
 * to listen for item selections.
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieActivity extends FragmentActivity
        implements MovieListFragment.Callbacks {

    private static final String TAG = "MovieActivity";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }
        setContentView(R.layout.activity_movie);
    }

    /**
     * Callback method from {@link MovieListFragment.Callbacks}
     * indicating that the given item was selected.
     */
    @Override
    public void onItemSelected(Object item) {
        // Show the detail view in this activity by adding or replacing the detail
        // fragment using a fragment transaction.
        String id = null;
        if (item instanceof Movie) {
            id = ((Movie) item).getImdbId();
        } else {
            Log.e(TAG, "Not a movie");
            return;
        }
        final Bundle arguments = new Bundle();
        arguments.putString(Movie.IMDBID, id);

        final MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();
    }
}
