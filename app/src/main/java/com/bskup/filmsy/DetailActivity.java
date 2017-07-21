package com.bskup.filmsy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.R.attr.bitmap;

/**
 * Created on 6/15/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Film mCurrentFilm;
    private Context mContext;

    /* Base image url using w342 as the size */
    private final String TMD_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Theme change based on preference */
        String themeName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme", "AppThemeDark");
        Log.v(LOG_TAG, "value for theme String in detailactivity oncreate: " + themeName);
        if (themeName.equals("AppThemeLight")) {
            setTheme(R.style.AppThemeLight);
        } else if (themeName.equals("AppThemeDark")) {
            setTheme(R.style.AppThemeDark);

        }

        setContentView(R.layout.activity_detail);

        mContext = this;

        Intent intentThatStartedThisActivity = getIntent();
        mCurrentFilm = intentThatStartedThisActivity.getParcelableExtra("FilmExtra");

        int voteCount = mCurrentFilm.getVoteCount();
        int id = mCurrentFilm.getId();
        boolean video = mCurrentFilm.getVideo();
        double voteAverage = mCurrentFilm.getVoteAverage();
        String title = mCurrentFilm.getTitle();
        double popularity = mCurrentFilm.getPopularity();
        String posterPath = mCurrentFilm.getPosterPath();
        String originalLanguage = mCurrentFilm.getOriginalLanguage();
        String originalTitle = mCurrentFilm.getOriginalTitle();
        int[] genreIds = mCurrentFilm.getGenreIds();
        String backdropPath = mCurrentFilm.getBackdropPath();
        boolean adult = mCurrentFilm.getAdult();
        String overview = mCurrentFilm.getOverview();
        String releaseDate = mCurrentFilm.getReleaseDate();

        /* Get references to views we're going to manipulate */
        final ImageView ivPosterLarge = (ImageView) findViewById(R.id.iv_poster_large);
        final TextView tvFilmTitle = (TextView) findViewById(R.id.tv_film_title);
        final TextView tvFilmReleaseDate = (TextView) findViewById(R.id.tv_film_release_date);
        TextView tvFilmOverview = (TextView) findViewById(R.id.tv_film_overview);
        final LinearLayout llColoredBarWithText = (LinearLayout) findViewById(R.id.ll_colored_bar_with_text);
        final View vColorTintBox = (View) findViewById(R.id.v_color_tint_box);
        ImageView ivPosterSmallFullview = (ImageView) findViewById(R.id.iv_poster_small_fullview);

        /* Set data on views */
        /* Set poster to imageView using Picasso, colors using Palette */
        if (posterPath != null) {
            Picasso.with(mContext).load(TMD_BASE_IMAGE_URL + posterPath).into(ivPosterLarge, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    Log.d(LOG_TAG, "onSuccess called");
                    Bitmap bitmap = ((BitmapDrawable) ivPosterLarge.getDrawable()).getBitmap();
                    if (bitmap != null) {
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                Log.d(LOG_TAG, "mutedSwatch generated");
                                if (mutedSwatch == null) {
                                    Log.d(LOG_TAG, "Null swatch!");
                                    return;
                                }

                                /* Set list item card text color based on theme */
                                TypedValue typedValueTextColor = new TypedValue();
                                getTheme().resolveAttribute(R.attr.listItemCardTextColor, typedValueTextColor, true);
                                int resolvedListItemCardTextColor = typedValueTextColor.data;
                                Log.d(LOG_TAG, "resolvedListItemCardTextColor: " + Integer.toHexString(resolvedListItemCardTextColor));

                                tvFilmTitle.setTextColor(resolvedListItemCardTextColor);
                                tvFilmReleaseDate.setTextColor(resolvedListItemCardTextColor);
                                vColorTintBox.setBackgroundColor(mutedSwatch.getRgb());
                            }
                        });
                    }
                }
            });
            Picasso.with(mContext).load(TMD_BASE_IMAGE_URL + posterPath).into(ivPosterSmallFullview);
            Log.d(LOG_TAG, "2nd picasso load image called");

        }
        /* Set title */
        tvFilmTitle.setText(title);
        /* Set release date */
        tvFilmReleaseDate.setText(releaseDate);
        /* Set overview */
        tvFilmOverview.setText(overview);

    }
}
