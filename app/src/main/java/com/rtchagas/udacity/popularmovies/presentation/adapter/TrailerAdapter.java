package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.Config;
import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.core.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> mTrailerList = null;

    public void setTrailers(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        // Get context from any view inside view holder
        final Context context = holder.itemView.getContext();

        final Trailer trailer = mTrailerList.get(position);

        // Prepare the trailer thumbnail URL to be loaded
        String imgUrl = String.format(Config.URL_YOUTUBE_THUMBNAIL, trailer.getKey());

        // Load the thumbnail through Picasso
        Picasso.with(context).load(Uri.parse(imgUrl)).into(holder.ivMovieTrailer);

        // Set the trailer title to image description
        holder.ivMovieTrailer.setContentDescription(trailer.getName());

        // To the title text view
        holder.tvTrailerTitle.setText(trailer.getName());

        // Set the click listener to fire Youtube
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create the intent to fire YouTube
                String url = String.format(Config.URL_YOUTUBE_WATCH, trailer.getKey());
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Start a new activity
                context.startActivity(youtubeIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mTrailerList != null ? mTrailerList.size() : 0);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_movie_trailer)
        ImageView ivMovieTrailer;

        @BindView(R.id.tv_trailer_title)
        TextView tvTrailerTitle;

        TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
