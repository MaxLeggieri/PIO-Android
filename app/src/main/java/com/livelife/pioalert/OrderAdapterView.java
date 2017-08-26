package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Max on 24/06/2017.
 */

public class OrderAdapterView extends RecyclerView.Adapter<OrderAdapterView.OrderViewHolder> {

    ArrayList<Order> items;

    interface OnRecyclerScrollListener {
        public void onNearEndScrollListener();
    }

    ShowcaseRecyclerView.OnRecyclerScrollListener scrollListener;
    private boolean endScrollNotified = false;

    OrderAdapterView(ArrayList<Order> items) {
        this.items = items;
    }

    public void setItems(ArrayList<Order> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cart_view,
                        parent,
                        false);


        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {

        final Order o = items.get(position);

        if (position >= items.size()-4) {

            if ((scrollListener != null) && !endScrollNotified)  {
                endScrollNotified = true;
                scrollListener.onNearEndScrollListener();
            }
        }

        Picasso.with(holder.context).load("http://pioalert.com"+o.brandLogo).into(holder.imageView);

        holder.itemId = o.idOrder;
        holder.title.setText(o.brandName);
        holder.description.setText(o.totalItems+" oggetti");
        holder.total.setText(Utility.getInstance().getFormattedPrice(Double.parseDouble(o.subTotal)+Double.parseDouble(o.shipping)));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder {


        private TextView title;
        private TextView description;
        private TextView total;
        private ImageView imageView;
        private int itemId;
        private Context context;


        public OrderViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            total = (TextView) itemView.findViewById(R.id.total);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(),CartActivity.class);
                    intent.putExtra("idCom",itemId);
                    itemView.getContext().startActivity(intent);

                }
            });

        }

    }
}
