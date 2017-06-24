package com.bskup.filmsy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds details about a single Film.
 */
public class Film implements Parcelable {

    /** Private fields to hold each film detail passed in via constructor */
    // TODO Delete unused fields eventually
    private int mVoteCount;
    private int mId;
    private boolean mVideo;
    private double mVoteAverage;
    private String mTitle;
    private double mPopularity;
    private String mPosterPath;
    private String mOriginalLanguage;
    private String mOriginalTitle;
    private int[] mGenreIds;
    private String mBackdropPath;
    private boolean mAdult;
    private String mOverview;
    private String mReleaseDate;

    /** Constructor method */
    public Film(int voteCount, int id, boolean video, double voteAverage, String title,
                double popularity, String posterPath, String originalLanguage, String originalTitle,
                int[] genreIds, String backdropPath, boolean adult, String overview, String releaseDate) {
        mVoteCount = voteCount;
        mId = id;
        mVideo = video;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPopularity = popularity;
        mPosterPath = posterPath;
        mOriginalLanguage = originalLanguage;
        mOriginalTitle = originalTitle;
        mGenreIds = genreIds;
        mBackdropPath = backdropPath;
        mAdult = adult;
        mOverview = overview;
        mReleaseDate = releaseDate;
    }

    /** Getter methods */
    public int getVoteCount() {
        return mVoteCount;
    }

    public int getId() {
        return mId;
    }

    public boolean getVideo() {
        return mVideo;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public int[] getGenreIds() {
        return mGenreIds;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public boolean getAdult() {
        return mAdult;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    /* Parcelable implementation */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mVoteCount);
        out.writeInt(mId);
        out.writeString(String.valueOf(mVideo));
        out.writeDouble(mVoteAverage);
        out.writeString(mTitle);
        out.writeDouble(mPopularity);
        out.writeString(mPosterPath);
        out.writeString(mOriginalLanguage);
        out.writeString(mOriginalTitle);
        out.writeIntArray(mGenreIds);
        out.writeString(mBackdropPath);
        out.writeString(String.valueOf(mAdult));
        out.writeString(mOverview);
        out.writeString(mReleaseDate);
    }

    public static final Parcelable.Creator<Film> CREATOR = new Parcelable.Creator<Film>() {

        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        public Film[] newArray(int size) {
            return new Film[size];
        }
    };

    /* Film constructor using Parcel, read in same order as fields were written */
    private Film(Parcel in) {
        mVoteCount = in.readInt();
        mId = in.readInt();
        mVideo = Boolean.getBoolean(in.readString());
        mVoteAverage = in.readDouble();
        mTitle = in.readString();
        mPopularity = in.readDouble();
        mPosterPath = in.readString();
        mOriginalLanguage = in.readString();
        mOriginalTitle = in.readString();
        mGenreIds = in.createIntArray();
        mBackdropPath = in.readString();
        mAdult = Boolean.getBoolean(in.readString());
        mOverview = in.readString();
        mReleaseDate = in.readString();
    }
}
