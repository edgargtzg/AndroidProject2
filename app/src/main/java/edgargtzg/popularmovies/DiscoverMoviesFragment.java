package edgargtzg.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Fragment contains the popular movies discovery features.
 */
public class DiscoverMoviesFragment extends Fragment {

    private MovieItemAdapter mMoviePosterAdapter;

    public DiscoverMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The MovieItemAdapter will take data from a source and
        // use it to populate the GridView it's attached to.

        mMoviePosterAdapter =
                new MovieItemAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.movie_grid_item_imageview, // The name of the grid item ID.
                        new ArrayList<MovieItem>());


        View rootView = inflater.inflate(R.layout.discover_movies_layout, container, false);


        // Get a reference to the GridView, and attach this adapter to it.
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


        return rootView;
    }

    /**
     * Updates the content of the movies discovery based on the selected sort by
     * option by the User.
     *
     * @param sortById id of the action to fetch/sort the movies
     *                 (for example: most popular, highest-rated)
     */
    private void updateMovies(String sortById) {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(sortById);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMovies(
                PreferenceManager.getDefaultSharedPreferences(
                        getActivity()).getString(
                        getString(R.string.pref_sortBy_list_key),
                        getString(R.string.pref_most_popular)));
    }

    /**
     * Obtains the movies data from the themoviedb.org API and populates the grid view.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Obtains the movie entries data from the JSON response from themoviedb.org API.
         */
        private ArrayList<MovieItem> getMovieEntriesFromJson(String movieDataJsonStr)
                throws JSONException {

            // Name of the movie list entry.
            final String MOVIE_LIST = "results";
            JSONObject moviesDataJson = new JSONObject(movieDataJsonStr);
            JSONArray moviesArray = moviesDataJson.getJSONArray(MOVIE_LIST);

            ArrayList<MovieItem> movieEntries = new ArrayList<MovieItem>(moviesArray.length());

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
                // Construct the URL for the themoviedb.org query
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

                URL url = new URL(builtUri.toString());

                // Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                for (MovieItem movieItem : result) {
                    mMoviePosterAdapter.add(movieItem);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

}