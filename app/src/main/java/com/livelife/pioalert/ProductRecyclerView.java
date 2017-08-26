package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
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
 * Created by Max on 07/06/2017.
 */

public class ProductRecyclerView extends RecyclerView.Adapter<ProductRecyclerView.ProductViewHolder> {

    ArrayList<Product> items;

    ProductRecyclerView(ArrayList<Product> items) {
        this.items = items;
    }


    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.horizontal_item_square,
                        parent,
                        false);

        return new ProductRecyclerView.ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        Product p = items.get(position);

        Picasso.with(holder.context).load("http://pioalert.com"+p.image).into(holder.imageView);
        holder.title.setText(p.name);
        holder.subtitle.setText("€ "+p.price);
        holder.position = position;
        holder.itemId = p.pid;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        private TextView title;
        private TextView subtitle;
        private ImageView imageView;
        private RelativeLayout container;
        private int position;
        private int itemId;
        private Context context;


        public ProductViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productIntent = new Intent(itemView.getContext(),ProductActivity.class);
                    productIntent.putExtra("productId",itemId);
                    itemView.getContext().startActivity(productIntent);

                }
            });

        }

    }
}
