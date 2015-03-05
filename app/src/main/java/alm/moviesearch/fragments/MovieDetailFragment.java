package alm.moviesearch.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import alm.moviesearch.R;
import alm.moviesearch.models.Movie;
import alm.moviesearch.models.MovieDetail;
import alm.moviesearch.models.MovieDetailFactory;
import alm.moviesearch.network.HttpConnection;
import alm.moviesearch.util.ImageUtil;

/**
 * A fragment representing a single Movie detail screen.
 * <p/>
 * Created by zhongchen on 15-03-03.
 */
public class MovieDetailFragment extends Fragment {

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mGenreReleased;
    private TextView mPlot;
    private TextView mError;
    private ProgressBar mProgress;
    private static String mImdbId;

    private static final String TAG = "MovieDetailFragment";
    private static final boolean DEBUG = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImdbId = null; //reset mImdbId
        final Bundle args = getArguments();
        if (args.containsKey(Movie.IMDBID)) {
            mImdbId = args.getString(Movie.IMDBID);
        }
        if (DEBUG) {
            Log.d(TAG, "onCreate: mImdbId = " + mImdbId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mPoster = (ImageView) rootView.findViewById(R.id.poster);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mGenreReleased = (TextView) rootView.findViewById(R.id.genre_released);
        mPlot = (TextView) rootView.findViewById(R.id.plot);
        mError = (TextView) rootView.findViewById(R.id.error);
        mProgress = (ProgressBar) rootView.findViewById(R.id.progress);

        if (TextUtils.isEmpty(mImdbId)) {
            mTitle.setText("Invalid data");
        } else {
            // Retrieve movie detail from OMDb API
            new RetrieveDetail(mPoster, mTitle, mGenreReleased, mPlot, mError, mProgress, mImdbId)
                    .execute(createDetailUrl(mImdbId), null);
        }
        return rootView;
    }

    private URL createDetailUrl(String imdbId) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.omdbapi.com")
                .appendQueryParameter("i", imdbId)
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
     * Retrieve the detail info via OMDb API in the background
     */
    private static class RetrieveDetail extends AsyncTask<URL, Void, MovieDetail> {
        /* Use WeakReferences to ensure the ui widgets can be garbage collected */
        private final WeakReference<ImageView> mImageRef;
        private final WeakReference<TextView> mTitleRef;
        private final WeakReference<TextView> mGenreReleasedRef;
        private final WeakReference<TextView> mPlotRef;
        private final WeakReference<TextView> mErrorRef;
        private final WeakReference<ProgressBar> mProgressRef;
        private final String mId;

        public RetrieveDetail(ImageView imageView, TextView title, TextView genreReleased,
                              TextView plot, TextView error, ProgressBar progress, String id) {
            mImageRef = new WeakReference<ImageView>(imageView);
            mTitleRef = new WeakReference<TextView>(title);
            mGenreReleasedRef = new WeakReference<TextView>(genreReleased);
            mPlotRef = new WeakReference<TextView>(plot);
            mErrorRef = new WeakReference<TextView>(error);
            mProgressRef = new WeakReference<ProgressBar>(progress);
            mId = id;
        }

        @Override
        protected MovieDetail doInBackground(URL... params) {
            if (params[0] == null) {
                return null;
            }
            // retrieve movie detail
            JSONObject json = null;
            try {
                json = new JSONObject(HttpConnection.connect(params[0]).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return MovieDetailFactory.fromJSONObject(json);
        }

        @Override
        protected void onPostExecute(MovieDetail movieDetail) {
            if (mId == null || !mId.equals(mImdbId)) {
                return;
            }
            final TextView title = mTitleRef.get();
            final TextView genreReleased = mGenreReleasedRef.get();
            final TextView plot = mPlotRef.get();

            // Update ui
            if (title != null) {
                title.setText(movieDetail.getTitle());
            }
            if (genreReleased != null) {
                final StringBuilder str = new StringBuilder();
                str.append("(");
                str.append(movieDetail.getGenre());
                str.append(", ");
                str.append(movieDetail.getReleased());
                str.append(")");
                genreReleased.setText(str.toString());
            }
            if (plot != null) {
                plot.setText(movieDetail.getPlot());
            }

            // Retrieve the poster
            final ImageView image = mImageRef.get();
            final ProgressBar progress = mProgressRef.get();
            final TextView error = mErrorRef.get();
            if (image != null) {
                final URL url = movieDetail.getPoster();
                if (url != null) {
                    new ImageUtil(image, mErrorRef.get(), mProgressRef.get()).execute(url);
                } else {
                    if (progress != null) {
                        progress.setVisibility(View.GONE);
                    }
                    if (error != null) {
                        error.setText("No poster");
                    }

                }
            }
        }
    }

}
