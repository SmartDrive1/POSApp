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
import android.widget.TextView;

import java.util.ArrayList;

public class cart extends AppCompatActivity {

    ListView lstCart1;
    ArrayList<String> titles = new ArrayList <String>();
    TextView total;
    ArrayAdapter arrayAdapter;
    Button btnBack, payment;
    Double tPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btnBack = findViewById(R.id.btnBack);
        lstCart1 = findViewById(R.id.lstCart1);
        payment = findViewById(R.id.btnPayment);
        total = findViewById(R.id.totalPrice);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cart.this, OrderingSystem.class);
                startActivity(i);
            }
        });
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(prodName VARCHAR PRIMARY KEY,quantity INTEGER, price DOUBLE)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from cartlist", null);
        int prodName = c.getColumnIndex("prodName");
        int quantity = c.getColumnIndex("quantity");
        int price = c.getColumnIndex("price");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstCart1.setAdapter(arrayAdapter);

        final ArrayList<carttmp> cart = new ArrayList<carttmp>();
        if(c.moveToFirst())
        {
            do{
                carttmp pr = new carttmp();
                pr.prodName = c.getString(prodName);
                pr.quantity = c.getString(quantity);
                pr.price = c.getString(price);

                tPrice = tPrice + Double.parseDouble(c.getString(price));//edit
                cart.add(pr);

                titles.add(c.getString(prodName) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(quantity) + "\t\t\t\t\t\t\t" + c.getString(price));

            }while(c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
            lstCart1.invalidateViews();
        }

        lstCart1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String a = titles.get(position).toString();
                carttmp pr = cart.get((position));
                Intent i = new Intent(getApplicationContext(), editOrder.class);
                i.putExtra("prodName",pr.prodName);
                i.putExtra("quantity",pr.quantity);
                i.putExtra("price",pr.price);
                startActivity(i);

            }
        });

        total.setText("Total: " + tPrice.toString());
    }
}