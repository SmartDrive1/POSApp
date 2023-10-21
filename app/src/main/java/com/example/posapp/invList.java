package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class invList extends AppCompatActivity implements invClickListener {

    Button btnBack, btnAddItem;
    invListAdapter invListAdapter;
    List<invItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_list);

        btnBack = findViewById(R.id.btnBack);
        btnAddItem = findViewById(R.id.btnAddItem);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this,MainScreen.class);
                startActivity(i);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this, invAdd.class);
                startActivity(i);
            }
        });

        refreshList();
    }

    public void refreshList(){
        RecyclerView invRecycleView = findViewById(R.id.recycleInv);
        invRecycleView.setHasFixedSize(true);
        invRecycleView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY, itemName VARCHAR, stock INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from inventory", null);
        int count = c.getCount();

        if(count == 0){
            c.close();
            db.close();
            Toast.makeText(this,"No Items Found", Toast.LENGTH_LONG).show();
        }else {
            int id = c.getColumnIndex("id");
            int itemName = c.getColumnIndex("itemName");
            int stock = c.getColumnIndex("stock");

            if(c.moveToFirst()){
                do{
                    items.add(new invItems(c.getString(id), c.getString(itemName), c.getString(stock)));
                    invListAdapter = new invListAdapter(this, items, this);
                    invRecycleView.setAdapter(invListAdapter);
                }while (c.moveToNext());
            }
            c.close();
            db.close();
        }
    }

    @Override
    public void onItemClicked(invItems view) {
        Intent i = new Intent(getApplicationContext(), invEdit.class);
        i.putExtra("id", view.getItemID());
        i.putExtra("itemName", view.getItemName());
        i.putExtra("itemStock", view.getItemStock());
        startActivity(i);
    }
}