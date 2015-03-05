package alm.moviesearch.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import alm.moviesearch.Adapter.MovieListAdapter;
import alm.moviesearch.R;
import alm.moviesearch.models.Movie;
import alm.moviesearch.models.MovieFactory;
import alm.moviesearch.network.HttpConnection;

/**
 * A fragment representing a search view and a list of Movies.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieListFragment extends Fragment implements SearchView.OnQueryTextListener {

    /**
     * The listview that displays the search results.
     */
    private ListView mListView;

    /**
     * The listview adapter that manages the search results.
     */
    private MovieListAdapter mListAdapter;

    /**
     * The searchview that displays the search input.
     */
    private SearchView mSearchView;

    /**
     * The current search text;
     */
    private static String mSearchText;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = "MovieListFragment";
    private static final boolean DEBUG = true;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Object item);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Object item) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_movie_list, null);
        mSearchView = (SearchView) v.findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(this);
        mListView = (ListView) v.findViewById(R.id.movie_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListAdapter = new MovieListAdapter(this.getActivity());
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mSearchText = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent instanceof ListView) {
                final Object clickedMovie = ((ListView) parent).getAdapter().getItem(position);
                mCallbacks.onItemSelected(clickedMovie);

            }
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (DEBUG) {
            Log.d(TAG, "onQueryTextSubmit: " + query);
        }
        if (query == null || query.length() <= 1) {
            Toast.makeText(this.getActivity(), "Must provide more than one character.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (DEBUG) {
            Log.d(TAG, "onQueryTextChange: " + newText);
        }
        mSearchText = newText;
        mSearchText.trim();
        new SearchMovies(mListView, newText).execute(createSearchUrl(newText), null);
        return true;
    }

    private URL createSearchUrl(String searchText) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.omdbapi.com")
                .appendQueryParameter("s", searchText)
                .appendQueryParameter("y", "")
                .appendQueryParameter("plot", "short")
                .appendQueryParameter("r", "json");
        URL url = null;
        try {
            url = new URL(builder.build().toString());
            if (DEBUG) {
                Log.d(TAG, "url: " + url.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Search movies with given url via OMDb API in the background
     */
    private static class SearchMovies extends AsyncTask<URL, Void, List<Movie>> {
        /* Use WeakReferences to ensure the listview can be garbage collected */
        private final WeakReference<ListView> mListView;
        private final String mText;

        public SearchMovies(ListView listView, String searchText) {
            mListView = new WeakReference<ListView>(listView);
            mText = searchText;
        }

        @Override
        protected List<Movie> doInBackground(URL... params) {
            if (params[0] == null) {
                return null;
            }
            // search movies
            JSONObject json = null;
            try {
                json = new JSONObject(HttpConnection.connect(params[0]).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return MovieFactory.fromJSONObject(json);
        }

        @Override
        protected void onPostExecute(List<Movie> list) {
            final ListView listView = mListView.get();
            if (listView != null && mText.equals(mSearchText)) {
                // Refresh the list view with new data
                ((MovieListAdapter) listView.getAdapter()).updateData(list);
            }
        }
    }

}
