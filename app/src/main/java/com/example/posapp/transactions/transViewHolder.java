package com.example.posapp.transactions;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class transViewHolder extends RecyclerView.ViewHolder {
    public TextView transIDP, tQuantityP, tPriceP, dateP, pCategory;
    public CardView cardView;

    public transViewHolder(@NonNull View itemView) {
        super(itemView);
        transIDP = itemView.findViewById(R.id.transIDP);
        tQuantityP = itemView.findViewById(R.id.tQuantityP);
        tPriceP = itemView.findViewById(R.id.tPriceP);
        dateP = itemView.findViewById(R.id.dateP);
        pCategory = itemView.findViewById(R.id.pCategory);
        cardView = itemView.findViewById(R.id.transContainer);
    }
}
