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
 * Contains the video information of a movie item.
 */
public class MovieItemVideo implements Parcelable {

    /**
     * Log identifier for the class.
     */
    private final String LOG_TAG = MovieItemVideo.class.getSimpleName();

    /**
     * Contains the movie video key.
     */
    private String mVideoKey = "";

    /**
     * Contains the movie video name.
     */
    private String mVideoName = "";

    /**
     * Contains the movie video site.
     */
    private String mVideoSite = "";

    /**
     * Contains the movie video type.
     */
    private String mVideoType = "";

    /**
     * Default constructor.
     *
     * @param movieJsonObject the JsonObject to extract the movie video data.
     */
    public MovieItemVideo(JSONObject movieJsonObject) {
        parseMovieData(movieJsonObject);
    }

    /**
     * Populating class variables.
     *
     * @param in the parcel request.
     */
    protected MovieItemVideo(Parcel in) {
        mVideoKey = in.readString();
        mVideoName = in.readString();
        mVideoSite = in.readString();
        mVideoType = in.readString();
    }

    /**
     * Default creator for the MovieItemVideo class.
     */
    public static final Creator<MovieItemVideo> CREATOR = new Creator<MovieItemVideo>() {
        @Override
        public MovieItemVideo createFromParcel(Parcel in) {
            return new MovieItemVideo(in);
        }

        @Override
        public MovieItemVideo[] newArray(int size) {
            return new MovieItemVideo[size];
        }
    };

    /**
     * Extracts the movie video data from the given JsonObject.
     *
     * @param movieJsonObject to be parsed.
     */
    private void parseMovieData(JSONObject movieJsonObject) {

        // JSON keys to extract the data.
        final String VIDEO_KEY = "key";
        final String VIDEO_NAME = "name";
        final String VIDEO_SITE = "site";
        final String VIDEO_TYPE = "type";

        try {
            mVideoKey = (String)movieJsonObject.get(VIDEO_KEY);
            mVideoName = (String)movieJsonObject.get(VIDEO_NAME);
            mVideoSite = (String)movieJsonObject.get(VIDEO_SITE);
            mVideoType = (String)movieJsonObject.get(VIDEO_TYPE);
        } catch (JSONException | ClassCastException e) {
            Log.e(LOG_TAG, "Could not parse data for videos of movie item.", e);
        }
    }

    /**
     * Gets the video key.
     *
     * @return the video key.
     */
    public String getVideoKey() {
        return mVideoKey;
    }

    /**
     * Gets the video name.
     *
     * @return the video name.
     */
    public String getVideoName() {
        return mVideoName;
    }

    /**
     * Get the video site.
     *
     * @return the video site.
     */
    public String getVideoSite() {
        return mVideoSite;
    }

    /**
     * Get the video type.
     *
     * @return the video type.
     */
    public String getVideoType() {
        return mVideoType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mVideoKey);
        dest.writeString(mVideoName);
        dest.writeString(mVideoSite);
        dest.writeString(mVideoType);
    }
}
