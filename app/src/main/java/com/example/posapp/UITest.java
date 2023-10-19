package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UITest extends AppCompatActivity implements ItemClickListener {

    Button back;
    UITestAdapter UIAdapter;
    List<UITestItems> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitest);

        back = findViewById(R.id.btnBack);
        RecyclerView recyclerView = findViewById(R.id.recycleProds);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UITest.this, MainScreen.class);
                startActivity(i);
            }
        });


        loadProducts();
    }

    public void loadProducts(){
        RecyclerView recyclerView = findViewById(R.id.recycleProds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from products", null);
        int id = c.getColumnIndex("id");
        int product = c.getColumnIndex("product");
        int category = c.getColumnIndex("category");
        int prodPrice = c.getColumnIndex("prodPrice");
        items = new ArrayList<UITestItems>();

        if(c.moveToFirst()){
            do{
                items.add(new UITestItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
            }while(c.moveToNext());
        }

        //recyclerView.setAdapter(new UITestAdapter(getApplicationContext(), items, this));
        UIAdapter = new UITestAdapter(this, items, this);
        recyclerView.setAdapter(UIAdapter);
    }

    @Override
    public void onItemClicked(UITestItems view) {
        Toast.makeText(this, view.getProduct(), Toast.LENGTH_SHORT).show();
    }
}