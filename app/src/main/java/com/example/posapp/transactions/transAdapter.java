package com.example.posapp.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class transAdapter extends RecyclerView.Adapter<transViewHolder> {
    Context context;
    List<transItems> items;
    transClickListener mClickListener;

    public transAdapter(Context context, List<transItems> items, transClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public transViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new transViewHolder(LayoutInflater.from(context).inflate(R.layout.transcardview, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull transViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.transIDP.setText(items.get(position).getTransID());
        holder.tQuantityP.setText(items.get(position).getpQuantity());
        holder.tPriceP.setText(items.get(position).getpPrice() + ".00");
        holder.dateP.setText(items.get(position).gettDate());

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
