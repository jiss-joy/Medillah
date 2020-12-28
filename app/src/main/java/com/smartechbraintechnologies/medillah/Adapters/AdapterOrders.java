package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.Models.ModelOrder;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelOrder> orderItems;
    private OnOrderClickListener onOrderClickListener;

    public AdapterOrders(Context mContext, ArrayList<ModelOrder> orderItems, OnOrderClickListener onOrderClickListener) {
        this.mContext = mContext;
        this.orderItems = orderItems;
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterOrders.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false), onOrderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(orderItems.get(position).getOrderImage()).into(holder.image);
        holder.name.setText(orderItems.get(position).getOrderName());
        holder.status.setText(orderItems.get(position).getOrderStatus());
        holder.qty.setText(String.valueOf(orderItems.get(position).getOrderQuantity()));
        holder.total.setText("₹" + String.valueOf(orderItems.get(position).getOrderTotal()));

        switch (orderItems.get(position).getOrderStatus()) {
            case "B":
                holder.status.setText("Placed");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_unchecked_radio);
                break;
            case "A":
                holder.status.setText("Accepted");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.secondary));
                holder.status.setBackgroundResource(R.drawable.custom_checked_radio);
                break;
            case "C":
                holder.status.setText("Delivery");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_field_orange);
                break;
            case "E":
                holder.status.setText("Declined");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_field_red);
                break;
            case "F":
                holder.status.setText("Cancelled");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_field_red);
                break;
            case "D":
                holder.status.setText("Delivered");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_field_green);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name, status, qty, total;

        public MyViewHolder(@NonNull View itemView, OnOrderClickListener onOrderClickListener) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.short_order_pic);
            name = (TextView) itemView.findViewById(R.id.short_order_name);
            qty = (TextView) itemView.findViewById(R.id.short_order_qty);
            total = (TextView) itemView.findViewById(R.id.short_order_total_price);
            status = (TextView) itemView.findViewById(R.id.short_order_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOrderClickListener.OnOrderClick(getAdapterPosition(), v);
        }
    }

    public interface OnOrderClickListener {
        void OnOrderClick(int position, View view);
    }
}
