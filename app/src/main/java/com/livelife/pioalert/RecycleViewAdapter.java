package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Max on 25/05/2017.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ListItemViewHolder> {


    List<JSONObject> items;
    Context context;

    RecycleViewAdapter(Context context, List<JSONObject> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.context = context;
        this.items = modelData;
    }

    public void setItems(List<JSONObject> modelData) {
        this.items = modelData;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject model = items.get(position);



        try {

            //Log.e("getItemViewType",model.toString(2));

            if (model.getString("type").equals("com")) {
                return 0;
            }
            else if (model.getString("type").equals("ad")) {
                return 1;
            }
            else {
                return 2;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return super.getItemViewType(position);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.horizontal_item_square,
                                parent,
                                false);
                break;

            case 1:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.horizontal_item_rect,
                                parent,
                                false);
                break;

            case 2:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.horizontal_item_square,
                                parent,
                                false);
                break;

            default:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.horizontal_item_square,
                                parent,
                                false);

        }

        return new ListItemViewHolder(itemView,context);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, final int position) {
        JSONObject model = items.get(position);
        try {

            switch (holder.getItemViewType()) {
                case 0:
                    Picasso.with(context).load("http://pioalert.com"+model.getString("img")).into(holder.imageView);
                    holder.title.setText(model.getString("title"));
                    //String distance1 = String.format("%.1f", model.getDouble("distance"));
                    holder.subtitle.setText(model.getString("distanceHuman"));
                    holder.position = position;
                    holder.type = 0;
                    holder.itemId = model.getInt("id");
                    break;
                case 1:
                    Picasso.with(context).load("http://pioalert.com"+model.getString("img")).into(holder.imageView);
                    holder.title.setText(model.getString("title"));
                    //String distance2 = String.format("%.1f", model.getDouble("distance"));
                    holder.subtitle.setText(model.getString("distanceHuman"));
                    holder.position = position;
                    holder.type = 1;
                    holder.itemId = model.getInt("id");
                    break;
                case 2:
                    Picasso.with(context).load("http://pioalert.com"+model.getString("img")).into(holder.imageView);
                    holder.title.setText(model.getString("title"));
                    holder.subtitle.setText(model.getString("subititle"));
                    holder.position = position;
                    holder.type = 2;
                    holder.itemId = model.getInt("id");
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }




    public static class ListItemViewHolder extends RecyclerView.ViewHolder {


        private TextView title;
        private TextView subtitle;
        private ImageView imageView;
        private RelativeLayout container;
        private int position;
        private int type;
        private int itemId;


        public ListItemViewHolder(View itemView, final Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ListItemViewHolder","Clicked..."+position+" type = "+type);

                    if (type == 0) {
                        Intent companyIntent = new Intent(context,CompanyActivity.class);
                        companyIntent.putExtra("comId",itemId);
                        context.startActivity(companyIntent);
                    }
                    else if (type == 1) {
                        Intent promoIntent = new Intent(context,PromoActivity.class);
                        promoIntent.putExtra("promoId",itemId);
                        context.startActivity(promoIntent);
                    }
                    else if (type == 2) {
                        Intent productIntent = new Intent(context,ProductActivity.class);
                        productIntent.putExtra("productId",itemId);
                        context.startActivity(productIntent);
                    }

                }
            });

        }

    }

}
