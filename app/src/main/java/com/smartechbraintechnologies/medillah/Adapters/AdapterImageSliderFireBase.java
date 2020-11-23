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

import java.util.ArrayList;

public class AdapterImageSliderFireBase extends SliderViewAdapter<AdapterImageSliderFireBase.Holder> {

    private Context mContext;
    private final ArrayList<String> mImages;

    public AdapterImageSliderFireBase(Context mContext, ArrayList<String> mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
    }

    @Override
    public AdapterImageSliderFireBase.Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_image, parent, false);
        return new AdapterImageSliderFireBase.Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Glide.with(mContext).load(mImages.get(position)).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder {

        private final ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image_view);
        }
    }
}
