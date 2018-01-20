package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.rtchagas.udacity.popularmovies.controller.MovieController;
import com.rtchagas.udacity.popularmovies.core.Movie;

public final class MovieCursorAdapter extends MovieBaseAdapter {

    private Cursor mCursor = null;

    public MovieCursorAdapter(View.OnClickListener itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public Movie getMovie(int position) {
        mCursor.moveToPosition(position);
        return MovieController.getMovieFromCursor(mCursor);
    }

    @Override
    public void swapData(@Nullable Object newData) {
        mCursor = (Cursor) newData;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Object getData() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        return (mCursor != null ? mCursor.getCount() : 0);
    }
}
