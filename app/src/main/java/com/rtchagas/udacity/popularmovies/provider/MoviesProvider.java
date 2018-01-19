package com.rtchagas.udacity.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rtchagas.udacity.popularmovies.provider.contract.MovieContract;

public final class MoviesProvider extends ContentProvider {

    private MovieDbHelper mDBHelper;

    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        sUriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case CODE_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                qb.setTables(MovieContract.MovieEntry.TABLE_NAME);
                break;
            case CODE_MOVIE_ID:
                qb.setTables(MovieContract.MovieEntry.TABLE_NAME);
                qb.appendWhere(MovieContract.MovieEntry._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown or not supported URI " + uri);
        }

        Cursor c = qb.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // Tell the cursor what URI to watch, so it knows when its source data changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri newURI = null;
        SQLiteDatabase db  = mDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    newURI = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown or not supported URI " + uri);
        }

        // Notify others that this URI has changed.
        if (newURI != null) {
            getContext().getContentResolver().notifyChange(newURI, null);
        }
        return newURI;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count;

        switch (sUriMatcher.match(uri)) {
            // We support deleting only one row.
            case CODE_MOVIE_ID:
                String id = uri.getLastPathSegment();
                count = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.MovieEntry.TABLE_NAME,
                                MovieContract.MovieEntry._ID + " = ? ",
                                new String[] {id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown or not supported URI " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Update is now supported.
        return 0;
    }
}
