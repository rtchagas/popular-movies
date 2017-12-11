package com.rtchagas.udacity.popularmovies.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.controller.MovieController.MovieSort;
import com.rtchagas.udacity.popularmovies.controller.OnMovieSearchResultListener;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.presentation.adapter.MovieAdapter;
import com.rtchagas.udacity.popularmovies.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements OnMovieSearchResultListener, View.OnClickListener {

    private static final String STATE_KEY_MOVIE_LIST = "movie_list";
    private static final String PREF_KEY_SORT_ORDER = "sort_order";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pb_movies)
    ProgressBar mProgressBar;

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    private List<Movie> mMovieList = null;
    private MovieAdapter mAdapter = null;

    private MovieSort mCurrentSortOrder = null;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mAdapter = new MovieAdapter(this);

        // Configure the Recycler View
        mMovieRecyclerView.setAdapter(mAdapter);
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.movies_list_columns)));
        mMovieRecyclerView.setHasFixedSize(true);

        // Get/Restore the current sort order
        int sortValue = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(PREF_KEY_SORT_ORDER, MovieSort.POPULARITY.ordinal());
        mCurrentSortOrder = MovieSort.from(sortValue);

        if (savedInstanceState != null) {
            // Restore the current movies list.
            onResultReady((ArrayList<Movie>) savedInstanceState
                    .getSerializable(STATE_KEY_MOVIE_LIST));
        }
        else {
            loadMoviesAsync(mCurrentSortOrder, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_movies_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_movies_list_item_popular:
                loadMoviesAsync(MovieSort.POPULARITY);
                return true;
            case R.id.menu_movies_list_item_rating:
                loadMoviesAsync(MovieSort.TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovieList != null) {
            outState.putSerializable(STATE_KEY_MOVIE_LIST, new ArrayList<>(mMovieList));
        }
    }

    private void loadMoviesAsync(MovieSort newOrder, boolean force) {

        // We need internet :)
        if (!NetworkUtils.isInternetAvailable(this)) {
            showTryAgainSnack(R.string.no_internet);
            return;
        }

        // Avoid searching for the same content
        if (!force && (mCurrentSortOrder == newOrder)) {
            return;
        }

        mCurrentSortOrder = newOrder;
        // Save this order to preferences
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(PREF_KEY_SORT_ORDER, mCurrentSortOrder.ordinal())
                .apply();

        MovieController movieController = MovieController.getInstance();
        movieController.loadMoviesAsync(mCurrentSortOrder, this);

        // Set the UI to indicate that the movies are being loaded.
        setProgressView(true);
    }

    private void loadMoviesAsync(MovieSort newOrder) {
        loadMoviesAsync(newOrder, false);
    }

    /**
     * Implementation of {@link OnMovieSearchResultListener}
     */
    @Override
    public void onResultReady(List<Movie> movieList) {

        if (movieList != null) {

            mMovieList = movieList;

            // Hide the loading progress
            setProgressView(false);

            // Fill the adapter
            mAdapter.setMovies(movieList);
        }
    }

    /**
     * Implementation of {@link OnMovieSearchResultListener}
     */
    @Override
    public void onResultError(String message) {
        // Just show a snack..
        showTryAgainSnack(R.string.movies_loading_error);
    }

    @Override
    /**
     * Implementation of {@link android.view.View.OnClickListener}
     */
    public void onClick(View v) {

        // Get the position that was clicked
        int position = (int) v.getTag();

        // Get the target movie
        Movie movie = mMovieList.get(position);

        // Send it to detail activity as extra
        Intent detailIntent = new Intent(this, MovieDetailActivity.class);
        detailIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);

        // Start the MovieDetailActivity
        startActivity(detailIntent);
    }

    private void setProgressView(boolean isLoading) {
        mProgressBar.setVisibility((isLoading ? View.VISIBLE : View.GONE));
        // Looked better to keep the recyclerview visible while loading other results...
        //mMovieRecyclerView.setVisibility((isLoading ? View.GONE : View.VISIBLE));
    }

    private void showTryAgainSnack(int msgId) {

        // Hide the loading progress
        setProgressView(false);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                msgId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoviesAsync(mCurrentSortOrder, true);
            }
        });

        snackbar.show();
    }
}
