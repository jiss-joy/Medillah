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

import com.smartechbraintechnologies.medillah.Models.ModelAddress;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAddress extends RecyclerView.Adapter<AdapterAddress.MyViewHolder> {

    private final Context context;
    private final ArrayList<ModelAddress> addressList;
    private OnOptionsClickListener onOptionsClickListener;

    public AdapterAddress(Context context, ArrayList<ModelAddress> addressList, OnOptionsClickListener onOptionsClickListener) {
        this.context = context;
        this.addressList = addressList;
        this.onOptionsClickListener = onOptionsClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterAddress.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address_card, parent, false), onOptionsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAddress.MyViewHolder holder, int position) {
        holder.address_icon.setImageResource(addressList.get(position).getIcon());
        if (addressList.get(position).getAddressStatus().equals("Default")) {
            holder.address_status.setVisibility(View.VISIBLE);
        } else {
            holder.address_status.setVisibility(View.GONE);
        }
        holder.address_type.setText(addressList.get(position).getAddressType());
        holder.address.setText(addressList.get(position).getAddress());
        if (addressList.get(position).getAddressDeliveryStatus().equals("No Delivery")) {
            holder.no_delivery.setVisibility(View.VISIBLE);
        } else {
            holder.no_delivery.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
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
            onOptionsClickListener.onOptionsClick(getAdapterPosition(), v);
        }
    }

    public interface OnOptionsClickListener {
        void onOptionsClick(int position, View v);
    }

}
