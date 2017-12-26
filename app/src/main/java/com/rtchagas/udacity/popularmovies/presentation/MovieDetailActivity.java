package com.rtchagas.udacity.popularmovies.presentation;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.TmdbAPI;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // Initializes the action bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the movie from the incoming intent
        if ((getIntent().getExtras() != null) && getIntent().getExtras().containsKey(EXTRA_MOVIE)) {
            Movie movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);
            fillMovieDetails(movie);
        }
        else {
            // Wrong intent..
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Back button just finishes this activity
        if (android.R.id.home == item.getItemId()) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    }
}
