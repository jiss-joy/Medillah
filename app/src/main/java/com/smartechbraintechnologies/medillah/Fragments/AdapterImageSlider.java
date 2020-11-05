package com.smartechbraintechnologies.medillah.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartechbraintechnologies.medillah.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class AdapterImageSlider extends SliderViewAdapter<AdapterImageSlider.Holder> {

    private final int[] mImages;

    public AdapterImageSlider(int[] mImages) {
        this.mImages = mImages;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_image, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.imageView.setImageResource(mImages[position]);
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
