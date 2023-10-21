package com.example.posapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class invListAdapter extends RecyclerView.Adapter<invViewHolder> {
    Context context;
    List<invItems> items;
    invClickListener mClickListener;

    public invListAdapter(Context context, List<invItems> items, invClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public invViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new invViewHolder(LayoutInflater.from(context).inflate(R.layout.invrecycleview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull invViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemID.setText("ID: " + items.get(position).getItemID());
        holder.itemName.setText("Name: " + items.get(position).getItemName());
        holder.itemStock.setText("Stock: " + items.get(position).getItemStock());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClicked(items.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
