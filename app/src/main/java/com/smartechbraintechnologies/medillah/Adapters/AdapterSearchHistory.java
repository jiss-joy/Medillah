package com.smartechbraintechnologies.medillah.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.R;

import java.util.ArrayList;

public class AdapterSearchHistory extends RecyclerView.Adapter<AdapterSearchHistory.MyViewHolder> {

    private final Context mContext;
    private final ArrayList<String> mHistory;
    private OnSearchHistoryClickListener onSearchHistoryClickListener;

    public AdapterSearchHistory(Context mContext, ArrayList<String> mHistory, OnSearchHistoryClickListener onSearchHistoryClickListener) {
        this.mContext = mContext;
        this.mHistory = mHistory;
        this.onSearchHistoryClickListener = onSearchHistoryClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterSearchHistory.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_history, parent, false), onSearchHistoryClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.history.setText(mHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView history;

        public MyViewHolder(@NonNull View itemView, OnSearchHistoryClickListener onSearchHistoryClickListener) {
            super(itemView);
            history = (TextView) itemView.findViewById(R.id.item_search_history_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSearchHistoryClickListener.OnSearchHistoryClick(getAdapterPosition(), v);
        }
    }

    public interface OnSearchHistoryClickListener {
        void OnSearchHistoryClick(int position, View view);
    }
}
