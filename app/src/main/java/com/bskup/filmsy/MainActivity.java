package com.bskup.filmsy;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mTvErrorMsg;
    private ProgressBar mProgressBar;
    private PosterAdapter mPosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Get reference to RecyclerView, error textView and progress bar */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_scrolling_posters);
        mTvErrorMsg = (TextView) findViewById(R.id.tv_main_error_msg);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /** Create and set layout type for our RecyclerView using LayoutManager
         * LinearLayoutManager can support HORIZONTAL or VERTICAL. The reverse layout param
         * (false) is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages. */
        // TODO try other layout types, grid?
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /** Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView */
        mRecyclerView.setHasFixedSize(true);

        /** Create and set PosterAdapter to link our data to the Views that display it */
        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        /** Call helper method to load our poster data */
        loadPosterData();
    }

    /** Helper method that calls hideErrorMsg helper and starts new AsyncTask */
    private void loadPosterData() {
        hideErrorMsg();

        // TODO fetch a preference to affect the AsyncTask (see loadWeatherData)
        new FetchPosterDataTask().execute();
    }

    /** Overridden by MainActivity class in order to handle RecyclerView item clicks.
     *
     * @param currentPosterData Poster data for poster that was clicked
     */
    @Override
    public void onClick(String currentPosterData) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, currentPosterData);
        startActivity(intentToStartDetailActivity);
    }

    /** Helper method to hide error msg and show poster data RecyclerView */
    private void hideErrorMsg() {
        mTvErrorMsg.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /** Helper method to show error msg and hide poster data RecyclerView */
    private void showErrorMsg() {
        mTvErrorMsg.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public class FetchPosterDataTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            // TODO fill this out (see doInBackground)
        }

        @Override
        protected void onPostExecute(String[] posterData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (posterData != null) {
                hideErrorMsg();
                // Pass poster data to the adapter
                mPosterAdapter.setPosterData(posterData);
            } else {
                // If posterData is null, show error
                showErrorMsg();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use AppCompat's getMenuInflater to get the inflater to create our menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Return true so menu is displayed in toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mPosterAdapter.setPosterData(null);
            loadPosterData();
            return true;
        } else if (id == R.id.action_settings) {
            // TODO open settings intent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
