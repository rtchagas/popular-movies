package com.rtchagas.udacity.popularmovies.controller;

import com.rtchagas.udacity.popularmovies.core.Movie;

import java.util.List;

public interface OnMovieSearchResultListener {

    void onResultReady(List<Movie> movieList);

    void onResultError(String message);
}
