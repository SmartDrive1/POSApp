package com.example.posapp.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class CakeListAdapter extends RecyclerView.Adapter<prodViewHolder> {

    Context context;
    List<prodItems> items;
    prodClickListener mClickListener;

    public CakeListAdapter(Context context, List<prodItems> items, prodClickListener mClickListener) {
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
        holder.bind(items.get(position), mClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
