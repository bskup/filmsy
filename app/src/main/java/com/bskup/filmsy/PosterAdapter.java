package com.bskup.filmsy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created on 6/15/2017.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    /** Variable holding our data set */
    private String[] mPosterData;

    /** OnClick handler to make it easy for an Activity to interface with our RecyclerView */
    private final PosterAdapterOnClickHandler mClickHandler;

    /** Interface that receives onClick messages. */
    public interface PosterAdapterOnClickHandler {
        void onClick(String currentPosterData);
    }

    /** Creates a PosterAdapter.
     *
     * @param clickHandler OnClick handler for this adapter, called when an item is clicked.
     */
    public PosterAdapter(PosterAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTvPosterTitle;
        public final TextView mTvPosterSubTitle;
        public final ImageView mIvPoster;

        /** Creates a PosterAdapterViewHolder.
         *
         * @param itemView View passed in when creating, containing inflated poster_list_item layout
         */
        public PosterAdapterViewHolder(View itemView) {
            super(itemView);
            // Find views in our poster_list_item layout we will populate with data
            mTvPosterTitle = (TextView) itemView.findViewById(R.id.tv_poster_title);
            mTvPosterSubTitle = (TextView) itemView.findViewById(R.id.tv_poster_subtitle);
            mIvPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
        }

        /** Called by the child views when clicked.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String currentPosterData = mPosterData[adapterPosition];
            mClickHandler.onClick(currentPosterData);
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
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get context for LayoutInflater to use
        Context context = parent.getContext();
        // Layout to inflate
        int layoutIdForListItem = R.layout.poster_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // Inflate our list item layout and store in a View
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        // Return ViewHolder holding the View we created with our inflated layout above
        return new PosterAdapterViewHolder(view);
    }

    /** Binds data to ViewHolder to display based on position argument
     *
     * @param holder The ViewHolder we're updating with new data to display
     * @param position Position of the item within the data set the adapter is using
     */
    @Override
    public void onBindViewHolder(PosterAdapterViewHolder holder, int position) {
        // Get poster and other info from our dataset at position
        String currentPosterData = mPosterData[position];
        // TODO Set poster to imageview and other info to textviews in our layout
    }

    /** Returns the number of items to display. Used to help layout our Views and for animations.
     *
     * @return The number of items available in our data set
     */
    @Override
    public int getItemCount() {
        // If no data, return 0
        if (mPosterData == null) {
            return 0;
        }
        // If there's data, return the length of our data set
        return mPosterData.length;
    }

    /** Sets new poster data on a PosterAdapter if we've already created one.
     * Used when we get new data from the web but don't want to create a
     * new PosterAdapter to display it.
     *
     * @param posterData The new poster data to be displayed.
     */
    public void setPosterData(String[] posterData) {
        mPosterData = posterData;
        notifyDataSetChanged();
    }
}
