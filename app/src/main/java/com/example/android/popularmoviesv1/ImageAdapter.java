package com.example.android.popularmoviesv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> mMovies;

    public ImageAdapter(Context c, List<Movie> movies) {
        mContext = c;
        mMovies = movies;
    }


    public int getCount() {

        return mMovies.size();

    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = (ImageView) LayoutInflater
                    .from(this.mContext)
                    .inflate(R.layout.movie_cell, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }

        String title = getItem(position).getPosterUrl();

        Picasso.with(mContext).load(title).placeholder(R.drawable.blank_film_poster).into(imageView);

        return imageView;
    }

    public void updateValues(List<Movie> newMovies) {
        mMovies = newMovies;
        notifyDataSetChanged();
    }


}
