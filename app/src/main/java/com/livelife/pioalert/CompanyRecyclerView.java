package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Max on 07/06/2017.
 */

public class CompanyRecyclerView extends RecyclerView.Adapter<CompanyRecyclerView.CompanyViewHolder> {

    ArrayList<Company> items;

    interface OnRecyclerScrollListener {
        public void onNearEndScrollListener();
    }

    OnRecyclerScrollListener scrollListener;
    private boolean endScrollNotified = false;

    CompanyRecyclerView(ArrayList<Company> items, OnRecyclerScrollListener scrollListener) {

        this.items = items;
        this.scrollListener = scrollListener;
    }

    public void setItems(ArrayList<Company> items) {
        this.items = items;
        this.notifyDataSetChanged();
        endScrollNotified = false;
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.company_view,
                        parent,
                        false);

        return new CompanyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CompanyViewHolder holder, int position) {

        final Company c = items.get(position);

        Log.e("onBindViewHolder","company: "+c.brandName);

        if (position >= items.size()-4) {

            if ((scrollListener != null) && !endScrollNotified)  {
                endScrollNotified = true;
                scrollListener.onNearEndScrollListener();
            }
        }

        Picasso.with(holder.context).load("http://pioalert.com"+c.image).into(holder.imageView);

        holder.companyId = c.cid;
        holder.title.setText(c.brandName);
        holder.description.setText(c.description);
        holder.location.setText(c.locations.get(0).distanceHuman);



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title,description,location;
        private int companyId;
        private Context context;

        public CompanyViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            location = (TextView) itemView.findViewById(R.id.location);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent companyIntent = new Intent(itemView.getContext(),CompanyActivity.class);
                    companyIntent.putExtra("comId",companyId);
                    itemView.getContext().startActivity(companyIntent);
                }
            });
        }
    }
}
