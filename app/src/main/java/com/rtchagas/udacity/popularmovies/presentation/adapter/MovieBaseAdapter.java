package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.TmdbAPI;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class MovieBaseAdapter extends RecyclerView.Adapter<MovieBaseAdapter.MovieViewHolder> {

    private View.OnClickListener mItemClickListener = null;

    MovieBaseAdapter(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @NonNull
    public abstract Movie getMovie(int position);

    public abstract void swapData(@Nullable Object newData);

    @Nullable
    public abstract Object getData();

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        // Set the click listener.
        // I like to use the view's tag to avoid creating a special interface
        // just for handling the item clicks.
        if (mItemClickListener != null) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mItemClickListener);
        }

        // Get context from any view inside view holder
        Context context = holder.itemView.getContext();

        Movie movie = getMovie(position);

        // Prepare the poster URL to be loaded
        String imgUrl = TmdbAPI.BASE_IMG_THUMB_URL + movie.getPosterPath();

        // Load the poster through Picasso
        Picasso.with(context).load(Uri.parse(imgUrl)).into(holder.ivPoster);

        // Dynamically add the content description to make easy for accessibility.
        holder.ivPoster.setContentDescription(movie.getTitle());
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_poster)
        ImageView ivPoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
