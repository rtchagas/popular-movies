package com.rtchagas.udacity.popularmovies.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.core.MovieSearchResult;
import com.rtchagas.udacity.popularmovies.core.Review;
import com.rtchagas.udacity.popularmovies.core.ReviewSearchResult;
import com.rtchagas.udacity.popularmovies.core.Trailer;
import com.rtchagas.udacity.popularmovies.core.TrailerSearchResult;
import com.rtchagas.udacity.popularmovies.provider.contract.MovieContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void loadMoviesAsync(@NonNull Context context, MovieSort criteria, @NonNull final OnSearchResultListener<Movie> resultListener) {

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
        else if (MovieSort.TOP_RATED == criteria){
            mTmdbApi.getTopRated().enqueue(resultCallback);
        }
        else {
            // From local database
            getFavoriteMovies(context, resultListener);
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

    private void getFavoriteMovies(@NonNull Context context, @NonNull OnSearchResultListener<Movie> resultListener) {

        ContentResolver resolver = context.getContentResolver();

        if (resolver == null) {
            return;
        }

        try {
            Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                    null, null, null, null);

            if (cursor != null) {
                List<Movie> movieList = getMoviesFromCursor(cursor);
                resultListener.onResultReady(movieList);
                cursor.close();
            }
        }
        catch (RuntimeException ex) {
            // Catch any RuntimeException and pass to result listener
            resultListener.onResultError(ex.getMessage());
        }
    }

    private List<Movie> getMoviesFromCursor(Cursor cursor) {

        ArrayList<Movie> movieList = new ArrayList<>();

        while (cursor.moveToNext()) {

            // Fill a new Movie object with each cursor entry.
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.ORIGINAL_TITLE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.BACKDROP_PATH)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW)));
            movie.setReleaseDate(new Date(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE))));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.VOTE_AVERAGE)));

            // Add the movie to the list.
            movieList.add(movie);
        }

        return movieList;
    }

    public boolean isFavoriteMovie(@NonNull Context context, int movieId) {

        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            Cursor cursor = resolver.query(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId),
                    new String[] {MovieContract.MovieEntry._ID}, null, null, null);

            if (cursor != null) {
                try {
                    return (cursor.getCount() > 0);
                }
                finally {
                    cursor.close();
                }
            }
        }

        return false;
    }

    @Nullable
    public Uri addFavoriteMovie(@NonNull Context context, @NonNull Movie movie) {

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry._ID, movie.getId());
        values.put(MovieContract.MovieEntry.TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(MovieContract.MovieEntry.POSTER_PATH, movie.getPosterPath());
        values.put(MovieContract.MovieEntry.BACKDROP_PATH, movie.getBackdropPath());
        values.put(MovieContract.MovieEntry.OVERVIEW, movie.getOverview());
        values.put(MovieContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate().getTime());
        values.put(MovieContract.MovieEntry.VOTE_AVERAGE, movie.getVoteAverage());

        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            return resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
        }

        return null;
    }

    public boolean removeFavoriteMovie(@NonNull Context context, int movieId) {

        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            int rows = resolver.delete(
                    ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId),
                    null, null);
            return (rows > 0);
        }

        return false;
    }

    public enum MovieSort {
        POPULARITY,
        TOP_RATED,
        FAVORITES;

        public static MovieSort from(int ordinal) {
            return values()[ordinal];
        }
    }
}
