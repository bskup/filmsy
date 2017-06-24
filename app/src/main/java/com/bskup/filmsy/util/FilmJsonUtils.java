package com.bskup.filmsy.util;

import android.content.Context;
import android.util.Log;

import com.bskup.filmsy.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for parsing JSON.
 */
public class FilmJsonUtils {

    private static final String LOG_TAG = FilmJsonUtils.class.getSimpleName();

    /** Parses JSON from a web response and returns an array list of Film objects
     * describing movies and detailed info about them.
     *
     * @param filmInfoJsonString JSON response from server
     *
     * @return Array list of Film objects describing film data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Film> getSimpleFilmListFromJson(Context context, String filmInfoJsonString)
            throws JSONException {

        /* TMD = The Movie Database
          Each film's info is an element of the "results" array */
        final String TMD_RESULTS = "results";

        /* Each element from the "results" array will contain these objects */
        // TODO Delete unused items eventually
        final String TMD_VOTE_COUNT = "vote_count";
        final String TMD_FILM_ID = "id";
        final String TMD_VIDEO = "video";
        final String TMD_VOTE_AVERAGE = "vote_average";
        final String TMD_TITLE = "title";
        final String TMD_POPULARITY = "popularity";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_ORIGINAL_LANGUAGE = "original_language";
        final String TMD_ORIGINAL_TITLE = "original_title";
        final String TMD_GENRE_IDS = "genre_ids";
        final String TMD_BACKDROP_PATH = "backdrop_path";
        final String TMD_ADULT = "adult";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RELEASE_DATE = "release_date";

        /* In case of 401 or 404 error, we might get these objects instead */
        final String TMD_STATUS_MESSAGE = "status_message";
        final String TMD_STATUS_CODE = "status_code";

        /* Parse the full JSON response into a JSONObject */
        JSONObject filmsJson = new JSONObject(filmInfoJsonString);

        /* Test the full JSON response for error codes */
        if (filmsJson.has(TMD_STATUS_CODE)) {
            int errorCode = filmsJson.getInt(TMD_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        /* Array of Film objects that will contain parsed main film list and corresponding details
         * we're about to create with our parsing below */
        List<Film> parsedFilmData = null;
        parsedFilmData = new ArrayList<>();

        /* Catch errors in our JSON parsing */
        try {
            /* If there was no error code, there should be a list of results to get */
            JSONArray mainFilmsArray = filmsJson.getJSONArray(TMD_RESULTS);


            /* For each film item in main JSON film list, extract values
              and add to array list created above */
            for (int i = 0; i < mainFilmsArray.length(); i++) {
                /* Get the film JSON object from the main array corresponding to the iteration of the loop we're in */
                JSONObject currentFilm = mainFilmsArray.getJSONObject(i);

                /* Extract data */
                int voteCount = currentFilm.getInt(TMD_VOTE_COUNT);
                int id = currentFilm.getInt(TMD_FILM_ID);
                boolean video = currentFilm.getBoolean(TMD_VIDEO);
                double voteAverage = currentFilm.getDouble(TMD_VOTE_AVERAGE);
                String title = currentFilm.getString(TMD_TITLE);
                double popularity = currentFilm.getDouble(TMD_POPULARITY);
                String posterPath = currentFilm.getString(TMD_POSTER_PATH);
                String originalLanguage = currentFilm.getString(TMD_ORIGINAL_LANGUAGE);
                String originalTitle = currentFilm.getString(TMD_ORIGINAL_TITLE);
                /* Genre IDs is an array, get it and loop through to save values as Integer[] */
                JSONArray genreIdsJSONArray = currentFilm.getJSONArray(TMD_GENRE_IDS);
                int[] genreIds = new int[genreIdsJSONArray.length()];
                for (int j = 0; j < genreIdsJSONArray.length(); j++) {
                    genreIds[j] = genreIdsJSONArray.getInt(j);
                }
                String backdropPath = currentFilm.getString(TMD_BACKDROP_PATH);
                boolean adult = currentFilm.getBoolean(TMD_ADULT);
                String overview = currentFilm.getString(TMD_OVERVIEW);
                String releaseDate = currentFilm.getString(TMD_RELEASE_DATE);

                /* Add a new Film object to array list of Film objects */
                parsedFilmData.add(new Film(voteCount, id, video, voteAverage, title, popularity, posterPath,
                        originalLanguage, originalTitle, genreIds, backdropPath, adult, overview, releaseDate));
            }
        } catch (JSONException e) {
            /* Catch exception so app doesn't crash */
            Log.e(LOG_TAG, "Problem parsing JSON results.", e);
        }

        /* Return the whole array list of Film objects */
        return parsedFilmData;
    }

}
