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
public interface TmdbAPI {

    String BASE_URL = "https://api.themoviedb.org/3/";
    String BASE_IMG_THUMB_URL = "http://image.tmdb.org/t/p/w342/";
    String BASE_IMG_BACKDROP_URL = "http://image.tmdb.org/t/p/w500/";

    @GET("movie/popular?api_key=" + Config.TMDB_API_KEY)
    Call<SearchResult> getPopular();

    @GET("movie/top_rated?api_key=" + Config.TMDB_API_KEY)
    Call<SearchResult> getTopRated();

    @GET("/movie/{id}?api_key=" + Config.TMDB_API_KEY)
    Call<Movie> getMovieDetails(@Path("id") int id);
}
