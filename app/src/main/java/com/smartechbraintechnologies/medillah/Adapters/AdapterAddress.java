package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smartechbraintechnologies.medillah.Models.ModelAddress;
import com.smartechbraintechnologies.medillah.Models.ModelMyLabTest;
import com.smartechbraintechnologies.medillah.Models.ModelProductShort;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAddress extends RecyclerView.Adapter<AdapterAddress.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelAddress> addressItems;
    private OnOptionsClickListener onOptionsClickListener;

    public AdapterAddress(Context mContext, ArrayList<ModelAddress> addressItems, OnOptionsClickListener onOptionsClickListener) {
        this.mContext = mContext;
        this.addressItems = addressItems;
        this.onOptionsClickListener = onOptionsClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterAddress.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address_card, parent, false), onOptionsClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (addressItems.get(position).getAddressType()) {
            case "Home":
                holder.address_icon.setImageResource(R.drawable.home);
                break;
            case "Work":
                holder.address_icon.setImageResource(R.drawable.work);
                break;
            case "Other":
                holder.address_icon.setImageResource(R.drawable.location);
                break;
        }
        if (addressItems.get(position).getAddressStatus().equals("Default")) {
            holder.address_status.setVisibility(View.VISIBLE);
        } else {
            holder.address_status.setVisibility(View.GONE);
        }
        holder.address_type.setText(addressItems.get(position).getAddressType());
        holder.address.setText(addressItems.get(position).getAddress());
        if (addressItems.get(position).getAddressDeliveryStatus().equals("No Delivery")) {
            holder.no_delivery.setVisibility(View.VISIBLE);
        } else {
            holder.no_delivery.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addressItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView address_type, address, address_status, no_delivery;
        ImageView address_icon;
        ImageButton optionsBTN;


        public MyViewHolder(@NonNull View itemView, OnOptionsClickListener onOptionsClickListener) {
            super(itemView);
            address_icon = (ImageView) itemView.findViewById(R.id.address_card_type_image);
            address_status = (TextView) itemView.findViewById(R.id.address_card_address_status);
            address = (TextView) itemView.findViewById(R.id.address_card_address);
            address_type = (TextView) itemView.findViewById(R.id.address_card_address_type);
            no_delivery = (TextView) itemView.findViewById(R.id.address_card_no_delivery);
            no_delivery.setVisibility(View.GONE);
            optionsBTN = (ImageButton) itemView.findViewById(R.id.address_card_options_btn);
            optionsBTN.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOptionsClickListener.OnOptionsClick(getAdapterPosition(), v);
        }
    }

    public interface OnOptionsClickListener {
        void OnOptionsClick(int position, View v);
    }

    public void setOnOptionsClickListener(OnOptionsClickListener onOptionsClickListener) {
        this.onOptionsClickListener = onOptionsClickListener;
    }
}
