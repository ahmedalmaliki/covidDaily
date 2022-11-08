package com.example.coviddaily;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> counts ;
    private ArrayList<String> dates;
    private Context context;
    private static final String TAG = "RecyclerAdapterDebug";

    public RecyclerViewAdapter(ArrayList<String> counts, ArrayList<String> dates, Context context) {
        Log.d(TAG, dates.toString());

        this.counts = counts;
        this.dates = dates;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView count, date;
        LinearLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            date = itemView.findViewById(R.id.date);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_items, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.count.setText(counts.get(position));
        holder.date.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}
