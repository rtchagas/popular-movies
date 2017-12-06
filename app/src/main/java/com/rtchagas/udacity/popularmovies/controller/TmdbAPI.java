package com.rtchagas.udacity.popularmovies.controller;

import com.rtchagas.udacity.popularmovies.Config;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.core.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface to The Movie Database API REST services
 */
interface TmdbAPI {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/popular?api_key=" + Config.TMDB_API_KEY)
    Call<SearchResult> getPopular();

    @GET("movie/top_rated?api_key=" + Config.TMDB_API_KEY)
    Call<SearchResult> getTopRated();

    @GET("/movie/{id}?api_key=" + Config.TMDB_API_KEY)
    Call<Movie> getMovieDetails(@Path("id") int id);
}
