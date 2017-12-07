package com.rtchagas.udacity.popularmovies.presentation;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.controller.OnMovieSearchResultListener;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.presentation.adapter.MovieAdapter;
import com.rtchagas.udacity.popularmovies.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements OnMovieSearchResultListener {

    private static final String STATE_KEY_MOVIE_LIST = "movie_list";
    private static final String STATE_KEY_SORT_ORDER = "sort_order";

    @BindView(R.id.group_movie_list_progress)
    ViewGroup mGroupMovieListProgress;

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    private List<Movie> mMovieList = null;
    private MovieAdapter mAdapter = null;

    private MovieController.MovieSort mCurrentSortOrder = null;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        mAdapter = new MovieAdapter();

        // Configure the Recycler View
        mMovieRecyclerView.setAdapter(mAdapter);
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.movies_list_columns)));
        mMovieRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) {

            // Restore the current sort order
            int value = savedInstanceState.getInt(STATE_KEY_SORT_ORDER);
            mCurrentSortOrder = MovieController.MovieSort.from(value);

            // Restore the current movies list.
            onResultReady((ArrayList<Movie>) savedInstanceState
                    .getSerializable(STATE_KEY_MOVIE_LIST));
        }
        else {
            if (NetworkUtils.isInternetAvailable(this)) {
                loadMoviesAsync(MovieController.MovieSort.POPULARITY);
            }
            else {
                showTryAgainSnack(R.string.no_internet);
            }
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
                loadMoviesAsync(MovieController.MovieSort.POPULARITY);
                return true;
            case R.id.menu_movies_list_item_rating:
                loadMoviesAsync(MovieController.MovieSort.TOP_RATED);
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
        outState.putInt(STATE_KEY_SORT_ORDER, mCurrentSortOrder.ordinal());
    }

    private void loadMoviesAsync(MovieController.MovieSort newOrder) {

        // Avoid searching for the same content
        if (mCurrentSortOrder == newOrder) {
            return;
        }

        mCurrentSortOrder = newOrder;

        MovieController movieController = MovieController.getInstance();
        movieController.loadMoviesAsync(mCurrentSortOrder, this);

        // Set the UI to indicate that the movies are being loaded.
        setProgressView(true);
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

        // Hide the loading progress
        setProgressView(false);

        // Just show a snack..
        showTryAgainSnack(R.string.movies_loading_error);
    }

    private void setProgressView(boolean isLoading) {
        mGroupMovieListProgress.setVisibility((isLoading ? View.VISIBLE : View.GONE));
        //mMovieRecyclerView.setVisibility((isLoading ? View.GONE : View.VISIBLE));
    }

    private void showTryAgainSnack(int msgId) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                msgId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoviesAsync(mCurrentSortOrder);
            }
        });

        snackbar.show();
    }
}
