package com.example.posapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class prodViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtPrice;
    public CardView cardView;

    public prodViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.prodName);
        txtPrice = itemView.findViewById(R.id.prodPrice);
        cardView = itemView.findViewById(R.id.main_container);
    }
}
