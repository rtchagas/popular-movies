package com.rtchagas.udacity.popularmovies.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.Config;
import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.controller.OnSearchResultListener;
import com.rtchagas.udacity.popularmovies.controller.TmdbAPI;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.core.Review;
import com.rtchagas.udacity.popularmovies.core.Trailer;
import com.rtchagas.udacity.popularmovies.presentation.adapter.ReviewAdapter;
import com.rtchagas.udacity.popularmovies.presentation.adapter.TrailerAdapter;
import com.rtchagas.udacity.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    protected static final String EXTRA_MOVIE = "movie";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.iv_movie_backdrop)
    ImageView mIvMovieBackdrop;

    @BindView(R.id.iv_movie_poster)
    ImageView mIvMoviePoster;

    @BindView(R.id.tv_movie_release_year)
    TextView mTvMovieReleaseDate;

    @BindView(R.id.tv_movie_rating)
    TextView mTvMovieRating;

    @BindView(R.id.rb_movie_rating)
    RatingBar mRbMovieRating;

    @BindView(R.id.tv_movie_overview)
    TextView mTvMovieOverview;

    @BindView(R.id.rv_movie_trailers)
    RecyclerView mRvTrailers;

    @BindView(R.id.pb_trailers)
    ProgressBar mPbTrailers;

    @BindView(R.id.tv_trailers_info)
    TextView mTvTrailersInfo;

    @BindView(R.id.rv_movie_reviews)
    RecyclerView mRvReviews;

    @BindView(R.id.pb_reviews)
    ProgressBar mPbReviews;

    @BindView(R.id.tv_reviews_info)
    TextView mTvReviewsInfo;

    @BindView(R.id.bt_favorite)
    Button mBtFavorite;

    /**
     * The current movie being displayed.
     */
    private Movie mCurrentMovie = null;

    /**
     * The movie's first trailer (if available).
     */
    private Trailer mFirstTrailer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // Initializes the action bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if ((getIntent().getExtras() != null) && getIntent().getExtras().containsKey(EXTRA_MOVIE)) {

            // Get the movie from the incoming intent and store it for future access.
            mCurrentMovie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

            // Fill the details
            fillMovieDetails(mCurrentMovie);
        }
        else {
            // Wrong intent..
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Back/home button just finishes this activity
        if (android.R.id.home == item.getItemId()) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        // Share action
        if (R.id.menu_item_share == item.getItemId()) {
            Intent shareIntent = getTrailerShareIntent();
            if (shareIntent != null) {
                startActivity(shareIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if ((mCurrentMovie != null) && (mFirstTrailer != null)) {
            // Share option should be available
            menu.findItem(R.id.menu_item_share).setVisible(true);
        }
        else {
            menu.findItem(R.id.menu_item_share).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void fillMovieDetails(Movie movie) {

        // Set movie backdrop
        String imgUrl = TmdbAPI.BASE_IMG_BACKDROP_URL + movie.getBackdropPath();
        Picasso.with(this).load(Uri.parse(imgUrl)).into(mIvMovieBackdrop);

        // Movie original title
        mCollapsingToolbarLayout.setTitle(movie.getOriginalTitle());

        // Movie poster
        imgUrl = TmdbAPI.BASE_IMG_THUMB_URL + movie.getPosterPath();
        Picasso.with(this).load(Uri.parse(imgUrl)).into(mIvMoviePoster);

        // Movie release date
        String dateStr = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
                .format(movie.getReleaseDate());
        mTvMovieReleaseDate.setText(dateStr);

        // Movie rating
        mTvMovieRating.setText(getString(R.string.movie_detail_rating, movie.getVoteAverage()));
        mRbMovieRating.setRating((float) (movie.getVoteAverage() / 2d));

        // Movie overview
        mTvMovieOverview.setText(movie.getOverview());

        // Favorite
        initFavoriteButton();

        // Movie trailers
        initMovieTrailers();

        // Movie reviews
        initMovieReviews();
    }

    private void initFavoriteButton() {

        if (!isMovieFavorite()) {
            updateFavoriteButtonState(false);
        }
        else {
            updateFavoriteButtonState(true);
        }
    }

    private void showFavoritesSnack(final boolean isFavorite) {

        int msgId = (isFavorite ? R.string.movie_detail_add_favorite_success
                : R.string.movie_detail_del_favorite_success);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                msgId, Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    delMovieFromFavorites(false);
                }
                else {
                    addMovieToFavorites(false);
                }
            }
        });

        snackbar.show();
    }

    private void addMovieToFavorites(boolean showSnack) {

        boolean success = (MovieController.getInstance()
                .addFavoriteMovie(this, mCurrentMovie) != null);

        if (success) {
            updateFavoriteButtonState(true);
            if (showSnack) {
                showFavoritesSnack(true);
            }
        }
        //TODO: Maybe add a snack for this error...
    }

    private void delMovieFromFavorites(boolean showSnackOnSuccess) {
        boolean success = MovieController.getInstance()
                .removeFavoriteMovie(this, mCurrentMovie.getId());

        if (success) {
            updateFavoriteButtonState(false);
            if (showSnackOnSuccess) {
                showFavoritesSnack(false);
            }
        }
        //TODO: Maybe add a snack for this error...
    }

    private void updateFavoriteButtonState(boolean isFavorite) {

        int textId = (isFavorite ? R.string.movie_detail_is_favorite
                : R.string.movie_detail_add_favorite);
        int drawableId = (isFavorite ? R.drawable.ic_favorite_dark_24dp
                : R.drawable.ic_favorite_24dp);

        mBtFavorite.setText(textId);
        mBtFavorite.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mBtFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0);
        }

        if (!isFavorite) {
            mBtFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMovieToFavorites(true);
                }
            });
        }
        else {
            mBtFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delMovieFromFavorites(true);
                }
            });
        }
    }

    private boolean isMovieFavorite() {
        return MovieController.getInstance()
                .isFavoriteMovie(this, mCurrentMovie.getId());
    }

    private void initMovieTrailers() {

        TrailerAdapter trailerAdapter = new TrailerAdapter();
        mRvTrailers.setAdapter(trailerAdapter);
        mRvTrailers.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRvTrailers.setHasFixedSize(true);

        // Set some nice item sp_divider_horizontal
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.sp_divider_horizontal));
        mRvTrailers.addItemDecoration(itemDecorator);

        // Load the trailers in the background
        loadTrailersAsync();
    }

    private void loadTrailersAsync() {

        // We need internet :)
        if (!NetworkUtils.isInternetAvailable(this)) {
            // Show the text view warning the user
            setTrailersProgressView(false);
            mTvTrailersInfo.setVisibility(View.VISIBLE);
            mTvTrailersInfo.setText(getString(R.string.movie_trailers_offline));
            return;
        }

        int movieId = mCurrentMovie.getId();

        MovieController.getInstance().getTrailersAsync(movieId, new OnSearchResultListener<Trailer>() {
            @Override
            public void onResultReady(@Nullable List<Trailer> trailerList) {
                // Hide the progress bar
                setTrailersProgressView(false);

                if ((trailerList != null) && (trailerList.size() > 0)) {
                    // Update the adapter
                    ((TrailerAdapter) mRvTrailers.getAdapter()).setTrailers(trailerList);
                    mTvTrailersInfo.setVisibility(View.GONE);

                    // Save the first trailer for sharing
                    mFirstTrailer = trailerList.get(0);
                }
                else {
                    // No trailers...
                    mTvTrailersInfo.setVisibility(View.VISIBLE);
                    mTvTrailersInfo.setText(getString(R.string.movies_trailers_empty));
                }

                // Invalidate the option menu to enable or disable the trailer sharing
                if (getSupportActionBar() != null) {
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onResultError(@Nullable String message) {

                // Just show a snack...
                showTrailersTryAgainSnack(R.string.movies_trailers_loading_error);

                // Invalidate the option menu to enable or disable the trailer sharing
                if (getSupportActionBar() != null) {
                    invalidateOptionsMenu();
                }
            }
        });

        // Set the UI to indicate that the trailers are being loaded.
        setTrailersProgressView(true);
    }

    private void setTrailersProgressView(boolean isLoading) {
        mPbTrailers.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showTrailersTryAgainSnack(int msgId) {

        // Hide the loading progress
        setTrailersProgressView(false);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                msgId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTrailersAsync();
            }
        });

        snackbar.show();
    }

    private Intent getTrailerShareIntent() {

        if ((mCurrentMovie == null) || (mFirstTrailer == null)) {
            return null;
        }

        String url = String.format(Config.URL_YOUTUBE_WATCH, mFirstTrailer.getKey());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.movies_trailers_sharing_subject, mCurrentMovie.getTitle()));
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");

        return Intent.createChooser(sendIntent, getResources().getText(R.string.movies_trailers_sharing_chooser));
    }

    private void initMovieReviews() {

        ReviewAdapter trailerAdapter = new ReviewAdapter();
        mRvReviews.setAdapter(trailerAdapter);
        mRvReviews.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRvReviews.setHasFixedSize(true);
        mRvReviews.setNestedScrollingEnabled(false);


        // Set some nice vertical divider
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.sp_divider_vertical));
        mRvReviews.addItemDecoration(itemDecorator);

        // Load the reviews in the background
        loadReviewsAsync();
    }

    private void loadReviewsAsync() {

        // We need internet :)
        if (!NetworkUtils.isInternetAvailable(this)) {
            // Show the text view warning the user
            setReviewsProgressView(false);
            mTvReviewsInfo.setVisibility(View.VISIBLE);
            mTvReviewsInfo.setText(getString(R.string.movie_reviews_offline));
            return;
        }

        int movieId = mCurrentMovie.getId();

        MovieController.getInstance().getReviewsAsync(movieId, new OnSearchResultListener<Review>() {
            @Override
            public void onResultReady(@Nullable List<Review> reviewList) {
                // Hide the progress bar
                setReviewsProgressView(false);

                if ((reviewList != null) && (reviewList.size() > 0)) {
                    ((ReviewAdapter) mRvReviews.getAdapter()).setReviews(reviewList);
                    mTvReviewsInfo.setVisibility(View.GONE);
                }
                else {
                    // No reviews...
                    mTvReviewsInfo.setVisibility(View.VISIBLE);
                    mTvReviewsInfo.setText(getString(R.string.movies_reviews_empty));
                }
            }

            @Override
            public void onResultError(@Nullable String message) {
                // Just show a snack...
                showReviewsTryAgainSnack(R.string.movies_trailers_loading_error);
            }
        });

        // Set the UI to indicate that the trailers are being loaded.
        setReviewsProgressView(true);
    }

    private void setReviewsProgressView(boolean isLoading) {
        mPbReviews.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showReviewsTryAgainSnack(int msgId) {

        // Hide the loading progress
        setReviewsProgressView(false);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),
                msgId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadReviewsAsync();
            }
        });

        snackbar.show();
    }
}
