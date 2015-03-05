package alm.moviesearch.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Retrieve an image (movie poster0 with a given url via OMDb API in the background
 * <p/>
 * Created by zhongchen on 15-03-04.
 */

public class ImageUtil extends AsyncTask<URL, Void, Bitmap> {
    private final WeakReference<ImageView> mImageViewRef;
    private final WeakReference<TextView> mErrorRef;
    private final WeakReference<ProgressBar> mProgressRef;

    private static final String TAG = "ImageUtil";
    private static final boolean DEBUG = true;

    public ImageUtil(ImageView imageView, TextView error, ProgressBar progress) {
        // Use WeakReferences to ensure the ui widgets can be garbage collected
        mImageViewRef = new WeakReference<ImageView>(imageView);
        mErrorRef = new WeakReference<TextView>(error);
        mProgressRef = new WeakReference<ProgressBar>(progress);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(URL... params) {
        if (DEBUG) {
            Log.d(TAG, "Poster url: " + params[0]);
        }
        InputStream is = null;
        try {
            is = (InputStream) params[0].getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is == null) return null;
        final BufferedInputStream buffer = new BufferedInputStream(is);

        return decodeSampledBitmapFromStream(buffer, params[0], 800, 800);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (DEBUG) {
            Log.d(TAG, "onPostExecute: ");
        }
        // Update ui
        final ProgressBar progress = mProgressRef.get();
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
        final TextView error = mErrorRef.get();
        if (bitmap == null) {
            if (error != null) {
                error.setText("Invalid poster");
            }
        }
        if (mImageViewRef != null && bitmap != null) {
            final ImageView imageView = mImageViewRef.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            if (error != null) {
                error.setVisibility(View.GONE);
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream is, URL url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        try {
            is = new BufferedInputStream((InputStream) url.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        if (DEBUG) {
            Log.d(TAG, "inSampleSize = " + inSampleSize);
        }
        return inSampleSize;
    }
}
