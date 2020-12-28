package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smartechbraintechnologies.medillah.MainActivity;
import com.smartechbraintechnologies.medillah.Models.ModelProductShort;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class AdapterProductShort extends FirestorePagingAdapter<ModelProductShort, AdapterProductShort.MyViewHolder> {

    private Context mContext;
    private OnProductClickListener onProductClickListener;

    public AdapterProductShort(@NonNull FirestorePagingOptions<ModelProductShort> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelProductShort model) {
        Glide.with(mContext).load(model.getProductImage()).placeholder(R.drawable.orders).into(holder.image);
        holder.name.setText(model.getProductName());
        holder.mrp.setText("₹" + model.getProductMRP());
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sp.setText("₹" + model.getProductSellingPrice());
        holder.percentOFF.setText(new DecimalFormat("#.00").format(model.getProductDiscount()) + "% off");
        holder.saveAmt.setText("Save ₹" + new DecimalFormat("#.00").format(model.getProductMRP() - model.getProductSellingPrice()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_short, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {
            case ERROR:
                Log.d("ERROR", state.name());
                break;
            case FINISHED:
                Log.d("FINISHED", state.name());
                break;
            case LOADED:
                Log.d("LOADED", "Total Items Loaded" + getItemCount());
                break;
            case LOADING_INITIAL:
                Log.d("LOADING_INITIAL", "Loading Initial Data");
                break;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        TextView name, mrp, sp, percentOFF, saveAmt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (SimpleDraweeView) itemView.findViewById(R.id.item_product_short_image);
            name = (TextView) itemView.findViewById(R.id.item_product_short_name);
            mrp = (TextView) itemView.findViewById(R.id.item_product_short_mrp);
            sp = (TextView) itemView.findViewById(R.id.item_product_short_sp);
            percentOFF = (TextView) itemView.findViewById(R.id.item_product_short_percent_off);
            saveAmt = (TextView) itemView.findViewById(R.id.item_product_short_save_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onProductClickListener != null) {
                        onProductClickListener.OnProductClick(getItem(position), position);
                    }
                }
            });
        }
    }

    public interface OnProductClickListener {
        void OnProductClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }
}
