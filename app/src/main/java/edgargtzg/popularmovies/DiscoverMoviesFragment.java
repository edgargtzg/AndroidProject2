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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
 * Fragment contains the discover movies functionality which populates the view based on the
 * selected sort order in Settings.
 */
public class DiscoverMoviesFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Adapter to populate grid view with movie items.
     */
    private MovieItemAdapter mMoviePosterAdapter;

    /**
     * Contains the list of movie items.
     */
    private ArrayList<MovieItem> mListOfMovies;

    /**
     * Movie list key to use when saving state of the activity.
     */
    private static final String MOVIE_LIST_KEY = "MOVIE_LIST_KEY";

    /**
     * Default constructor.
     */
    public DiscoverMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fragment to handle menu events.
        setHasOptionsMenu(true);
        // The MovieItemAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        mMoviePosterAdapter =
                new MovieItemAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.grid_movie_item,
                        new ArrayList<MovieItem>());
        if (savedInstanceState != null) {
            mListOfMovies = (ArrayList<MovieItem>) savedInstanceState.get(MOVIE_LIST_KEY);
            if (mListOfMovies != null) {
                mMoviePosterAdapter.addAll(mListOfMovies);
            }
        } else {
            mListOfMovies = new ArrayList<>();
            updateMovies(PreferenceManager.getDefaultSharedPreferences(
                    getActivity()).getString(
                    getString(R.string.pref_sortBy_list_key),
                    getString(R.string.pref_most_popular)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_discover_movies, container, false);

        // Get a reference to the GridView, and attach the adapter to it.
        GridView gridView  = (GridView) rootView.findViewById(R.id.movies_discovery_gridview);
        gridView.setAdapter(mMoviePosterAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieItem movieItem = mMoviePosterAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                        .putExtra(MovieItem.class.getCanonicalName(), movieItem);
                startActivity(intent);
            }
        });

        // Registers the preference listener to populate movies.
        PreferenceManager.getDefaultSharedPreferences(
                getActivity()).registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    /**
     * Updates the content of the view based on the selected sort by
     * option by the User.
     *
     * @param sortById id of the action to sort the movies
     *                 (for example: most popular, highest-rated)
     */
    private void updateMovies(String sortById) {

        if (isNetworkAvailable()) {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sortById);
        } else {
            Toast toast = Toast.makeText(
                    getActivity(), R.string.error_msg_no_network, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mListOfMovies);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregisters the preference listener to populate movies.
        PreferenceManager.getDefaultSharedPreferences(
                getActivity()).unregisterOnSharedPreferenceChangeListener(this);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateMovies(sharedPreferences.getString(key,getString(R.string.pref_most_popular)));
    }

    /**
     * Obtains the movies data from the themoviedb.org API and populates the view.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        /**
         * Log identifier for the class.
         */
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Progress dialog when fetching movie data.
         */
        private ProgressDialog progressDialog;

        /**
         * Constructor.
         */
        public FetchMoviesTask() {

            progressDialog = new ProgressDialog(getActivity());

        }

        /**
         * Extracts the movie items data from the JSON response from themoviedb.org API.
         *
         * @param movieDataJsonStr JSON response from themoviedb.org.
         * @return list of MovieItems with data.
         * @throws JSONException if an error occurs.
         */
        private ArrayList<MovieItem> getMovieEntriesFromJson(String movieDataJsonStr)
                throws JSONException {

            // Delimiter for movies items.
            final String MOVIE_LIST = "results";
            // Obtains the list of movies.
            JSONObject moviesDataJson = new JSONObject(movieDataJsonStr);
            JSONArray moviesArray = moviesDataJson.getJSONArray(MOVIE_LIST);
            // List which will contain the populated move items.
            ArrayList<MovieItem> movieEntries = new ArrayList<>(moviesArray.length());

            // Create movie entries
            for (int i = 0; i < moviesArray.length(); i++) {
                movieEntries.add(i,new MovieItem(moviesArray.getJSONObject(i)));
            }
            return movieEntries;
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            // Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the themoviedb.org query.
                final String MOVIEDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY_PARAM = "api_key";
                final String SORT_BY_PARAM = "sort_by";
                final String MOST_POPULAR = "popularity.desc";
                final String HIGHEST_RATED = "vote_average.desc";

                Uri builtUri = null;

                if(params[0].equalsIgnoreCase(getString(R.string.pref_most_popular))) {
                    builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_api_key))
                            .appendQueryParameter(SORT_BY_PARAM, MOST_POPULAR)
                            .build();
                } else if (params[0].equalsIgnoreCase(getString(R.string.pref_highest_rated))){
                    builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_api_key))
                            .appendQueryParameter(SORT_BY_PARAM, HIGHEST_RATED)
                            .build();
                } else {
                    Log.e(LOG_TAG, "Error: Invalid preference option to sort movies.");
                }

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
                    Log.w(LOG_TAG, "No movie data has been fetched from themoviedb.org.");
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
                    Log.w(LOG_TAG, "No movie data has been fetched from themoviedb.org.");
                    return null;
                }
                moviesJsonStr = buffer.toString();
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
                return getMovieEntriesFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> result) {
            if (result != null) {
                mMoviePosterAdapter.clear();
                mListOfMovies.clear();
                for (MovieItem movieItem : result) {
                    mListOfMovies.add(movieItem);
                }
                mMoviePosterAdapter.addAll(mListOfMovies);
            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading Movies");
            progressDialog.show();
        }
    }

}
