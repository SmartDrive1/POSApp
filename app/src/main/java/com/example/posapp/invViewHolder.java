package com.example.posapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class invViewHolder extends RecyclerView.ViewHolder {
    public TextView itemID, itemName, itemStock;
    public CardView cardView;

    public invViewHolder(@NonNull View itemView) {
        super(itemView);

        itemID = itemView.findViewById(R.id.itemID);
        itemName = itemView.findViewById(R.id.itemName);
        itemStock = itemView.findViewById(R.id.itemStock);
        cardView = itemView.findViewById(R.id.invContainer);
    }
}
