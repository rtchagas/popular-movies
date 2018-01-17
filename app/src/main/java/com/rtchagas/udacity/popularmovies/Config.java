package com.rtchagas.udacity.popularmovies;

/**
 * 'Config' class with values used application wide.
 */
public final class Config {

    // Replace this with your own key!
    public static final String TMDB_API_KEY = SensitiveData.TMDB_API_KEY;

    // YouTube integration
    public static final String URL_YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/%s/0.jpg";
    public static final String URL_YOUTUBE_WATCH = "https://www.youtube.com/watch?v=%s";
}
