package com.rtchagas.udacity.popularmovies.controller;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.core.MovieSearchResult;
import com.rtchagas.udacity.popularmovies.core.Review;
import com.rtchagas.udacity.popularmovies.core.ReviewSearchResult;
import com.rtchagas.udacity.popularmovies.core.Trailer;
import com.rtchagas.udacity.popularmovies.core.TrailerSearchResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieController {

    private static TmdbAPI mTmdbApi = null;
    private static MovieController mInstance = null;
    private MovieController() {
        // Singleton
    }

    public static MovieController getInstance() {
        if (mInstance == null) {
            mInstance = new MovieController();
            initTmdbApi();
        }
        return mInstance;
    }

    private static void initTmdbApi() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TmdbAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mTmdbApi = retrofit.create(TmdbAPI.class);
    }

    public void loadMoviesAsync(MovieSort criteria, @NonNull final OnSearchResultListener<Movie> resultListener) {

        Callback<MovieSearchResult> resultCallback = new Callback<MovieSearchResult>() {

            @Override
            public void onResponse(Call<MovieSearchResult> call, Response<MovieSearchResult> response) {
                if (response.body() != null) {
                    resultListener.onResultReady(response.body().getMovieList());
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResult> call, Throwable t) {
                resultListener.onResultError(t.getMessage());
            }
        };

        if (MovieSort.POPULARITY == criteria) {
            mTmdbApi.getPopular().enqueue(resultCallback);
        }
        else {
            mTmdbApi.getTopRated().enqueue(resultCallback);
        }
    }

    public void getTrailersAsync(int movieId, @NonNull final OnSearchResultListener<Trailer> resultListener) {

        Callback<TrailerSearchResult> resultCallback = new Callback<TrailerSearchResult>() {
            @Override
            public void onResponse(Call<TrailerSearchResult> call, Response<TrailerSearchResult> response) {
                if (response.body() != null) {
                    resultListener.onResultReady(response.body().getTrailers());
                }
            }

            @Override
            public void onFailure(Call<TrailerSearchResult> call, Throwable t) {
                resultListener.onResultError(t.getMessage());
            }
        };

        // Call the API asynchronously.
        mTmdbApi.getTrailers(movieId).enqueue(resultCallback);
    }

    public void getReviewsAsync(int movieId, @NonNull final OnSearchResultListener<Review> resultListener) {

        Callback<ReviewSearchResult> resultCallback = new Callback<ReviewSearchResult>() {
            @Override
            public void onResponse(Call<ReviewSearchResult> call, Response<ReviewSearchResult> response) {
                if (response.body() != null) {
                    resultListener.onResultReady(response.body().getReviews());
                }
            }

            @Override
            public void onFailure(Call<ReviewSearchResult> call, Throwable t) {
                resultListener.onResultError(t.getMessage());
            }
        };

        // Call the API asynchronously.
        mTmdbApi.getReviews(movieId).enqueue(resultCallback);
    }

    public enum MovieSort {
        POPULARITY,
        TOP_RATED;

        public static MovieSort from(int ordinal) {
            return values()[ordinal];
        }
    }
}
