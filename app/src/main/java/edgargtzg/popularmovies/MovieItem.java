/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edgargtzg.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parcelable class which contains details data of a movie.
 */
public class MovieItem implements Parcelable {

    /**
     * Log identifier for the class.
     */
    private final String LOG_TAG = MovieItem.class.getSimpleName();

    /**
     * Contains the movie original title.
     */
    private String mOriginalTitle = "";
    /**
     * Contains the movie poster id.
     */
    private String mMoviePoster = "";
    /**
     * Contains the movie plot synopsis.
     */
    private String mPlotSynopsis = "";
    /**
     * Contains the movie user rating.
     */
    private String mUserRating = "";
    /**
     * Contains the movie release date.
     */
    private String mReleaseDate = "";

    /**
     * Default constructor.
     * @param movieJsonObject the JsonObject to extract the movie data.
     */
    public MovieItem(JSONObject movieJsonObject) {
        parseMovieData(movieJsonObject);
    }

    /**
     * Populating class variables.
     *
     * @param in the parcel request.
     */
    protected MovieItem(Parcel in) {
        mOriginalTitle = in.readString();
        mMoviePoster = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
    }

    /**
     * Default creator for the MovieItem class.
     */
    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    /**
     * Extracts the movie data from the given JsonObject.
     *
     * @param movieJsonObject to be parsed.
     */
    private void parseMovieData(JSONObject movieJsonObject) {

        // JSON keys to extract the data.
        final String ORIGINAL_TITLE = "original_title";
        final String MOVIE_POSTER = "poster_path";
        final String PLOT_SYNOPSIS = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        try {
            mOriginalTitle = (String)movieJsonObject.get(ORIGINAL_TITLE);
            mMoviePoster = (String)movieJsonObject.get(MOVIE_POSTER);
            mPlotSynopsis = (String)movieJsonObject.get(PLOT_SYNOPSIS);
            mUserRating = movieJsonObject.get(USER_RATING).toString();
            mReleaseDate = (String)movieJsonObject.get(RELEASE_DATE);
        } catch (JSONException | ClassCastException e) {
            Log.e(LOG_TAG,"Could not parse data for movie item.",e);
        }
    }

    /**
     * Gets the movie original title.
     *
     * @return the movie original title.
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * Gets the movie poster id.
     *
     * @return the movie poster id.
     */
    public String getMoviePoster() {
        return mMoviePoster;
    }

    /**
     * Gets the movie plot synopsis.
     *
     * @return the movie plot synopsis.
     */
    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    /**
     * Gets the movie user rating.
     *
     * @return the movie user rating.
     */
    public String getUserRating() {
        return mUserRating;
    }

    /**
     * Gets the movie release date.
     *
     * @return the movie release date.
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mMoviePoster);
        dest.writeString(mPlotSynopsis);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
    }
}
