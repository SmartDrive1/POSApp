package com.example.posapp.pendingTrans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class servingAdapter extends RecyclerView.Adapter<servingViewholder> {
    Context context;
    List<servingItems> items;

    public servingAdapter(Context context, List<servingItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public servingViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new servingViewholder(LayoutInflater.from(context).inflate(R.layout.preparingviewholder, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull servingViewholder holder, int position) {
        holder.transID.setText(items.get(position).getTransID());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
