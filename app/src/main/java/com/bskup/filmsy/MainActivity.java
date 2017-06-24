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

import com.bskup.filmsy.util.FilmJsonUtils;
import com.bskup.filmsy.util.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmAdapter.FilmAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mTvErrorMsg;
    private ProgressBar mProgressBar;
    private FilmAdapter mFilmAdapter;

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

        /** Create and set FilmAdapter to link our data to the Views that display it */
        mFilmAdapter = new FilmAdapter(this);
        mRecyclerView.setAdapter(mFilmAdapter);

        /** Call helper method to load our Film data */
        loadFilmData();
    }

    /** Helper method that calls hideErrorMsg helper and starts new AsyncTask */
    private void loadFilmData() {
        hideErrorMsg();

        // TODO fetch a preference to affect the AsyncTask (see loadWeatherData)
        new FetchPosterDataTask().execute();
    }

    /** Overridden by MainActivity class in order to handle RecyclerView item clicks.
     *
     * @param currentFilm Film data for film that was clicked
     */
    @Override
    public void onClick(Film currentFilm) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("FilmExtra", currentFilm);
        startActivity(intentToStartDetailActivity);
    }

    /** Helper method to hide error msg and show film data RecyclerView */
    private void hideErrorMsg() {
        mTvErrorMsg.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /** Helper method to show error msg and hide film data RecyclerView */
    private void showErrorMsg() {
        mTvErrorMsg.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public class FetchPosterDataTask extends AsyncTask<String, Void, List<Film>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Film> doInBackground(String... params) {
            // If no params, nothing to query
            if (params.length == 0) {
                return null;
            }
            String endpoint = params[0];
            URL filmsRequestUrl = NetworkUtils.buildUrl(endpoint);

            try {
                String jsonFilmsResponse = NetworkUtils.getResponseFromHttpUrl(filmsRequestUrl);

                List<Film> simpleJsonFilmData = FilmJsonUtils.getSimpleFilmListFromJson(MainActivity.this, jsonFilmsResponse);

                return simpleJsonFilmData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Film> filmData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (filmData != null) {
                hideErrorMsg();
                // Pass film data to the adapter
                mFilmAdapter.setFilmData(filmData);
            } else {
                // If filmData is null, show error
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
            mFilmAdapter.setFilmData(null);
            loadFilmData();
            return true;
        } else if (id == R.id.action_settings) {
            // TODO open settings intent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
