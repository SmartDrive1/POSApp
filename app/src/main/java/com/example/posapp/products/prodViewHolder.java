package com.example.posapp.products;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

        String base64Image = item.getProdImage();

        if (base64Image != null) {
            // Decode Base64 string to byte array
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Set the Bitmap to the ImageView
            prodImg.setImageBitmap(bitmap);
        } else {
            // Handle the case when the Base64 image string is null
            prodImg.setImageResource(R.drawable.noimage);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(item);
            }
        });
    }
}
