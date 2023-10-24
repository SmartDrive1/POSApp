package com.example.posapp.OrderingSystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class OSPaymentAdapter extends RecyclerView.Adapter<OSViewHolder> {
    Context context;
    List<OSItems> items;

    public OSPaymentAdapter(Context context, List<OSItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public OSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OSViewHolder(LayoutInflater.from(context).inflate(R.layout.cartcardview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull OSViewHolder holder, int position) {
        holder.nameP.setText(items.get(position).getpName());
        holder.quantityP.setText(items.get(position).getpQuantity());
        holder.priceP.setText(items.get(position).getpPrice() + ".00");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
