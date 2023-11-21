package com.example.posapp.products;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class prodDrinksListAdapter extends RecyclerView.Adapter<prodViewHolder> {

    Context context;
    List<prodItems> items;
    prodClickListener mClickListener;

    public prodDrinksListAdapter(Context context, List<prodItems> items, prodClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public prodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new prodViewHolder(LayoutInflater.from(context).inflate(R.layout.prodrecycleview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull prodViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtName.setText(items.get(position).getProduct());
        holder.txtQuantity.setText("Stock: " + items.get(position).getQuantity());
        holder.txtPrice.setText("Price: " + items.get(position).getProdPrice() + ".00");

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


