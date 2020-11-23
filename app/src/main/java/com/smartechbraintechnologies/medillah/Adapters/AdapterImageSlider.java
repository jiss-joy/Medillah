package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smartechbraintechnologies.medillah.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

public class AdapterImageSlider extends SliderViewAdapter<AdapterImageSlider.Holder> {

    private Context mContext;
    private final int[] mImages;

    public AdapterImageSlider(Context mContext, int[] mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_image, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Glide.with(mContext).load(mImages[position]).placeholder(R.drawable.orders).into(viewHolder.imageView);
//        viewHolder.imageView.setImageResource(mImages[position]);
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder {

        private final ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image_view);
        }
    }
}
