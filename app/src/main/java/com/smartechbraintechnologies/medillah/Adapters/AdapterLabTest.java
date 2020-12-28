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

import com.smartechbraintechnologies.medillah.Models.ModelLabTest;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLabTest extends RecyclerView.Adapter<AdapterLabTest.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelLabTest> labItems;
    private OnLabTestClickListener onLabTestClickListener;

    public AdapterLabTest(Context mContext, ArrayList<ModelLabTest> labItems, OnLabTestClickListener onLabTestClickListener) {
        this.mContext = mContext;
        this.labItems = labItems;
        this.onLabTestClickListener = onLabTestClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterLabTest.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lab_test, parent, false), onLabTestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(labItems.get(position).getTestImage()).into(holder.image);
        holder.name.setText(labItems.get(position).getTestName());
        holder.mrp.setText("₹" + String.valueOf(labItems.get(position).getTestMRP()));
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sp.setText("₹" + String.valueOf(labItems.get(position).getTestSellingPrice()));
    }

    @Override
    public int getItemCount() {
        return labItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name, mrp, sp;

        public MyViewHolder(@NonNull View itemView, OnLabTestClickListener onLabTestClickListener) {
            super(itemView);

            image = itemView.findViewById(R.id.item_lab_test_image);
            name = itemView.findViewById(R.id.item_lab_test_name);
            mrp = itemView.findViewById(R.id.item_lab_test_mrp);
            sp = itemView.findViewById(R.id.item_lab_test_sp);

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
