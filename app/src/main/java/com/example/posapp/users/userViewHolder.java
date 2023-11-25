package com.example.posapp.users;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.posapp.R;

public class userViewHolder extends RecyclerView.ViewHolder {
    public TextView userID, fName, uName, password, access;
    public ImageView userImg;
    public CardView cardview;

    public userViewHolder(@NonNull View itemView) {
        super(itemView);

        userID = itemView.findViewById(R.id.userID);
        fName = itemView.findViewById(R.id.fName);
        uName = itemView.findViewById(R.id.uName);
        password = itemView.findViewById(R.id.password);
        access = itemView.findViewById(R.id.access);
        userImg = itemView.findViewById(R.id.userImg);
        cardview = itemView.findViewById(R.id.userContainer);
    }

    public void bind (userItems item, userClickListener clickListener){
        userID.setText("ID: " + item.getId());
        fName.setText("Name: " + item.getFullName());
        uName.setText("Username: " + item.getUserName());
        password.setText("Password: " + item.getPassword());
        access.setText("Access: " + item.getAccess());

        Bitmap bitmap = item.getUserImageBitmap();

        if (bitmap != null) {
            // Set the Bitmap to the ImageView
            userImg.setImageBitmap(bitmap);
        } else {
            // Handle the case when the Bitmap is null
            userImg.setImageResource(R.drawable.noimage); // Set a default image or do something else
        }

        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(item);
            }
        });
    }
}
