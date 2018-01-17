package com.rtchagas.udacity.popularmovies.controller;

        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;

        import java.util.List;

/**
 * Listener for handling API calls that return a list as result.
 * @param <T> Type of the result. Basically, a collection of T elements.
 */
public interface OnSearchResultListener<T> {

    void onResultReady(@Nullable List<T> list);

    void onResultError(@Nullable String message);
}
