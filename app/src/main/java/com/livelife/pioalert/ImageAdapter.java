package com.livelife.pioalert;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 11/08/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    List<String> items;
    PromoActivity activity;

    ImageAdapter(PromoActivity activity, List<String> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.activity = activity;
        this.items = modelData;
    }


    public void setItems(List<String> modelData) {
        this.items = modelData;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.horizontal_item_square,
                        parent,
                        false);


        return new ImageViewHolder(itemView,activity);
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {

        String path = items.get(position);

        Picasso.with(activity).load(path).into(holder.imageView);
        holder.position = position;

        holder.title.setText("");
        holder.subtitle.setText("");


    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {


        private ImageView imageView;
        private TextView title;
        private TextView subtitle;
        private int position;
        private ArrayList<String> items;

        public ImageViewHolder(View itemView, final PromoActivity activity) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ListItemViewHolder","Clicked..."+position);

                    activity.showGallery(position);
                }
            });

        }

    }
}
