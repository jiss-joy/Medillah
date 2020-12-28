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

import com.smartechbraintechnologies.medillah.Models.ModelMyAppointments;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMyAppointments extends RecyclerView.Adapter<AdapterMyAppointments.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelMyAppointments> appointmentItems;
    private OnAppointmentClickListener onAppointmentClickListener;

    public AdapterMyAppointments(Context mContext, ArrayList<ModelMyAppointments> appointmentItems, OnAppointmentClickListener onAppointmentClickListener) {
        this.mContext = mContext;
        this.appointmentItems = appointmentItems;
        this.onAppointmentClickListener = onAppointmentClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterMyAppointments.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_my_appointment, parent, false), onAppointmentClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(appointmentItems.get(position).getConsultationDoctorImage()).into(holder.image);
        holder.name.setText(appointmentItems.get(position).getConsultationDoctorName());
        holder.speciality.setText(appointmentItems.get(position).getConsultationDoctorSpeciality());
        holder.timing.setText(String.valueOf(appointmentItems.get(position).getConsultationTiming()));
        holder.status.setText(String.valueOf(appointmentItems.get(position).getConsultationStatus()));

        switch (appointmentItems.get(position).getConsultationStatus()) {
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
                holder.status.setText("In Progress");
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
                holder.status.setText("Completed");
                holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.status.setBackgroundResource(R.drawable.custom_field_green);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return appointmentItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name, speciality, timing, status;

        public MyViewHolder(@NonNull View itemView, OnAppointmentClickListener onAppointmentClickListener) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.my_appointments_image);
            name = (TextView) itemView.findViewById(R.id.my_appointments_name);
            timing = (TextView) itemView.findViewById(R.id.my_appointments_timing);
            speciality = (TextView) itemView.findViewById(R.id.my_appointments_speciality);
            status = (TextView) itemView.findViewById(R.id.my_appointments_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAppointmentClickListener.OnAppointmentClick(getAdapterPosition(), v);
        }
    }

    public interface OnAppointmentClickListener {
        void OnAppointmentClick(int position, View view);
    }
}
