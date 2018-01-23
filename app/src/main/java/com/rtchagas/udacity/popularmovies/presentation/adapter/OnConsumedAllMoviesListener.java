package com.rtchagas.udacity.popularmovies.presentation.adapter;

public interface OnConsumedAllMoviesListener {

    /**
     * This method is called every time when the adapter consumed
     * all movies in its internal list.<br/>
     * I.e, the last movie was bound to a view holder.
     * @param size The number of movies already consumed.
     */
    void onConsumedAllMovies(int size);
}
