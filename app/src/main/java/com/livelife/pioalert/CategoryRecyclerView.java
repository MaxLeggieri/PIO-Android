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

import java.util.ArrayList;

/**
 * Created by Max on 13/06/2017.
 */

public class CategoryRecyclerView  extends RecyclerView.Adapter<CategoryRecyclerView.CategoryViewHolder> {

    static ArrayList<Category> items;
    static CategoryChangedListener categoryChangedListener;
    interface CategoryChangedListener {
        void onCategoryChanged(String category);
    }


    CategoryRecyclerView(ArrayList<Category> items, CategoryChangedListener categoryChangedListener) {
        CategoryRecyclerView.categoryChangedListener = categoryChangedListener;
        this.items = items;
    }



    public void setItems(ArrayList<Category> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.horizontal_cat_rect,
                        parent,
                        false);

        return new CategoryRecyclerView.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {

        Category c = items.get(position);

        if(c.imgPath != null) {
            Picasso.with(holder.context).load(c.imgPath).into(holder.catImageView);
        }
        holder.catName.setText(c.name);
        holder.position = position;
        holder.itemId = c.cid;


        if (c.cid == 0 || c.cid == -1) {
            //holder.catImageView.setVisibility(View.GONE);
        }

        if (c.selected) {
            holder.checkedBorder.setVisibility(View.VISIBLE);
        } else {
            holder.checkedBorder.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {


        private TextView catName;
        private ImageView catImageView;
        private RelativeLayout container;
        private View checkedBorder;
        private int position;
        private int itemId;
        private Context context;


        public CategoryViewHolder(final View itemView) {
            super(itemView);
            catName = (TextView) itemView.findViewById(R.id.catName);
            catImageView = (ImageView) itemView.findViewById(R.id.catImageView);
            checkedBorder = (View) itemView.findViewById(R.id.checkedBorder);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Log.e("CategoryViewHolder","Clicked cat item: "+itemId);


                    String cat = "";



                    for (Category c : items) {
                        if (c.cid == itemId)  {
                            if (itemId == 0) {
                                cat = "0";
                            }
                            else if (itemId == -1) {
                                cat = "all";
                            } else {
                                cat = ""+c.cid+"";
                            }
                            c.selected = true;
                        } else {
                            c.selected = false;
                        }
                    }

                    if (categoryChangedListener != null) {
                        /*
                        promoFragment.catAdapter.setItems(promoFragment.allCat);
                    promoFragment.lastCatSelected = cat;
                    promoFragment.scrollToTop();
                    promoFragment.reloadPromos(cat);
                         */

                        categoryChangedListener.onCategoryChanged(cat);
                    }




                    /*
                    Intent productIntent = new Intent(itemView.getContext(),ProductActivity.class);
                    productIntent.putExtra("productId",itemId);
                    itemView.getContext().startActivity(productIntent);
                    */

                }
            });

        }

    }


}
