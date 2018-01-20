package com.rtchagas.udacity.popularmovies.provider.contract;

import android.net.Uri;
import android.provider.BaseColumns;

import com.rtchagas.udacity.popularmovies.BuildConfig;

public final class MovieContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of movies.
         */
        public static final String CONTENT_TYPE=
                "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single question.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE;


        public static final String TITLE = "title";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String RELEASE_DATE = "release_date";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String POSTER_PATH = "poster_path";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String OVERVIEW = "overview";

        /**
         * Database creation statement
         */
        public static final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "
                + TABLE_NAME + "("
                + _ID	+ " INTEGER PRIMARY KEY,"
                + TITLE + " TEXT NOT NULL,"
                + ORIGINAL_TITLE + " TEXT NOT NULL,"
                + RELEASE_DATE + " INTEGER NOT NULL,"
                + VOTE_AVERAGE + " DOUBLE NOT NULL,"
                + POSTER_PATH + " TEXT NOT NULL,"
                + BACKDROP_PATH + " TEXT NOT NULL,"
                + OVERVIEW + " TEXT NOT NULL);";
    }
}
