package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class OrderingSystem extends AppCompatActivity {
    Button btnAdd, btnBack;
    EditText txtCategory, Quantity, Price;
    Spinner sp1;
    ArrayList<String> getP = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_system);

        btnAdd = findViewById(R.id.btnCart);
        btnBack = findViewById(R.id.btnCancel);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        sp1 = findViewById(R.id.prodName);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderingSystem.this, MainScreen.class);
                startActivity(i);
            }
        });

        getProducts();

    }

    public void getProducts(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        final Cursor c = db.rawQuery("select * from products", null);
        int product = c.getColumnIndex("product");

        getP.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,getP);
        sp1.setAdapter(arrayAdapter);

        final ArrayList<cProd> prods = new ArrayList<cProd>();
        if(c.moveToFirst())
        {
            do{
                cProd pr = new cProd();
                pr.product = c.getString(product);
                prods.add(pr);

                getP.add(c.getString(product));

            }while(c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
