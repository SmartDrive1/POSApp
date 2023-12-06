package com.example.posapp.transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class transEditAdapter extends RecyclerView.Adapter<transEditViewHolder> {
    Context context;
    List<transEditItems> items;

    public transEditAdapter(Context context, List<transEditItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public transEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new transEditViewHolder(LayoutInflater.from(context).inflate(R.layout.transeditcardview, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull transEditViewHolder holder, int position) {
        holder.pName.setText(items.get(position).getpName());
        holder.pQuantity.setText(items.get(position).getpQuantity());
        holder.pCategory.setText(items.get(position).getpCategory());
        holder.pPrice.setText(items.get(position).getpPrice());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
