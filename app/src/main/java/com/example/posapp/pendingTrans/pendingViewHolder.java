package com.example.posapp.pendingTrans;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class pendingViewHolder extends RecyclerView.ViewHolder {
    public TextView transID, status, orderTime;
    public CardView cardView;

    public pendingViewHolder(@NonNull View itemView){
        super(itemView);
        transID = itemView.findViewById(R.id.transID);
        status = itemView.findViewById(R.id.status);
        orderTime = itemView.findViewById(R.id.orderTime);
        cardView = itemView.findViewById(R.id.pending);
    }
}
