package com.example.posapp.pendingTrans;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class servingViewholder extends RecyclerView.ViewHolder {
    public TextView transID;

    public servingViewholder(@NonNull View itemView) {
        super(itemView);

        transID = itemView.findViewById(R.id.transID);
    }
}
