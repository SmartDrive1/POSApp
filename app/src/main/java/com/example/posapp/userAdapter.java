package com.example.posapp;

import android.content.Context;

import java.util.List;

public class userAdapter {
    Context context;
    List<userItems> items;
    userClickListener mClickListener;

    public userAdapter(Context context, List<userItems> items, userClickListener mClickListener) {
        this.context = context;
        this.items = items;
        this.mClickListener = mClickListener;
    }
}
