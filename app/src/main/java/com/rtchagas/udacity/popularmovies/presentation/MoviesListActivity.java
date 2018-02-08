package com.rtchagas.udacity.popularmovies.presentation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.Config;
import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.controller.MovieController.MovieSort;
import com.rtchagas.udacity.popularmovies.controller.OnSearchResultListener;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.presentation.adapter.MovieBaseAdapter;
import com.rtchagas.udacity.popularmovies.presentation.adapter.MovieCursorAdapter;
import com.rtchagas.udacity.popularmovies.presentation.adapter.MovieListAdapter;
import com.rtchagas.udacity.popularmovies.presentation.adapter.OnConsumedAllMoviesListener;
import com.rtchagas.udacity.popularmovies.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements OnSearchResultListener<Movie>,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnConsumedAllMoviesListener {

    private static final String STATE_KEY_MOVIE_LIST = "movie_list";
    private static final String STATE_KEY_IS_PAGE_MODE = "is_page_mode";
    private static final String PREF_KEY_SORT_ORDER = "sort_order";

    private static final int ID_FAVORITES_LOADER = 100;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pb_movies)
    ProgressBar mProgressBar;

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.tv_no_movies)
    TextView mTvNoMovies;

    private MovieListAdapter mMovieListAdapter = null;
    private MovieCursorAdapter mMovieCursorAdapter = null;

    private MovieSort mCurrentSortOrder = null;

    private boolean mIsPagedMode = false;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mMovieListAdapter = new MovieListAdapter(this, this);
        mMovieCursorAdapter = new MovieCursorAdapter(this);

        // Configure the RecyclerView
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.movies_list_columns)));
        mMovieRecyclerView.setHasFixedSize(true);

        // Get/Restore the current sort order
        int sortValue = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(PREF_KEY_SORT_ORDER, MovieSort.POPULARITY.ordinal());
        mCurrentSortOrder = MovieSort.from(sortValue);

        // Restore the current movies list, if available
        if ((savedInstanceState != null) && savedInstanceState.containsKey(STATE_KEY_MOVIE_LIST)) {
            onResultReady((ArrayList<Movie>) savedInstanceState
                    .getSerializable(STATE_KEY_MOVIE_LIST));
        }
        else {
            // Load movies locally
            if (MovieSort.FAVORITES == mCurrentSortOrder) {
                initFavoriteMovies();
            }
            // Load movies from cloud
            else {
                loadMoviesAsync(mCurrentSortOrder, true);
            }
        }

        // Init the favorites loader
        getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.menu_movies_list_item_favorites:
                initFavoriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If movies came from cloud, lets store them to make things easier to the user.
        if ((MovieSort.FAVORITES != mCurrentSortOrder)
                && (mMovieListAdapter != null) && (mMovieListAdapter.getData() != null)) {
            List<Movie> movieList = (List<Movie>) mMovieListAdapter.getData();
            outState.putSerializable(STATE_KEY_MOVIE_LIST, new ArrayList<>(movieList));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the current sort order to preferences
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(PREF_KEY_SORT_ORDER, mCurrentSortOrder.ordinal())
                .apply();
    }

    private void loadMoviesAsync(MovieSort newOrder, boolean force) {

        // We need internet :)
        if ((MovieSort.FAVORITES != mCurrentSortOrder)
                && !NetworkUtils.isInternetAvailable(this)) {
            showTryAgainSnack(R.string.no_internet);
            return;
        }

        // Avoid searching for the same content
        if (!force && (mCurrentSortOrder == newOrder)) {
            return;
        }

        // Set the UI to indicate that the movies are being loaded.
        setProgressView(true);

        mCurrentSortOrder = newOrder;

        // Replace the entire adapter
        mIsPagedMode = false;

        MovieController.getInstance()
                .loadMoviesAsync(mCurrentSortOrder, this);
    }

    private void loadMoviesAsync(MovieSort newOrder) {
        loadMoviesAsync(newOrder, false);
    }

    /**
     * Implementation of {@link OnSearchResultListener}
     */
    @Override
    public void onResultReady(@Nullable List<Movie> movieList) {

        if (!mIsPagedMode) {
            // Set the correct adapter to the recycler view
            mMovieRecyclerView.setAdapter(mMovieListAdapter);
        }

        if (movieList != null) {

            // Hide the loading progress
            setProgressView(false);
            // Hide/show the empty view
            setEmptyView(!(movieList.size() > 0));

            // Fill the adapter
            if (mIsPagedMode) {
                mMovieListAdapter.appendMovies(movieList);
            }
            else {
                mMovieListAdapter.swapData(movieList);
            }
        }
    }

    /**
     * Implementation of {@link OnSearchResultListener}
     */
    @Override
    public void onResultError(@Nullable String message) {
        // Just show a snack...
        showTryAgainSnack(R.string.movies_loading_error);
    }

    /**
     * Implementation of {@link android.view.View.OnClickListener}
     */
    @Override
    public void onClick(View v) {

        // Get the position that was clicked
        int position = (int) v.getTag();

        // Get the target movie
        Movie movie = ((MovieBaseAdapter) mMovieRecyclerView.getAdapter()).getMovie(position);

        // Send it to detail activity as extra
        Intent detailIntent = new Intent(this, MovieDetailActivity.class);
        detailIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);

        // Start the MovieDetailActivity
        startActivity(detailIntent);
    }

    private void setProgressView(boolean isLoading) {
        mProgressBar.setVisibility((isLoading ? View.VISIBLE : View.GONE));
    }

    private void setEmptyView(boolean isEmpty) {
        mTvNoMovies.setVisibility((isEmpty ? View.VISIBLE : View.GONE));
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

    private void initFavoriteMovies() {

        // Set Favorites as new sort order
        mCurrentSortOrder = MovieSort.FAVORITES;

        // Set the correct adapter to the recycler view
        mMovieRecyclerView.setAdapter(mMovieCursorAdapter);

        // Hide/show the empty view
        setEmptyView(!(mMovieCursorAdapter.getItemCount() > 0));
    }

    // Cursor Loader callbacks implementation

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_FAVORITES_LOADER:
                return MovieController.getInstance().getFavoritesLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieCursorAdapter.swapData(data);

        // Update the UI only if in favorites view.
        if (MovieSort.FAVORITES == mCurrentSortOrder) {
            // Hide the loading progress
            setProgressView(false);
            // Hide/show the empty view
            setEmptyView(!(data.getCount() > 0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapData(null);
    }

    @Override
    public void onConsumedAllMovies(int size) {

        // Set current behavior to paged mode
        mIsPagedMode = true;

        // Calculate the next page
        int nextPage = (size / Config.TMDB_PAGE_SIZE) + 1;

        setProgressView(true);

        // Fetch the next page
        MovieController.getInstance()
                .loadMoviesAsync(mCurrentSortOrder, nextPage, this);
    }
}
