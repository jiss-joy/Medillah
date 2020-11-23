package com.smartechbraintechnologies.medillah.Adapters;

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
    private OnCategoryClickListener onCategoryClickListener;

    public AdapterCategory(Context mContext, int[] mCategoryImages, String[] mCategories, OnCategoryClickListener onCategoryClickListener) {
        this.mContext = mContext;
        this.mCategoryImages = mCategoryImages;
        this.mCategories = mCategories;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterCategory.Holder(LayoutInflater.from(mContext).inflate(R.layout.item_categories, parent, false), onCategoryClickListener);
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

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView category_image;
        private final TextView category_name;

        public Holder(@NonNull View itemView, OnCategoryClickListener onCategoryClickListener) {
            super(itemView);
            category_image = itemView.findViewById(R.id.item_categories_image);
            category_name = itemView.findViewById(R.id.item_categories_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCategoryClickListener.OnCategoryClick(getAdapterPosition(), v);
        }
    }

    public interface OnCategoryClickListener {
        void OnCategoryClick(int position, View view);
    }
}
