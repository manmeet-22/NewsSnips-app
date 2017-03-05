package com.androidexample.newssnips;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static com.androidexample.newssnips.MainActivity.LOG_TAG;

/**
 * Created by MANI on 2/6/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /**
     * Query URL
     */
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        Log.i(LOG_TAG, "NewsLoader called");
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading called");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<News> Books = QueryUtils.fetchBooksData(mUrl);

        return Books;
    }

}