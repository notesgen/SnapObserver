package com.notesgenlab.snapobserver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by Farhan on 6/10/2017.
 */

public class SnapperContentObserver extends ContentObserver {

    private final String LOG_TAG = "SnapObserver";

    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    private final ContentResolver contentResolver;
    private OnSnapTakenListener mListener;
    private boolean deleteSnap;

    /**
     * Creates a content observer.
     *
     */
    public SnapperContentObserver(Context context,OnSnapTakenListener listener) {
        super(null);
        contentResolver = context.getContentResolver();
        this.mListener = listener;
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(LOG_TAG,"onChange: " + selfChange + ", " + uri.toString());
        if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(uri, PROJECTION, null, null,
                        SORT_ORDER);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long dateAdded = cursor.getLong(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATE_ADDED));
                    long currentTime = System.currentTimeMillis() / 1000;
                    Log.d(LOG_TAG,"path: " + path + ", dateAdded: " + dateAdded +
                            ", currentTime: " + currentTime);
                    if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                        File file = new File(path);

                        if (deleteSnap) {
                            if (file != null)
                                file.delete();

                            /*
                            * A null uri is returned to listener once screenshot
                            * has been deleted.
                            * */
                            if (mListener != null)
                                mListener.onSnapTaken(null);

                        } else {

                            if (mListener != null)
                                mListener.onSnapTaken(file.getPath());
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(LOG_TAG,"open cursor fail");
                mListener.onError("Unable to observe Snap");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") ||
                path.contains("截图");
    }

    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    /**
     *  Call on Activity Resume
     */
    public void start(){
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this);
    }

    /**
     *  Call on Activity Pause
     */
    public void stop() {
        contentResolver.unregisterContentObserver(this);
    }


    public void deleteSnapshot(boolean deleteSnap) {
        this.deleteSnap = deleteSnap;
    }
}
