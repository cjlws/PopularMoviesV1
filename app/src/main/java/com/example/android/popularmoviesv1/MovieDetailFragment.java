package com.example.android.popularmoviesv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    private void populateDetails(View view, Movie movie) {
        ((TextView) view.findViewById(R.id.movie_detail_title)).setText(movie.getTitle());
        ((TextView) view.findViewById(R.id.movie_detail_year)).setText(movie.getReleaseYearAndMonth());
        ((TextView) view.findViewById(R.id.movie_detail_user_rating)).setText(String.valueOf(movie.getUserRating()));
        ((TextView) view.findViewById(R.id.movie_detail_synopsis)).setText(movie.getSynopsis());
        ImageView poster = (ImageView) view.findViewById(R.id.movie_detail_poster);
        Picasso.with(getActivity())
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.blank_film_poster)
                .into(poster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constants.MOVIE_EXTRA_KEY)) {
            Movie movie = intent.getParcelableExtra(Constants.MOVIE_EXTRA_KEY);
            populateDetails(view, movie);
        }
        return view;
    }
}
