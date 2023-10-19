package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class productList extends AppCompatActivity implements prodClickListener {

    Button btnAddProduct, back;
    productListAdapter UIAdapter;
    List<prodItems> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        back = findViewById(R.id.btnBack);
        btnAddProduct = findViewById(R.id.btnAddProduct);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, MainScreen.class);
                startActivity(i);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, prodAdd.class);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from products", null);
        int id = c.getColumnIndex("id");
        int product = c.getColumnIndex("product");
        int category = c.getColumnIndex("category");
        int prodPrice = c.getColumnIndex("prodPrice");
        items = new ArrayList<>();

        if(c.moveToFirst()){
            do{
                items.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        UIAdapter = new productListAdapter(this, items, this);
        recyclerView.setAdapter(UIAdapter);
    }

    @Override
    public void onItemClicked(prodItems view) {
        Intent i = new Intent(getApplicationContext(), prodEdit.class);
        i.putExtra("id", view.getId());
        i.putExtra("product", view.getProduct());
        i.putExtra("prodPrice", view.getProdPrice());
        i.putExtra("category", view.getCategory());
        startActivity(i);
    }
}