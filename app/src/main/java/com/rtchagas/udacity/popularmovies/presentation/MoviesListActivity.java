package com.rtchagas.udacity.popularmovies.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.controller.OnMovieSearchResultListener;
import com.rtchagas.udacity.popularmovies.core.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements OnMovieSearchResultListener {

    @BindView(R.id.tv_test) TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        MovieController movieController = MovieController.getInstance();

        movieController.loadMovies(MovieController.MovieSort.POPULARITY, this);
    }

    /**
     * Implementation of {@link OnMovieSearchResultListener}
     */
    @Override
    public void onResultReady(List<Movie> movieList) {

        if (movieList != null) {
            for (Movie movie : movieList) {
                tvTest.append(movie.getTitle() + "\n\n");
            }
        }
    }

    /**
     * Implementation of {@link OnMovieSearchResultListener}
     */
    @Override
    public void onResultError(String message) {
        tvTest.setText("Chupou, deu pau!");
    }
}
