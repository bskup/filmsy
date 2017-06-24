package com.bskup.filmsy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Takes parsed Film data and adds it to our layout.
 */
public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmAdapterViewHolder> {

    private static final String LOG_TAG = FilmAdapter.class.getSimpleName();

    /** Variable holding our data set */
    private List<Film> mFilmData;

    /** OnClick handler to make it easy for an Activity to interface with our RecyclerView */
    private final FilmAdapterOnClickHandler mClickHandler;

    /** Interface that receives onClick messages. */
    public interface FilmAdapterOnClickHandler {
        void onClick(Film currentFilm);
    }

    /** Creates a FilmAdapter.
     *
     * @param clickHandler OnClick handler for this adapter, called when an item is clicked.
     */
    public FilmAdapter(FilmAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class FilmAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTvFilmTitle;
        public final TextView mTvFilmSubTitle;
        public final ImageView mIvPoster;

        /** Creates a FilmAdapterViewHolder.
         *
         * @param itemView View passed in when creating, containing inflated poster_list_item layout
         */
        public FilmAdapterViewHolder(View itemView) {
            super(itemView);
            // Find views in our poster_list_item layout we will populate with data
            mTvFilmTitle = (TextView) itemView.findViewById(R.id.tv_poster_title);
            mTvFilmSubTitle = (TextView) itemView.findViewById(R.id.tv_poster_subtitle);
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
        int layoutIdForListItem = R.layout.poster_list_item;
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
        // Get poster and other info from our dataset at position
        Film currentFilm = mFilmData.get(position);
        // TODO Set poster to imageview and other info to textviews in our layout
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
