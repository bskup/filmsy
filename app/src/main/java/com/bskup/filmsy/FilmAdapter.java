package com.bskup.filmsy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Takes parsed Film data and adds it to our layout.
 */
public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmAdapterViewHolder> {

    private static final String LOG_TAG = FilmAdapter.class.getSimpleName();

    /* Base image url using w500 as the size */
    private final String TMD_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

    /* Context passed in via constructor, used with Picasso */
    private Context mContext;

    /* OnClick handler to make it easy for an Activity to interface with our RecyclerView */
    private final FilmAdapterOnClickHandler mClickHandler;

    /* Interface that receives onClick messages. */
    public interface FilmAdapterOnClickHandler {
        void onClick(Film currentFilm);
    }

    /* Our data set, a list of custom Film objects */
    private List<Film> mFilmData;

    /** Creates a FilmAdapter.
     *
     * @param clickHandler OnClick handler for this adapter, called when an item is clicked.
     */
    public FilmAdapter(Context context, FilmAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    public class FilmAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTvFilmTitle;
        public final TextView mTvFilmReleaseDate;
        public final ImageView mIvPoster;

        /** Creates a FilmAdapterViewHolder.
         *
         * @param itemView View passed in when creating, containing inflated film_list_item layout
         */
        public FilmAdapterViewHolder(View itemView) {
            super(itemView);
            // Find views in our film_list_item layout we will populate with data
            mTvFilmTitle = (TextView) itemView.findViewById(R.id.tv_film_title);
            mTvFilmReleaseDate = (TextView) itemView.findViewById(R.id.tv_film_release_date);
            mIvPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
        }

        /** Called by the child views when clicked.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Film currentFilm = mFilmData.get(adapterPosition);
            mClickHandler.onClick(currentFilm);
        }
    }

    /** When RecyclerView is laid out, this is called enough times to fill the screen
     * and allow scrolling.
     *
     * @param parent The ViewGroup these ViewHolders are contained within
     * @param viewType Used with multiple layout types (we don't use this now)
     * @return A new PosterAdapterViewHolder that holds the View for each list item
     */
    @Override
    public FilmAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get context for LayoutInflater to use
        Context context = parent.getContext();
        // Layout to inflate
        int layoutIdForListItem = R.layout.film_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // Inflate our list item layout and store in a View
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        // Return ViewHolder holding the View we created with our inflated layout above
        return new FilmAdapterViewHolder(view);
    }

    /** Binds data to ViewHolder to display based on position argument
     *
     * @param holder The ViewHolder we're updating with new data to display
     * @param position Position of the item within the data set the adapter is using
     */
    @Override
    public void onBindViewHolder(FilmAdapterViewHolder holder, int position) {
        // Get poster and other info from our data set at position
        Film currentFilm = mFilmData.get(position);
        int voteCount = currentFilm.getVoteCount();
        int id = currentFilm.getId();
        boolean video = currentFilm.getVideo();
        double voteAverage = currentFilm.getVoteAverage();
        String title = currentFilm.getTitle();
        double popularity = currentFilm.getPopularity();
        String posterPath = currentFilm.getPosterPath();
        String originalLanguage = currentFilm.getOriginalLanguage();
        String originalTitle = currentFilm.getOriginalTitle();
        int[] genreIds = currentFilm.getGenreIds();
        String backdropPath = currentFilm.getBackdropPath();
        boolean adult = currentFilm.getAdult();
        String overview = currentFilm.getOverview();
        String releaseDate = currentFilm.getReleaseDate();

        /* Set title and release date */
        // TODO Do stuff with other fields or delete them
        holder.mTvFilmTitle.setText(title);
        holder.mTvFilmReleaseDate.setText(releaseDate);

        /* Set poster to imageView using Picasso */
        if (posterPath != null) {
            Picasso.with(mContext).load(TMD_BASE_IMAGE_URL + posterPath).into(holder.mIvPoster);
        }

    }

    /** Returns the number of items to display. Used to help layout our Views and for animations.
     *
     * @return The number of items available in our data set
     */
    @Override
    public int getItemCount() {
        // If no data, return 0
        if (mFilmData == null) {
            return 0;
        }
        // If there's data, return the length of our data set
        return mFilmData.size();
    }

    /** Sets new film data on a FilmAdapter if we've already created one.
     * Used when we get new data from the web but don't want to create a
     * new FilmAdapter to display it.
     *
     * @param filmData The new film data to be displayed.
     */
    public void setFilmData(List<Film> filmData) {
        mFilmData = filmData;
        notifyDataSetChanged();
    }
}
