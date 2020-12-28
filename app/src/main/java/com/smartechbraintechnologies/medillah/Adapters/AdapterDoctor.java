package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.Models.ModelDoctor;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterDoctor extends RecyclerView.Adapter<AdapterDoctor.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelDoctor> docItems;
    private OnDocClickListener onDocClickListener;

    public AdapterDoctor(Context mContext, ArrayList<ModelDoctor> docItems, OnDocClickListener onDocClickListener) {
        this.mContext = mContext;
        this.docItems = docItems;
        this.onDocClickListener = onDocClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterDoctor.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_doctor, parent, false), onDocClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(docItems.get(position).getDoctorImage()).into(holder.image);
        holder.name.setText(docItems.get(position).getDoctorName());
        holder.mrp.setText("₹" + String.valueOf(docItems.get(position).getDoctorMRP()));
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.cp.setText("₹" + String.valueOf(docItems.get(position).getDoctorConsultationPrice()));
        Double yoe = docItems.get(position).getDoctorExperience();
        holder.exp.setText(yoe.intValue() + "+ years of experience");
    }

    @Override
    public int getItemCount() {
        return docItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name, mrp, cp, exp;

        public MyViewHolder(@NonNull View itemView, OnDocClickListener onDocClickListener) {
            super(itemView);

            image = itemView.findViewById(R.id.item_doctor_image);
            name = itemView.findViewById(R.id.item_doctor_name);
            mrp = itemView.findViewById(R.id.item_doctor_mrp);
            cp = itemView.findViewById(R.id.item_doctor_cp);
            exp = itemView.findViewById(R.id.item_doctor_exp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDocClickListener.OnDocClick(getAdapterPosition(), v);
        }
    }

    public interface OnDocClickListener {
        void OnDocClick(int position, View view);
    }
}
