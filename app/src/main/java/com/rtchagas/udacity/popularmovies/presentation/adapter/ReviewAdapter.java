package com.rtchagas.udacity.popularmovies.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.rtchagas.udacity.popularmovies.R;
import com.rtchagas.udacity.popularmovies.core.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.TrailerViewHolder> {

    private List<Review> mReviewList = null;

    public void setReviews(List<Review> trailerList) {
        mReviewList = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {
        final Review review = mReviewList.get(position);
        holder.tvReviewAuthor.setText(review.getAuthor());

        final String content = tideText(review.getContent());
        holder.tvReviewContent.setText(content);
        holder.tvReviewMore.setVisibility(View.GONE);

        // Click listener for the case when the review was ellipsized.
        final View.OnClickListener readMoreListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvReviewContent.setMaxLines(Integer.MAX_VALUE);
                holder.tvReviewMore.setVisibility(View.GONE);
            }
        };

        // Let's check if the text was ellipsized and change the layout.
        holder.tvReviewContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (isEllipsizedText(holder.tvReviewContent)) {
                            // Text was ellipsized
                            holder.tvReviewMore.setOnClickListener(readMoreListener);
                            holder.tvReviewMore.setVisibility(View.VISIBLE);
                        }
                        // Remove the listener
                        holder.tvReviewContent.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

    }

    private boolean isEllipsizedText(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null){
            int lines = layout.getLineCount();
            if ((lines > 0) && (layout.getEllipsisCount(lines - 1) > 0)) {
                return true;
            }
        }
        return false;
    }

    private String tideText(String text) {

        if (TextUtils.isEmpty(text)) {
            return "";
        }

        // Remove extra whitespaces
        return text.trim()
                .replaceAll("\r\n", "\n")
                .replaceAll("\n\n\n", "\n\n");
    }

    @Override
    public int getItemCount() {
        return (mReviewList != null ? mReviewList.size() : 0);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author)
        TextView tvReviewAuthor;

        @BindView(R.id.tv_review_content)
        TextView tvReviewContent;

        @BindView(R.id.tv_review_more)
        TextView tvReviewMore;

        TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
