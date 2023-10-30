package com.example.posapp.transactions;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class transEditViewHolder extends RecyclerView.ViewHolder {
    public TextView pName, pQuantity, pPrice;
    public CardView transEditContainer;

    public transEditViewHolder(@NonNull View itemView) {
        super(itemView);
        pName = itemView.findViewById(R.id.pName);
        pQuantity = itemView.findViewById(R.id.pQuantity);
        pPrice = itemView.findViewById(R.id.pPrice);
        transEditContainer = itemView.findViewById(R.id.transEditContainer);
    }
}
