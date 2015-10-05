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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to populate a view with movie review items.
 */
public class MovieItemReviewAdapter extends ArrayAdapter<MovieItemReview>{

    private Context mAdapterContext;
    private ArrayList<MovieItemReview> mMovieReviewEntries;

    public MovieItemReviewAdapter(Context context, int gridItemId, ArrayList<MovieItemReview> movieReviewEntries) {
        super(context, gridItemId, movieReviewEntries);
        mAdapterContext = context;
        mMovieReviewEntries = movieReviewEntries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = ((Activity) mAdapterContext).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.list_review_movie_item, parent, false);
        }

        TextView textViewAuthor =  (TextView) convertView.findViewById(
                R.id.list_review_movie_author_textview);
        textViewAuthor.setText(getItem(position).getReviewAuthor());

        TextView textViewContent =  (TextView) convertView.findViewById(
                R.id.list_review_movie_content_textview);
        textViewContent.setText(getItem(position).getReviewContent());

        return convertView;
    }

    @Override
    public MovieItemReview getItem(int position) {
        return mMovieReviewEntries.get(position);
    }
}