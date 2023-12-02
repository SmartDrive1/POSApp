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

public class pendingAdapter extends RecyclerView.Adapter<pendingViewHolder> {
    Context context;
    List<pendingItems> items;
    pendingClickListener mClickListener;

    public pendingAdapter(Context context, List<pendingItems> items, pendingClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public pendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new pendingViewHolder(LayoutInflater.from(context).inflate(R.layout.pending_trans, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull pendingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.transID.setText(items.get(position).getTransID());
        holder.status.setText(items.get(position).getStatus());
        holder.orderTime.setText(items.get(position).getOrderTime());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
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
