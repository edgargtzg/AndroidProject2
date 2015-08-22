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

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Activity which provides details of a movie.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_activity_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(
                    PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    SettingsActivity.PrefsDiscoverMovies.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Movie details fragment which populates the different view components.
     */
    public static class DetailFragment extends Fragment {

        /**
         * Default constructor.
         */
        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

            // The detail Activity called via intent.  Inspect the intent for movie data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MovieItem.class.getCanonicalName())) {
                Bundle movieData = intent.getExtras();
                MovieItem movieItem = movieData.getParcelable(
                        MovieItem.class.getCanonicalName());
                if (movieItem != null) {
                    String value;
                    // Populates movie original title text view value.
                    value = movieItem.getOriginalTitle();
                    if (!(value.isEmpty())) {
                        TextView originalTitleTextView =
                                (TextView) rootView.findViewById(R.id.movie_title_textView);
                        originalTitleTextView.setText(value);
                    }

                    // Populates movie poster image.
                    value = movieItem.getMoviePoster();
                    if (!(value.isEmpty())) {
                        ImageView moviePosterImageView =
                                (ImageView) rootView.findViewById(R.id.movie_poster_imageView);
                        Picasso.with(getActivity()).load(
                                rootView.getResources().getString(R.string.details_poster_api_call) +
                                        value).into(moviePosterImageView);
                    }

                    // Populates movie plot synopsis.
                    value = movieItem.getPlotSynopsis();
                    if (!(value.isEmpty())) {
                        TextView moviePlotTextView =
                                (TextView) rootView.findViewById(R.id.movie_plot_textView);
                        moviePlotTextView.setText(value);
                    }

                    // Populates movie user rating.
                    value = movieItem.getUserRating();
                    if (!(value.isEmpty())) {
                        RatingBar movieRateBar = (RatingBar) rootView.findViewById(R.id.movie_ratingBar);
                        movieRateBar.setRating((Float.parseFloat(value) / 2));
                    }

                    // Populates movie release date value.
                    value = movieItem.getReleaseDate();
                    if (!(value.isEmpty())) {
                        TextView movieReleaseTextView =
                                (TextView) rootView.findViewById(R.id.movie_release_textView);
                        movieReleaseTextView.setText(value);
                    }
                }
            }
            return rootView;
        }

    }
}
