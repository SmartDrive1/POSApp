package com.example.posapp.pendingTrans;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class orderEditViewHolder extends RecyclerView.ViewHolder {
    public TextView pName, pQuantity;
    public CardView orderEditContainer;


    public orderEditViewHolder(@NonNull View itemView) {
        super(itemView);
        pName = itemView.findViewById(R.id.pName);
        pQuantity = itemView.findViewById(R.id.pQuantity);

        orderEditContainer = itemView.findViewById(R.id.orderEditContainer);
    }
}
