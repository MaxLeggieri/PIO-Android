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

public class ShowcaseRecyclerView extends RecyclerView.Adapter<ShowcaseRecyclerView.ProductViewHolder> {

    ArrayList<Product> items;

    interface OnRecyclerScrollListener {
        public void onNearEndScrollListener();
    }

    OnRecyclerScrollListener scrollListener;
    private boolean endScrollNotified = false;

    ShowcaseRecyclerView(ArrayList<Product> items, OnRecyclerScrollListener scrollListener) {

        this.items = items;
        this.scrollListener = scrollListener;
    }

    public void setItems(ArrayList<Product> items) {
        this.items = items;
        this.notifyDataSetChanged();
        endScrollNotified = false;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.product_view,
                        parent,
                        false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {

        final Product p = items.get(position);

        Log.e("onBindViewHolder","product: "+p.name);

        if (position >= items.size()-4) {

            if ((scrollListener != null) && !endScrollNotified)  {
                endScrollNotified = true;
                scrollListener.onNearEndScrollListener();
            }
        }

        Picasso.with(holder.context).load("http://pioalert.com"+p.image).into(holder.imageView);

        holder.productId = p.pid;
        holder.title.setText(p.name);
        holder.description.setText(p.descShort);
        holder.price.setText("€ "+p.price);
        holder.priceOff.setText("€ "+p.initialPrice);
        holder.priceOff.setPaintFlags(holder.priceOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.priceOff.setVisibility(View.VISIBLE);

        if (p.initialPrice.equals("0")) {
            holder.priceOff.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title,description,price,priceOff;
        private int productId;
        private Context context;

        public ProductViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            price = (TextView) itemView.findViewById(R.id.price);
            priceOff = (TextView) itemView.findViewById(R.id.priceOff);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent promoIntent = new Intent(itemView.getContext(),ProductActivity.class);
                    promoIntent.putExtra("productId",productId);
                    itemView.getContext().startActivity(promoIntent);
                }
            });
        }
    }
}
