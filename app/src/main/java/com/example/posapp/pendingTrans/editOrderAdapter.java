package com.example.posapp.pendingTrans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class editOrderAdapter extends RecyclerView.Adapter<orderEditViewHolder> {
    Context context;
    List<orderEditItems> items;

    public editOrderAdapter(Context context, List<orderEditItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public orderEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new orderEditViewHolder(LayoutInflater.from(context).inflate(R.layout.pendingordercardview, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull orderEditViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.pName.setText(items.get(position).getProdName());
        holder.pQuantity.setText(items.get(position).getpQuantity());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
