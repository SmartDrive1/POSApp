package com.example.posapp.OrderingSystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class OSAdapter extends RecyclerView.Adapter<OSViewHolder> {
    Context context;
    List<OSItems> items;
    cartClickListener mClickListener;

    public OSAdapter(Context context, List<OSItems> items, cartClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public OSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OSViewHolder(LayoutInflater.from(context).inflate(R.layout.cartcardview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull OSViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameP.setText(items.get(position).getpName());
        holder.quantityP.setText(items.get(position).getpQuantity());
        holder.priceP.setText(items.get(position).getpPrice() + ".00");

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
