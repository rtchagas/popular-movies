package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.controller.TmdbAPI;
import com.rtchagas.udacity.popularmovies.core.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMoviesList = null;

    private View.OnClickListener mItemClickListener = null;

    public MovieAdapter(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setMovies(List<Movie> movieList) {
        mMoviesList = movieList;
        notifyDataSetChanged();
    }

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

        Movie movie = mMoviesList.get(position);

        // Prepare the poster URL to be loaded
        String imgUrl = TmdbAPI.BASE_IMG_URL + movie.getPosterPath();

        // Load the poster through Picasso
        Picasso.with(context).load(Uri.parse(imgUrl)).into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return (mMoviesList != null ? mMoviesList.size() : 0);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_poster) ImageView ivPoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
