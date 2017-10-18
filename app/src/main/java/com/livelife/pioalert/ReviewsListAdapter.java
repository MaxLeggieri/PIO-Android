package com.livelife.pioalert;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maroof Ahmed Siddique on 17/10/17.
 */

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.MyViewHolder> {

    public ReviewsListAdapter(List<ReviewsModal.Response.Data> mDataList, Activity mActivity) {
        this.mDataList = mDataList;
        this.mActivity = mActivity;
    }

    private List<ReviewsModal.Response.Data> mDataList = new ArrayList<>();
    private Activity mActivity ;


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ReviewsModal.Response.Data bean  = mDataList.get(position);
//        holder.rating_bar_rb.setRating(bean.getRatingValue());
//        holder.reviews_tv.setText(bean.getmComment());

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        RatingBar rating_bar_rb;
        TextView reviews_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            rating_bar_rb = itemView.findViewById(R.id.rating_bar_rb);
            reviews_tv = itemView.findViewById(R.id.reviews_tv);
        }
    }
}
