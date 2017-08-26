package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Max on 22/06/2017.
 */

public class CartRecyclerView extends RecyclerView.Adapter<CartRecyclerView.CartItemViewHolder> {

    ArrayList<Product> items;
    static CartActivity cartActivity;

    CartRecyclerView(ArrayList<Product> items, CartActivity cartActivity) {
        this.items = items;
        this.cartActivity = cartActivity;
    }

    public void setItems(ArrayList<Product> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.product_view_cart,
                        parent,
                        false);

        return new CartItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartItemViewHolder holder, int position) {
        final Product p = items.get(position);

        holder.product = p;

        Picasso.with(holder.context).load("http://pioalert.com"+p.image).into(holder.imageView);
        holder.productId = p.pid;
        holder.title.setText(p.name);
        holder.quantity.setText("Quantità: "+p.quantity);
        Double price = Double.parseDouble(p.price);
        holder.price.setText(Utility.getInstance().getFormattedPrice(price*p.quantity));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title,quantity,price;
        private Button editButton;
        private int productId;
        private Context context;
        private Product product;

        public CartItemViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            price = (TextView) itemView.findViewById(R.id.price);
            editButton = (Button) itemView.findViewById(R.id.editButton);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartActivity.modifyQuantityForItem(product);
                }
            });



            /*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent promoIntent = new Intent(itemView.getContext(),ProductActivity.class);
                    promoIntent.putExtra("productId",productId);
                    itemView.getContext().startActivity(promoIntent);
                }
            });
            */
        }
    }


}
