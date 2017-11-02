package com.bskup.filmsy;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bskup.filmsy.util.FilmJsonUtils;
import com.bskup.filmsy.util.NetworkUtils;

import java.net.URL;
import java.util.List;


/**
 * Created on 7/6/2017.
 */

public class PopularFragment extends Fragment implements FilmAdapter.FilmAdapterOnClickHandler {

    private static final String LOG_TAG = PopularFragment.class.getSimpleName();

    private static final String TMD_POPULAR_ENDPOINT = "/popular";
    private static final String TMD_TOP_RATED_ENDPOINT = "/top_rated";

    private RecyclerView mRecyclerView;
    private TextView mTvErrorMsg;
    private FilmAdapter mFilmAdapter;
    private Activity mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mEmptyStateImageView;
    private TextView mEmptyStateTextView;

    public static PopularFragment newInstance() {
        PopularFragment fragment = new PopularFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /* Call helper method to load our Film data */
        //loadPopularFilmData();
        Log.d(LOG_TAG, "loadFilmData() called from OnCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume called from PopularFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        View inflatedLayout = inflater.inflate(R.layout.fragment_popular, container, false);

        /* Get reference to RecyclerView, error textView and progress bar */
        mRecyclerView = (RecyclerView) inflatedLayout.findViewById(R.id.rv_main_scrolling_posters);
        mTvErrorMsg = (TextView) inflatedLayout.findViewById(R.id.tv_main_error_msg);
        mEmptyStateImageView = (ImageView) inflatedLayout.findViewById(R.id.iv_empty_state);
        mEmptyStateTextView = (TextView) inflatedLayout.findViewById(R.id.tv_empty_state);

        /* Use to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView */
        mRecyclerView.setHasFixedSize(true);

        /* Create and set FilmAdapter to link our data to the Views that display it */
        mFilmAdapter = new FilmAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mFilmAdapter);

        /* Find swipe refresh layout and set onRefresh listener to handle swipe down refresh */
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedLayout.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do stuff when user swipes down to refresh */
                Log.v(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                mFilmAdapter.setFilmData(null);
                loadPopularFilmData();
            }
        });

        /** Create and set layout type for our RecyclerView using LayoutManager
         * LinearLayoutManager can support HORIZONTAL or VERTICAL. The reverse layout param
         * (false) is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages. */
        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Testing
        loadPopularFilmData();

        return inflatedLayout;
    }


    /** Helper method that calls hideErrorMsg helper and starts new AsyncTask */
    private void loadPopularFilmData() {
        hideErrorMsg();

        // Only execute the new asynctask if we have a network connection
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {

            // TODO fetch a preference to affect the AsyncTask (see loadWeatherData)
            new FetchFilmDataTask().execute(TMD_POPULAR_ENDPOINT);
            Log.d(LOG_TAG, "new FetchFilmDataTask executed from loadFilmData()");
        } else {
            // Display network error (hide loading indicator and change empty state text)
            mSwipeRefreshLayout.setRefreshing(false);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateImageView.setVisibility(View.VISIBLE);
            mEmptyStateImageView.setImageResource(R.drawable.ic_no_network_accent_green);
        }
    }

    /** Overridden in order to handle RecyclerView item clicks.
     *
     * @param currentFilm Film data for film that was clicked
     */
    @Override
    public void onClick(Film currentFilm) {
        Context context = mContext;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("FilmExtra", currentFilm);
        startActivity(intentToStartDetailActivity);
        Log.d(LOG_TAG, "onClick called from PopularFragment");
    }

    /** Helper method to hide error msg and show film data RecyclerView */
    private void hideErrorMsg() {
        mTvErrorMsg.setVisibility(View.INVISIBLE);
        mEmptyStateTextView.setVisibility(View.GONE);
        mEmptyStateImageView.setVisibility(View.GONE);
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

                List<Film> simpleJsonFilmData = FilmJsonUtils.getSimpleFilmListFromJson(mContext, jsonFilmsResponse);

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

}