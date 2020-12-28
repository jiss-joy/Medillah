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

import com.smartechbraintechnologies.medillah.Models.ModelMyLabTest;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMyLabTests extends RecyclerView.Adapter<AdapterMyLabTests.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelMyLabTest> labItems;
    private OnLabTestClickListener onLabTestClickListener;

    public AdapterMyLabTests(Context mContext, ArrayList<ModelMyLabTest> labItems, OnLabTestClickListener onLabTestClickListener) {
        this.mContext = mContext;
        this.labItems = labItems;
        this.onLabTestClickListener = onLabTestClickListener;
    }

    @NonNull
    @Override
    public AdapterMyLabTests.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterMyLabTests.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_my_lab_test, parent, false), onLabTestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyLabTests.MyViewHolder holder, int position) {
        Picasso.get().load(labItems.get(position).getTestImage()).into(holder.image);
        holder.name.setText(labItems.get(position).getTestName());
        holder.timing.setText(String.valueOf(labItems.get(position).getTestTiming()));

        switch (labItems.get(position).getTestStatus()) {
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
        return labItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name, timing, status;

        public MyViewHolder(@NonNull View itemView, OnLabTestClickListener onLabTestClickListener) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.my_lab_test_image);
            name = (TextView) itemView.findViewById(R.id.my_lab_test_name);
            timing = (TextView) itemView.findViewById(R.id.my_lab_test_timing);
            status = (TextView) itemView.findViewById(R.id.my_lab_test_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLabTestClickListener.OnLabTestClick(getAdapterPosition(), v);
        }
    }

    public interface OnLabTestClickListener {
        void OnLabTestClick(int position, View view);
    }
}
