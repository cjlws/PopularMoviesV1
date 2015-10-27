package com.example.android.popularmoviesv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;

public class MainActivityFragment extends Fragment {

    private static final String TAG = "MainActivityFragment";
    private ImageAdapter imageAdapter;

    private static final int PREF_SORT_BY_RATING = 0;
    private static final int PREF_SORT_BY_POPULARITY = 1;
    private static final String PREF_SORT_ORDER_KEY = "sort_order";


    public MainActivityFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_order_by_popular:
                actionRequestedSortOrder(PREF_SORT_BY_POPULARITY);
                break;
            case R.id.action_order_by_rating:
                actionRequestedSortOrder(PREF_SORT_BY_RATING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionRequestedSortOrder(int requestedSortOrder) {
        if (requestedSortOrder != getPreferredSortOrder()) {
            storePreferredSortOrder(requestedSortOrder);
            gatherMovies();
        } else {
            Log.d(TAG, "Existing order selected - do nothing");
        }
    }

    private int getPreferredSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getInt(PREF_SORT_ORDER_KEY, PREF_SORT_BY_POPULARITY);
    }

    private String getSortOrderArgument() {
        int storedSortOrder = getPreferredSortOrder();
        String apiString;
        switch (storedSortOrder) {
            case PREF_SORT_BY_POPULARITY:
                apiString = "popularity.desc";
                break;
            case PREF_SORT_BY_RATING:
                apiString = "vote_average.desc";
                break;
            default:
                Log.d(TAG, "Default Sort Order Applied");
                apiString = "popularity.desc";
        }
        return apiString;
    }

    private void storePreferredSortOrder(int sortOrder) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_SORT_ORDER_KEY, sortOrder);
        editor.commit();
    }

    private void gatherMovies() {
        new DownloadFavouriteMoviesTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageAdapter = new ImageAdapter(getActivity(), getDefaultMovies());
        gatherMovies();
    }

    public List<Movie> getDefaultMovies() {
        List<Movie> movies = new ArrayList<>();
        return movies;
    }

    @Override
    public void onStart() {
        super.onStart();
        gatherMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setEmptyView(view.findViewById(R.id.empty));
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetail.class);
                intent.putExtra(Constants.MOVIE_EXTRA_KEY, movie);
                startActivity(intent);
            }
        });

        return view;
    }

    public class DownloadFavouriteMoviesTask extends AsyncTask<String, Void, List<Movie>> {


        private List<Movie> getMovieDataaFromJSON(String movieJSONStr)
                throws JSONException {

            final String ORIGINAL_TITLE_KEY = "original_title";
            final String VOTE_AVERAGE_KEY = "vote_average";
            final String RELEASE_DATE_KEY = "release_date";
            final String OVERVIEW_KEY = "overview";
            final String POSTER_PATH_KEY = "poster_path";
            final String RESULTS_kEY = "results";
            final String ID = "id";

            JSONObject moviesJSON = new JSONObject(movieJSONStr);
            JSONArray movieResults = moviesJSON.getJSONArray(RESULTS_kEY);

            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject movieJsonObject = movieResults.getJSONObject(i);
                Movie movie = new Movie(
                        movieJsonObject.getString(ORIGINAL_TITLE_KEY),
                        movieJsonObject.getDouble(VOTE_AVERAGE_KEY),
                        movieJsonObject.getString(RELEASE_DATE_KEY),
                        movieJsonObject.getString(OVERVIEW_KEY),
                        movieJsonObject.getString(POSTER_PATH_KEY),
                        movieJsonObject.getInt(ID));
                movies.add(movie);
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            imageAdapter.updateValues(movies);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moveJSONStr = null;

            try {
                String baseUrl = getString(R.string.url_stub);
                String apiKey = getString(R.string.api_key);

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("sort_by", getSortOrderArgument())
                        .appendQueryParameter("api_key", apiKey).build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moveJSONStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataaFromJSON(moveJSONStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


    }
}

