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
 * Contains a review of a movie item.
 */
public class MovieItemReview implements Parcelable {

    /**
     * Log identifier for the class.
     */
    private final String LOG_TAG = MovieItemReview.class.getSimpleName();

    /**
     * Contains the review author.
     */
    private String mReviewAuthor = "";

    /**
     * Contains the review content.
     */
    private String mReviewContent = "";

    /**
     * Default constructor.
     *
     * @param movieJsonObject the JsonObject to extract the movie review data.
     */
    public MovieItemReview(JSONObject movieJsonObject) {
        parseMovieData(movieJsonObject);
    }

    /**
     * Populating class variables.
     *
     * @param in the parcel request.
     */
    protected MovieItemReview(Parcel in) {
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
    }

    /**
     * Default creator for the MovieItemReview class.
     */
    public static final Creator<MovieItemReview> CREATOR = new Creator<MovieItemReview>() {
        @Override
        public MovieItemReview createFromParcel(Parcel in) {
            return new MovieItemReview(in);
        }

        @Override
        public MovieItemReview[] newArray(int size) {
            return new MovieItemReview[size];
        }
    };

    /**
     * Extracts the movie review data from the given JsonObject.
     *
     * @param movieJsonObject to be parsed.
     */
    private void parseMovieData(JSONObject movieJsonObject) {

        // JSON keys to extract the data.
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        try {
            mReviewAuthor = (String)movieJsonObject.get(REVIEW_AUTHOR);
            mReviewContent = (String)movieJsonObject.get(REVIEW_CONTENT);
        } catch (JSONException | ClassCastException e) {
            Log.e(LOG_TAG, "Could not parse data for videos of movie item.", e);
        }
    }

    /**
     * Gets the review author.
     *
     * @return the review author.
     */
    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    /**
     * Gets the review content.
     *
     * @return the review content.
     */
    public String getReviewContent() {
        return mReviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReviewAuthor);
        dest.writeString(mReviewContent);
    }
}
