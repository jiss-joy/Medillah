package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ornach.nobobutton.NoboButton;
import com.smartechbraintechnologies.medillah.Models.ModelCartItem;
import com.smartechbraintechnologies.medillah.R;

import java.util.ArrayList;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelCartItem> cartItems;
    private OnDeleteClickListener onDeleteClickListener;

    public AdapterCartItem(Context mContext, ArrayList<ModelCartItem> cartItems, OnDeleteClickListener onDeleteClickListener) {
        this.mContext = mContext;
        this.cartItems = cartItems;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterCartItem.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cart_product, parent, false), onDeleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.product_name.setText(cartItems.get(position).getProductName());
        holder.product_brand.setText(cartItems.get(position).getProductBrand());
        holder.product_sp.setText("₹" + cartItems.get(position).getProductSellingPrice());
        holder.product_mrp.setText("₹" + cartItems.get(position).getProductMRP());
        holder.product_mrp.setPaintFlags(holder.product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.product_discount.setText(cartItems.get(position).getProductDiscount() + "%");
        holder.product_qty.setText(String.valueOf(cartItems.get(position).getProductQuantity()));
        Glide.with(mContext).load(cartItems.get(position).getProductImage()).placeholder(R.drawable.orders).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView product_brand;
        private TextView product_name;
        private TextView product_sp;
        private TextView product_mrp;
        private TextView product_discount;
        private TextView product_qty;

        private NoboButton delete;

        public MyViewHolder(@NonNull View itemView, OnDeleteClickListener onDeleteClickListener) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_cart_product_brand_image);
            product_brand = itemView.findViewById(R.id.item_cart_product_brand_name);
            product_name = itemView.findViewById(R.id.item_cart_product_name);
            product_sp = itemView.findViewById(R.id.item_cart_product_sp);
            product_mrp = itemView.findViewById(R.id.item_cart_product_mrp);
            product_qty = itemView.findViewById(R.id.item_cart_product_quantity);
            product_discount = itemView.findViewById(R.id.item_cart_product_discount);

            delete = itemView.findViewById(R.id.item_cart_product_delete_btn);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClickListener.onDeleteClick(getAdapterPosition(), v);
                }
            });

        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position, View v);
    }
}
