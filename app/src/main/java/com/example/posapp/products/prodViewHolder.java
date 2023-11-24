package com.example.posapp.products;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class prodViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtPrice, txtQuantity;
    public ImageView prodImg;
    public CardView cardView;

    public prodViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.prodName);
        txtPrice = itemView.findViewById(R.id.prodPrice);
        txtQuantity = itemView.findViewById(R.id.quantity);
        prodImg = itemView.findViewById(R.id.prodImg);
        cardView = itemView.findViewById(R.id.main_container);
    }

    public void bind(prodItems item, prodClickListener clickListener) {
        txtName.setText(item.getProduct());
        txtPrice.setText("Price: " + item.getProdPrice() + ".00");
        txtQuantity.setText("Stock: " + item.getQuantity());

        // Get the Bitmap from the prodImage byte array
        Bitmap bitmap = item.getProdImageBitmap();

        if (bitmap != null) {
            // Set the Bitmap to the ImageView
            prodImg.setImageBitmap(bitmap);
        } else {
            // Handle the case when the Bitmap is null
            prodImg.setImageResource(R.drawable.noimage); // Set a default image or do something else
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(item);
            }
        });
    }
}
