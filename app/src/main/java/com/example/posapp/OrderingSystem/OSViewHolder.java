package com.example.posapp.OrderingSystem;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class OSViewHolder extends RecyclerView.ViewHolder {
    public TextView nameP, quantityP, priceP;
    public CardView cardview;
    ImageButton btnEdit;

    public OSViewHolder(@NonNull View itemView) {
        super(itemView);
        nameP = itemView.findViewById(R.id.nameP);
        quantityP = itemView.findViewById(R.id.quantityP);
        priceP = itemView.findViewById(R.id.priceP);
        cardview = itemView.findViewById(R.id.cartContainer);
        btnEdit = itemView.findViewById(R.id.btnEdit);
    }
}
