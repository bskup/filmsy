package com.bskup.filmsy;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.bskup.filmsy.util.FilmJsonUtils;
import com.bskup.filmsy.util.NetworkUtils;

import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FilmAdapter.FilmAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TMD_POPULAR_ENDPOINT = "/popular";
    private static final String TMD_TOP_RATED_ENDPOINT = "/top_rated";

    private RecyclerView mRecyclerView;
    private TextView mTvErrorMsg;
    private FilmAdapter mFilmAdapter;
    private Activity mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Theme change based on preference */
        String themeName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme", "AppThemeDark");
        Log.v(LOG_TAG, "value for theme String in mainactivity oncreate: " + themeName);
        if (themeName.equals("AppThemeLight")) {
            setTheme(R.style.AppThemeLight);
        } else if (themeName.equals("AppThemeDark")) {
            setTheme(R.style.AppThemeDark);

        }

        setContentView(R.layout.activity_main);

        mContext = this;

        /* Setup bottom nav colors based on theme */
        AHBottomNavigation bottomNav = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        TypedValue typedValueBg = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(R.attr.bottomNavBg, typedValueBg, true);
        int resolvedBottomNavBgColor = typedValueBg.data;
        Log.d(LOG_TAG, "resolvedBottomNavBgColor: " + resolvedBottomNavBgColor);
        bottomNav.setDefaultBackgroundColor(resolvedBottomNavBgColor);

        TypedValue typedValueActive = new TypedValue();
        theme.resolveAttribute(R.attr.bottomNavActive, typedValueActive, true);
        int resolvedBottomNavActiveColor = typedValueActive.data;
        bottomNav.setAccentColor(resolvedBottomNavActiveColor);

        TypedValue typedValueInactive = new TypedValue();
        theme.resolveAttribute(R.attr.bottomNavInactive, typedValueInactive, true);
        int resolvedBottomNavInactiveColor = typedValueInactive.data;
        bottomNav.setInactiveColor(resolvedBottomNavInactiveColor);

        /* Setup bottom nav adapter */
        AHBottomNavigationAdapter bottomNavAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_nav_main);
        bottomNavAdapter.setupWithBottomNavigation(bottomNav);

        /* Setup bottom nav listener */
        bottomNav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                /* Do something when user clicks a bottom nav menu icon */
                Log.d(LOG_TAG, "Bottom nav position clicked was: " + position);
                //Fragment selectedFragment = null;
                switch (position) {
                    case 0:
                        PopularFragment popularFragment = PopularFragment.newInstance();
                        FragmentTransaction popularTransaction = getFragmentManager().beginTransaction();
                        popularTransaction.replace(R.id.fragment_container, popularFragment);
                        popularTransaction.commit();
                        return true;
                    case 1:
                        TopRatedFragment topRatedFragment = TopRatedFragment.newInstance();
                        FragmentTransaction topRatedTransaction = getFragmentManager().beginTransaction();
                        topRatedTransaction.replace(R.id.fragment_container, topRatedFragment);
                        topRatedTransaction.commit();
                        return true;
                    case 2:
                        SettingsFragment settingsFragment = SettingsFragment.newInstance();
                        FragmentTransaction settingsTransaction = getFragmentManager().beginTransaction();
                        settingsTransaction.replace(R.id.fragment_container, settingsFragment);
                        settingsTransaction.commit();
                        return true;
                }
                return true;
            }
        });

        /* Manually display first fragment on first launch */
        FragmentTransaction firstLaunchTransaction = getFragmentManager().beginTransaction();
        firstLaunchTransaction.replace(R.id.fragment_container, new PopularFragment());
        firstLaunchTransaction.commit();
    }

    /** Helper method that calls hideErrorMsg helper and starts new AsyncTask */
    private void loadFilmData() {
        hideErrorMsg();

        // TODO fetch a preference to affect the AsyncTask (see loadWeatherData)
        new FetchFilmDataTask().execute(TMD_POPULAR_ENDPOINT);
        Log.d(LOG_TAG, "new FetchFilmDataTask executed from loadFilmData()");
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

    public class FetchFilmDataTask extends AsyncTask<String, Void, List<Film>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mProgressBar.setVisibility(View.VISIBLE);
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
            //mProgressBar.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
