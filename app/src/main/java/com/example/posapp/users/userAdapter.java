package com.example.posapp.users;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userViewHolder> {
    Context context;
    List<userItems> items;
    userClickListener mClickListener;

    public userAdapter(Context context, List<userItems> items, userClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new userViewHolder(LayoutInflater.from(context).inflate(R.layout.userrecycleview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.userID.setText("ID: " + items.get(position).getId());
        holder.fName.setText("Name: " + items.get(position).getFullName());
        holder.uName.setText("Username: " + items.get(position).getUserName());
        holder.password.setText("Password: " + items.get(position).getPassword());
        holder.access.setText("Access: " + items.get(position).getAccess());
        
        holder.cardview.setOnClickListener(new View.OnClickListener() {
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
