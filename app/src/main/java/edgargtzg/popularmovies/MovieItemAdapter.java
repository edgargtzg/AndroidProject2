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
public class MovieItemAdapter extends ArrayAdapter<MovieItem>{

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
            convertView = layoutInflater.inflate(R.layout.grid_movie_item, parent, false);
        }

        ImageView imageView =  (ImageView) convertView.findViewById(
                R.id.grid_item_movie_poster_imageview);
        Picasso.with(mAdapterContext).load(
                convertView.getResources().getString(R.string.grid_poster_api_call) +
                mMovieEntries.get(position).getMoviePoster()).into(imageView);

        return convertView;
    }

    @Override
    public MovieItem getItem(int position) {
        return mMovieEntries.get(position);
    }
}
