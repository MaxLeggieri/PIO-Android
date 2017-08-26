package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Max on 24/06/2017.
 */

public class RankAdapterView extends RecyclerView.Adapter<RankAdapterView.PlayerViewHolder> {

    ArrayList<PioPlayer> items;

    interface OnRecyclerScrollListener {
        public void onNearEndScrollListener();
    }

    ShowcaseRecyclerView.OnRecyclerScrollListener scrollListener;
    private boolean endScrollNotified = false;

    RankAdapterView(ArrayList<PioPlayer> items) {
        this.items = items;
    }

    public void setItems(ArrayList<PioPlayer> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.rank_view,
                        parent,
                        false);


        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final PioPlayer p = items.get(position);

        if (position >= items.size()-4) {

            if ((scrollListener != null) && !endScrollNotified)  {
                endScrollNotified = true;
                scrollListener.onNearEndScrollListener();
            }
        }

        Picasso.with(holder.context).load(p.imageFullPath).placeholder(R.drawable.placeholder_logo_square).into(holder.imageView);


        holder.title.setText(p.name);
        holder.rankPosition.setText("#"+p.rank);
        holder.total.setText(""+p.score+" pts");

        if (p.currentUser) {
            holder.container.setBackgroundColor(Color.parseColor("#fceec0"));
        } else {
            holder.container.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PlayerViewHolder extends RecyclerView.ViewHolder {


        private TextView title;
        private TextView total;
        private TextView rankPosition;
        private ImageView imageView;
        private RelativeLayout container;
        private Context context;


        public PlayerViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.title);
            rankPosition = (TextView) itemView.findViewById(R.id.rankPosition);
            total = (TextView) itemView.findViewById(R.id.total);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

    }
}
