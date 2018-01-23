package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.rtchagas.udacity.popularmovies.core.Movie;

import java.util.List;

public final class MovieListAdapter extends MovieBaseAdapter {

    private List<Movie> mMovieList = null;

    private OnConsumedAllMoviesListener mConsumedListener = null;

    public MovieListAdapter(View.OnClickListener itemClickListener,
                            OnConsumedAllMoviesListener consumedAllMoviesListener) {
        super(itemClickListener);
        mConsumedListener = consumedAllMoviesListener;
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

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        // If consumed the last item in the list, signal this.
        if ((position == (getItemCount() - 1)) && (mConsumedListener != null)) {
            mConsumedListener.onConsumedAllMovies(getItemCount());
        }
    }

    public void appendMovies(@NonNull List<Movie> moreMovieList) {
        if (mMovieList != null) {
            int oldSize = getItemCount();
            mMovieList.addAll(moreMovieList);
            notifyItemRangeInserted(oldSize, moreMovieList.size());
        }
    }
}
