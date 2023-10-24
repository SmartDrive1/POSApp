package com.example.posapp.users;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class userViewHolder extends RecyclerView.ViewHolder {
    public TextView userID, fName, uName, password, access;
    public CardView cardview;

    public userViewHolder(@NonNull View itemView) {
        super(itemView);

        userID = itemView.findViewById(R.id.userID);
        fName = itemView.findViewById(R.id.fName);
        uName = itemView.findViewById(R.id.uName);
        password = itemView.findViewById(R.id.password);
        access = itemView.findViewById(R.id.access);
        cardview = itemView.findViewById(R.id.userContainer);
    }
}
