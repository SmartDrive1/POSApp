package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class productList extends AppCompatActivity {

    ListView lstInventory;
    Button btnBack, btnAddProduct;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        btnBack = findViewById(R.id.btnBack);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        lstInventory = findViewById(R.id.lstInventory);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this,MainScreen.class);
                startActivity(i);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, addproduct.class);
                startActivity(i);
            }
        });

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from products", null);
        int id = c.getColumnIndex("id");
        int product = c.getColumnIndex("product");
        int category = c.getColumnIndex("category");
        int prodPrice = c.getColumnIndex("prodPrice");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstInventory.setAdapter(arrayAdapter);

        final ArrayList<cProd> prods = new ArrayList<cProd>();
        if(c.moveToFirst())
        {
            do{
                cProd pr = new cProd();
                pr.id = c.getString(id);
                pr.product = c.getString(product);
                pr.category = c.getString(category);
                pr.prodPrice = c.getString(prodPrice);
                prods.add(pr);

                titles.add(c.getString(id) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(product) + "\t\t\t\t\t\t\t" + c.getString(category) + " \t " + c.getString(prodPrice));

            }while(c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
            lstInventory.invalidateViews();
        }

        lstInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String a = titles.get(position).toString();
                cProd pr = prods.get((position));
                Intent i = new Intent(getApplicationContext(), editProduct.class);
                i.putExtra("id",pr.id);
                i.putExtra("product",pr.product);
                i.putExtra("prodPrice",pr.prodPrice);
                i.putExtra("category",pr.category);
                startActivity(i);

            }
        });
    }

}