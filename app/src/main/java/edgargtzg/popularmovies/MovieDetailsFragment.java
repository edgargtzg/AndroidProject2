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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Movie details fragment which populates the different view components.
 */
public class MovieDetailsFragment extends Fragment {

    /**
     * Adapter to populate view with movie video items.
     */
    private MovieItemVideoAdapter mMovieVideoAdapter;

    /**
     * Current movie item.
     */
    private MovieItem mMovieItem;

    /**
     * Contains the list of movie video items.
     */
    private ArrayList<MovieItemVideo> mListOfVideos;

    /**
     * Adapter to populate view with movie review items.
     */
    private MovieItemReviewAdapter mMovieReviewAdapter;

    /**
     * Contains the list of movie review items.
     */
    private ArrayList<MovieItemReview> mListOfReviews;

    /**
     * Video list key to use when saving state of the activity.
     */
    private static final String VIDEO_LIST_KEY = "VIDEO_LIST_KEY";

    /**
     * Review list key to use when saving state of the activity.
     */
    private static final String REVIEW_LIST_KEY = "REVIEW_LIST_KEY";

    /**
     * Default constructor.
     */
    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MovieItem.class.getCanonicalName())) {
            Bundle movieData = intent.getExtras();
            mMovieItem = movieData.getParcelable(
                    MovieItem.class.getCanonicalName());

            if (mMovieItem != null) {

                // The MovieItemVideoAdapter will take data from a source and
                // use it to populate the list it's attached to.
                mMovieVideoAdapter =
                        new MovieItemVideoAdapter(
                                getActivity(), // The current context (this activity)
                                R.layout.list_video_movie_item,
                                new ArrayList<MovieItemVideo>());

                // The MovieItemReviewAdapter will take data from a source and
                // use it to populate the list it's attached to.
                mMovieReviewAdapter =
                        new MovieItemReviewAdapter(
                                getActivity(), // The current context (this activity)
                                R.layout.list_review_movie_item,
                                new ArrayList<MovieItemReview>());

                if (savedInstanceState != null) {
                    mListOfVideos = (ArrayList<MovieItemVideo>) savedInstanceState.get(VIDEO_LIST_KEY);
                    if (mListOfVideos != null) {
                        mMovieVideoAdapter.addAll(mListOfVideos);
                    }
                    mListOfReviews = (ArrayList<MovieItemReview>) savedInstanceState.get(REVIEW_LIST_KEY);
                    if (mListOfReviews != null) {
                        mMovieReviewAdapter.addAll(mListOfReviews);
                    }
                } else {
                    mListOfVideos = new ArrayList<>();
                    mListOfReviews = new ArrayList<>();
                    updateMovieData(mMovieItem);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        if (mMovieItem != null) {
            String value;
            // Populates movie original title text view value.
            value = mMovieItem.getOriginalTitle();
            if (!(value.isEmpty())) {
                TextView originalTitleTextView =
                        (TextView) rootView.findViewById(R.id.movie_title_textView);
                originalTitleTextView.setText(value);
            }

            // Populates movie poster image.
            value = mMovieItem.getMoviePoster();
            if (!(value.isEmpty())) {
                ImageView moviePosterImageView =
                        (ImageView) rootView.findViewById(R.id.movie_poster_imageView);
                Picasso.with(getActivity()).load(
                        rootView.getResources().getString(R.string.details_poster_api_call) +
                                value).into(moviePosterImageView);
            }

            // Populates movie plot synopsis.
            value = mMovieItem.getPlotSynopsis();
            if (!(value.isEmpty())) {
                TextView moviePlotTextView =
                        (TextView) rootView.findViewById(R.id.movie_plot_textView);
                moviePlotTextView.setText(value);
            }

            // Populates movie user rating.
            value = mMovieItem.getUserRating();
            if (!(value.isEmpty())) {
                RatingBar movieRateBar = (RatingBar) rootView.findViewById(R.id.movie_ratingBar);
                movieRateBar.setRating((Float.parseFloat(value) / 2));
            }

            // Populates movie release date value.
            value = mMovieItem.getReleaseDate();
            if (!(value.isEmpty())) {
                TextView movieReleaseTextView =
                        (TextView) rootView.findViewById(R.id.movie_release_textView);
                movieReleaseTextView.setText(value);
            }

            ListView videoListView = (ListView) rootView.findViewById(R.id.movie_details_videos_listview);
            videoListView.setAdapter(mMovieVideoAdapter);
            // Adds play trailer using Youtube app or web browser.
            videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    MovieItemVideo videoItem = mMovieVideoAdapter.getItem(position);
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + videoItem.getVideoKey()));
                    startActivity(intent);
                }
            });

            ListView reviewListView = (ListView) rootView.findViewById(R.id.movie_details_reviews_listview);
            reviewListView.setAdapter(mMovieReviewAdapter);
            reviewListView.setEnabled(false);
        }
        return rootView;
    }


    /**
     * Updates the content of the view based on the movie data.
     *
     * @param movieItem the current movie item.
     */
    private void updateMovieData(MovieItem movieItem) {

        if (isNetworkAvailable()) {
            FetchMovieVideosTask fetchMovieVideosTask = new FetchMovieVideosTask(movieItem);
            fetchMovieVideosTask.execute();
            FetchMovieReviewsTask fetchMovieReviewsTask = new FetchMovieReviewsTask(movieItem);
            fetchMovieReviewsTask.execute();
        } else {
            Toast toast = Toast.makeText(
                    getActivity(), R.string.error_msg_no_network, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    /**
     * Checks if there is any network available.
     * Based on a stackoverflow snippet.
     *
     * @return true if there is a network available, otherwise false.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(VIDEO_LIST_KEY, mListOfVideos);
        outState.putParcelableArrayList(REVIEW_LIST_KEY, mListOfReviews);
    }

    /**
     * Obtains the movie videos from the themoviedb.org API and populates the view.
     */
    public class FetchMovieVideosTask extends AsyncTask<String, Void, ArrayList<MovieItemVideo>> {

        /**
         * Log identifier for the class.
         */
        private final String LOG_TAG = FetchMovieVideosTask.class.getSimpleName();

        /**
         * Progress dialog when fetching movie data.
         */
        private ProgressDialog mProgressDialog;

        /**
         * Current movie item.
         */
        private MovieItem mMovieItem;

        /**
         * Constructor.
         */
        public FetchMovieVideosTask(MovieItem movieItem) {
            mProgressDialog = new ProgressDialog(getActivity());
            mMovieItem = movieItem;
        }

        /**
         * Extracts the movie video items data from the JSON response from themoviedb.org API.
         *
         * @param movieDataJsonStr JSON response from themoviedb.org.
         * @return list of MovieItemVideo with data.
         * @throws JSONException if an error occurs.
         */
        private ArrayList<MovieItemVideo> getVideoEntriesFromJson(String movieDataJsonStr)
                throws JSONException {

            // Delimiter for movies items.
            final String VIDEO_LIST = "results";
            // Obtains the list of videos.
            JSONObject videosDataJson = new JSONObject(movieDataJsonStr);
            JSONArray videosArray = videosDataJson.getJSONArray(VIDEO_LIST);
            // List which will contain the populated video items.
            ArrayList<MovieItemVideo> videoEntries = new ArrayList<>(videosArray.length());

            // Create video entries
            for (int i = 0; i < videosArray.length(); i++) {
                videoEntries.add(i, new MovieItemVideo(videosArray.getJSONObject(i)));
            }
            return videoEntries;
        }

        @Override
        protected ArrayList<MovieItemVideo> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String videosJsonStr = null;

            try {
                // Construct the URL for the themoviedb.org query.
                final String MOVIEDB_BASE_URL =
                                "http://api.themoviedb.org/3/movie/"
                                + mMovieItem.getmMovieId()
                                +"/videos?";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri =
                        Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_api_key))
                        .build();
                URL url;
                if (builtUri != null) {
                    url = new URL(builtUri.toString());
                } else {
                    Log.e(LOG_TAG, "Error: Invalid URL for themoviedb.org.");
                    return null;
                }

                // Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    Log.w(LOG_TAG, "No movie video data has been fetched from themoviedb.org.");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.w(LOG_TAG, "No movie video data has been fetched from themoviedb.org.");
                    return null;
                }
                videosJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getVideoEntriesFromJson(videosJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItemVideo> result) {
            if (result != null) {
                mMovieVideoAdapter.clear();
                mListOfVideos.clear();
                for (MovieItemVideo videoItem : result) {
                    mListOfVideos.add(videoItem);
                }
                mMovieVideoAdapter.addAll(mListOfVideos);
            }

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Loading Trailers");
            mProgressDialog.show();
        }
    }

    /**
     * Obtains the movie reviews from the themoviedb.org API and populates the view.
     */
    public class FetchMovieReviewsTask extends AsyncTask<String, Void, ArrayList<MovieItemReview>> {

        /**
         * Log identifier for the class.
         */
        private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

        /**
         * Progress dialog when fetching movie data.
         */
        private ProgressDialog mProgressDialog;

        /**
         * Current movie item.
         */
        private MovieItem mMovieItem;

        /**
         * Constructor.
         */
        public FetchMovieReviewsTask(MovieItem movieItem) {
            mProgressDialog = new ProgressDialog(getActivity());
            mMovieItem = movieItem;
        }

        /**
         * Extracts the movie review items data from the JSON response from themoviedb.org API.
         *
         * @param movieDataJsonStr JSON response from themoviedb.org.
         * @return list of MovieItemReview with data.
         * @throws JSONException if an error occurs.
         */
        private ArrayList<MovieItemReview> getReviewEntriesFromJson(String movieDataJsonStr)
                throws JSONException {

            // Delimiter for movies items.
            final String REVIEW_LIST = "results";
            // Obtains the list of reviews.
            JSONObject reviewsDataJson = new JSONObject(movieDataJsonStr);
            JSONArray reviewsArray = reviewsDataJson.getJSONArray(REVIEW_LIST);
            // List which will contain the populated review items.
            ArrayList<MovieItemReview> reviewEntries = new ArrayList<>(reviewsArray.length());

            // Create review entries
            for (int i = 0; i < reviewsArray.length(); i++) {
                reviewEntries.add(i, new MovieItemReview(reviewsArray.getJSONObject(i)));
            }
            return reviewEntries;
        }

        @Override
        protected ArrayList<MovieItemReview> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;

            try {
                // Construct the URL for the themoviedb.org query.
                final String MOVIEDB_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"
                                + mMovieItem.getmMovieId()
                                +"/reviews?";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri =
                        Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                                .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_api_key))
                                .build();
                URL url;
                if (builtUri != null) {
                    url = new URL(builtUri.toString());
                } else {
                    Log.e(LOG_TAG, "Error: Invalid URL for themoviedb.org.");
                    return null;
                }

                // Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    Log.w(LOG_TAG, "No movie review data has been fetched from themoviedb.org.");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.w(LOG_TAG, "No movie review data has been fetched from themoviedb.org.");
                    return null;
                }
                reviewJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewEntriesFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItemReview> result) {
            if (result != null) {
                mMovieReviewAdapter.clear();
                mListOfReviews.clear();
                for (MovieItemReview reviewItem : result) {
                    mListOfReviews.add(reviewItem);
                }
                mMovieReviewAdapter.addAll(mListOfReviews);
            }

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Loading Reviews");
            mProgressDialog.show();
        }
    }
}
