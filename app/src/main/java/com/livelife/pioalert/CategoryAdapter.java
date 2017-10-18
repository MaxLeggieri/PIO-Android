package com.livelife.pioalert;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shoeb on 17/10/17.
 */

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private Activity mActivity;
    private List<CategoriesModal.Response.Datum> mDatumList;
    private boolean isExpanded = false;
    private boolean isFromFilter = false;

    public CategoryAdapter(Activity mActivity, List<CategoriesModal.Response.Datum> mDatumList) {
        this.mActivity = mActivity;
        this.mDatumList = mDatumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_parent_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoriesModal.Response.Datum bean
                = mDatumList.get(position);
        holder.parent_tv.setText(bean.getName());

        if (bean.isPrimarySelected()) {
            holder.status_iv.setImageResource(R.drawable.friend_added);
        } else {
            holder.status_iv.setImageResource(R.drawable.friend_add_icon);
        }

        if (position == (mDatumList.size() - 1)) {
            holder.bottom_view.setVisibility(View.GONE);
        } else {
            holder.bottom_view.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public int getItemCount() {
        return mDatumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView parent_tv;
        ImageView status_iv;
        View bottom_view;


        public ViewHolder(View view) {
            super(view);
            parent_tv = view.findViewById(R.id.parent_tv);
            status_iv = view.findViewById(R.id.status_iv);
            bottom_view = view.findViewById(R.id.bottom_view);

        }


    }

    public void addItems(List<CategoriesModal.Response.Datum> mDatumList) {
        this.mDatumList.clear();
        this.mDatumList.addAll(mDatumList);
        notifyDataSetChanged();
    }

    public void updateItem(CategoriesModal.Response.Datum mPublicProfileCategoryModalMain, int pos) {
        for (CategoriesModal.Response.Datum modalMain :
                mDatumList) {
            if (modalMain.isPrimarySelected()) {
                modalMain.setPrimarySelected(false);
            }
        }

        mPublicProfileCategoryModalMain.setPrimarySelected(true);

        mDatumList.set(pos, mPublicProfileCategoryModalMain);
        notifyDataSetChanged();
    }
}
