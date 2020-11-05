package com.smartechbraintechnologies.medillah.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.R;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.Holder> {

    private final Context mContext;
    private final int[] mCategoryImages;
    private final String[] mCategories;

    public AdapterCategory(Context mContext, int[] mCategoryImages, String[] mCategories) {
        this.mContext = mContext;
        this.mCategoryImages = mCategoryImages;
        this.mCategories = mCategories;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterCategory.Holder(LayoutInflater.from(mContext).inflate(R.layout.item_categories, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.category_image.setImageResource(mCategoryImages[position]);
        holder.category_name.setText(mCategories[position]);
    }

    @Override
    public int getItemCount() {
        return mCategoryImages.length;
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final ImageView category_image;
        private final TextView category_name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            category_image = itemView.findViewById(R.id.item_categories_image);
            category_name = itemView.findViewById(R.id.item_categories_name);
        }
    }
}
