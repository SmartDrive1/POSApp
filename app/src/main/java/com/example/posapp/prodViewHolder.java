package com.example.posapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class prodViewHolder extends RecyclerView.ViewHolder {

    public TextView txtID, txtName, txtCat, txtPrice;
    public CardView cardView;

    public prodViewHolder(@NonNull View itemView) {


        super(itemView);

        txtID = itemView.findViewById(R.id.prodID);
        txtName = itemView.findViewById(R.id.prodName);
        txtCat = itemView.findViewById(R.id.prodCat);
        txtPrice = itemView.findViewById(R.id.prodPrice);
        cardView = itemView.findViewById(R.id.main_container);
    }
}
