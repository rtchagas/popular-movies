package com.rtchagas.udacity.popularmovies.controller;

import com.rtchagas.udacity.popularmovies.Config;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.rtchagas.udacity.popularmovies.core.MovieSearchResult;
import com.rtchagas.udacity.popularmovies.core.ReviewSearchResult;
import com.rtchagas.udacity.popularmovies.core.TrailerSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface to The Movie Database API REST services
 */
public interface TmdbAPI {

    String BASE_URL = "https://api.themoviedb.org/3/";
    String BASE_IMG_THUMB_URL = "http://image.tmdb.org/t/p/w342/";
    String BASE_IMG_BACKDROP_URL = "http://image.tmdb.org/t/p/w780/";

    @GET("movie/popular?api_key=" + Config.TMDB_API_KEY)
    Call<MovieSearchResult> getPopular();

    @GET("movie/top_rated?api_key=" + Config.TMDB_API_KEY)
    Call<MovieSearchResult> getTopRated();

    @GET("movie/{id}?api_key=" + Config.TMDB_API_KEY)
    Call<Movie> getMovieDetails(@Path("id") int movieId);

    @GET("movie/{id}/videos?api_key=" + Config.TMDB_API_KEY)
    Call<TrailerSearchResult> getTrailers(@Path("id") int movieId);

    @GET("movie/{id}/reviews?api_key=" + Config.TMDB_API_KEY)
    Call<ReviewSearchResult> getReviews(@Path("id") int movieId);
}
