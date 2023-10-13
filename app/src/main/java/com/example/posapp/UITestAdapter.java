package com.example.posapp;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UITestAdapter extends RecyclerView.Adapter<prodViewHolder> {

    Context context;
    List<UITestItems> items;

    public UITestAdapter(Context context, List<UITestItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public prodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new prodViewHolder(LayoutInflater.from(context).inflate(R.layout.recycletrans,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull prodViewHolder holder, int position) {
        holder.txtID.setText(items.get(position).getId());
        holder.txtName.setText(items.get(position).getProduct());
        holder.txtCat.setText(items.get(position).getCategory());
        holder.txtPrice.setText(items.get(position).getProdPrice());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
