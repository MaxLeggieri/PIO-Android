package com.livelife.pioalert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 07/06/2017.
 */

public class PromoRecyclerView extends RecyclerView.Adapter<PromoRecyclerView.PromoViewHolder> {

    ArrayList<Promo> items;

    interface OnRecyclerScrollListener {
        public void onNearEndScrollListener();
    }

    OnRecyclerScrollListener scrollListener;
    private boolean endScrollNotified = false;

    PromoRecyclerView(ArrayList<Promo> items,OnRecyclerScrollListener scrollListener) {

        this.items = items;
        this.scrollListener = scrollListener;
    }

    public void setItems(ArrayList<Promo> items) {
        this.items = items;
        this.notifyDataSetChanged();
        endScrollNotified = false;
    }

    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.promo_view,
                        parent,
                        false);

        return new PromoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PromoViewHolder holder, int position) {

        final Promo p = items.get(position);


        if (position >= items.size()-4) {

            if ((scrollListener != null) && !endScrollNotified)  {
                endScrollNotified = true;
                scrollListener.onNearEndScrollListener();
            }
        }

        Picasso.with(holder.context).load("http://pioalert.com"+p.imagePath).placeholder(R.drawable.placeholder_logo_16_9).into(holder.imageView);
        holder.promoDesc.setText(p.title);
        holder.promoLongDesc.setText(p.desc);
        holder.promoExpire.setText("Scade il "+p.expiration);
        holder.shopDistanceTextView.setText("A "+p.distanceHuman+" da te");
        holder.promoId = p.pid;

        if (p.liked) {
            holder.likeButton.setImageResource(R.drawable.icon_like_attivo);
        } else {
            holder.likeButton.setImageResource(R.drawable.icon_like);
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageButton lb = (ImageButton) view;

                p.liked = !p.liked;

                if (p.liked) {
                    lb.setImageResource(R.drawable.icon_like_attivo);
                } else {
                    lb.setImageResource(R.drawable.icon_like);
                }

                WebApi.getInstance().likeAd(p.liked,p.pid);


            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.pioalert.com/sharead/?idad="+p.pid+"&uid="+PioUser.getInstance().uid);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Guarda questa offerta su PIO, l'app delle promozioni!");
                sendIntent.setType("text/plain");
                holder.context.startActivity(Intent.createChooser(sendIntent, "Condividi con:"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView promoDesc,promoExpire,shopDistanceTextView,promoLongDesc;
        private ImageButton likeButton,shareButton;
        private int promoId;
        private Context context;

        public PromoViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            promoDesc = (TextView) itemView.findViewById(R.id.promoDesc);
            promoLongDesc = (TextView) itemView.findViewById(R.id.promoLongDesc);
            promoExpire = (TextView) itemView.findViewById(R.id.promoExpire);
            shopDistanceTextView = (TextView) itemView.findViewById(R.id.shopDistanceTextView);

            likeButton = (ImageButton) itemView.findViewById(R.id.likeButton);
            shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent promoIntent = new Intent(itemView.getContext(),PromoActivity.class);
                    promoIntent.putExtra("promoId",promoId);



                    // Creating a pending intent and wrapping our intent
                    PendingIntent pendingIntent = PendingIntent.getActivity(itemView.getContext(), 1, promoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        // Perform the operation associated with our pendingIntent
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }



                    //itemView.getContext().startActivity(pendingIntent);
                }
            });
        }
    }
}
