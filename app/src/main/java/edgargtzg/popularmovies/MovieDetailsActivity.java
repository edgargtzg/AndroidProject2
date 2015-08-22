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

/**
 * Activity which provides details of a movie.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity_layout);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(
                    PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    SettingsActivity.PrefsMoviesSortBy.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MovieItem.class.getCanonicalName())) {
                Bundle movieData = intent.getExtras();
                MovieItem movieItem = (MovieItem) movieData.getParcelable(
                        MovieItem.class.getCanonicalName());




                /*
                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text))
                        .setText(mForecastStr);
                */
            }

            return rootView;
        }

    }
}
