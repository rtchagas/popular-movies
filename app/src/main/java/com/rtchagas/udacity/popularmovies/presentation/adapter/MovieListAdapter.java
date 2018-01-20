package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.rtchagas.udacity.popularmovies.core.Movie;

import java.util.List;

public final class MovieListAdapter extends MovieBaseAdapter {

    private List<Movie> mMovieList = null;

    public MovieListAdapter(View.OnClickListener itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public Movie getMovie(int position) {
        return mMovieList.get(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void swapData(@Nullable Object newData) {
        mMovieList = (List<Movie>) newData;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Object getData() {
        return mMovieList;
    }

    @Override
    public int getItemCount() {
        return (mMovieList != null ? mMovieList.size() : 0);
    }

}
