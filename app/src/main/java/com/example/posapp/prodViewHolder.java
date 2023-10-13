package com.example.posapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class prodViewHolder extends RecyclerView.ViewHolder {

    TextView txtID, txtName, txtCat, txtPrice;

    public prodViewHolder(@NonNull View itemView) {
        super(itemView);

        txtID = itemView.findViewById(R.id.prodID);
        txtName = itemView.findViewById(R.id.prodName);
        txtCat = itemView.findViewById(R.id.prodCat);
        txtPrice = itemView.findViewById(R.id.prodPrice);
    }
}
