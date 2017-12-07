package com.rtchagas.udacity.popularmovies.controller;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rtchagas.udacity.popularmovies.core.SearchResult;

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

    public void loadMoviesAsync(MovieSort criteria, @NonNull final OnMovieSearchResultListener resultListener) {

        Callback<SearchResult> resultCallback = new Callback<SearchResult>() {

            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.body() != null) {
                    resultListener.onResultReady(response.body().getMovieList());
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
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

    public enum MovieSort {
        POPULARITY,
        TOP_RATED
    }

}
