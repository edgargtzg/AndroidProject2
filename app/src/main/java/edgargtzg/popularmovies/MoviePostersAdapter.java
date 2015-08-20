package edgargtzg.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter to populate the grid items with the movie poster images.
 */
public class MoviePostersAdapter extends ArrayAdapter{

    private Context mAdapterContext;
    private ArrayList<MovieEntry> mMovieEntries;

    public MoviePostersAdapter(Context context, int gridItemId, ArrayList<MovieEntry> movieEntries) {
        super(context, gridItemId, movieEntries);
        mAdapterContext = context;
        mMovieEntries = movieEntries;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = ((Activity) mAdapterContext).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.grid_item_movie_poster, parent, false);
        }

        ImageView imageView =  (ImageView) convertView.findViewById(
                R.id.grid_item_movie_poster_imageview);
        Picasso.with(mAdapterContext).load(
                convertView.getResources().getString(R.string.movie_poster_api_call) +
                mMovieEntries.get(position).getMoviePoster()).into(imageView);

        return convertView;
    }

    @Override
    public MovieEntry getItem(int position) {
        return mMovieEntries.get(position);
    }
}
