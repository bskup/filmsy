package com.bskup.filmsy.util;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Build URL and create HTTPUrlConnection to The Movie Database server.
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String TMD_BASE_URL =
            "http://api.themoviedb.org/3/movie";
    private static final String TMD_POPULAR_ENDPOINT =
            "/popular";
    private static final String TMD_TOP_RATED_ENDPOINT =
            "/top_rated";
    /** API Key - REMOVE FROM PUBLICLY-SHARED CODE */
    private static final String apiKey = "677327156cd5bd6d499e0b800cfe124b";

    final static String API_KEY_PARAM = "api_key";

    /** Builds the URL used to talk to the movie database server.
     *
     * @param endpoint String determines which api endpoint to connect to. Could be
     *                 popular, top_rated, or a single movie id
     * @return The full URL to use to query the movie database server.
     */
    public static URL buildUrl(String endpoint) {
        String baseUrlWithEndpoint;
        if (endpoint == "/popular") {
            baseUrlWithEndpoint = TMD_BASE_URL + TMD_POPULAR_ENDPOINT;
        } else if (endpoint == "/top_rated") {
            baseUrlWithEndpoint = TMD_BASE_URL + TMD_TOP_RATED_ENDPOINT;
        } else {
            baseUrlWithEndpoint = TMD_BASE_URL + endpoint;
        }

        Uri builtUri = Uri.parse(baseUrlWithEndpoint).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    /** Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            // Delimiter \A means "start of String"
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
