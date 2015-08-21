package edgargtzg.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter to populate a view with movie items.
 */
public class MovieItemAdapter extends ArrayAdapter{

    private Context mAdapterContext;
    private ArrayList<MovieItem> mMovieEntries;

    public MovieItemAdapter(Context context, int gridItemId, ArrayList<MovieItem> movieEntries) {
        super(context, gridItemId, movieEntries);
        mAdapterContext = context;
        mMovieEntries = movieEntries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = ((Activity) mAdapterContext).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.movie_grid_item_imageview, parent, false);
        }

        ImageView imageView =  (ImageView) convertView.findViewById(
                R.id.grid_item_movie_poster_imageview);
        Picasso.with(mAdapterContext).load(
                convertView.getResources().getString(R.string.themoviedb_poster_api_call) +
                mMovieEntries.get(position).getMoviePoster()).into(imageView);

        return convertView;
    }

    @Override
    public MovieItem getItem(int position) {
        return mMovieEntries.get(position);
    }
}
