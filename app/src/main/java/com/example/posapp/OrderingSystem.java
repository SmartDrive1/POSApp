package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ScrollCaptureSession;
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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
                Intent i = new Intent(OrderingSystem.this, cart.class);
                startActivity(i);
            }
        });

        getProducts();

    }

    public void getProducts(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )");//incase there are no tables yet
        final Cursor c = db.rawQuery("select * from products", null);
        int product = c.getColumnIndex("product");

        getP.clear();
        arrayAdapter = new ArrayAdapter(this, R.layout.spinnerlayout,getP);
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

    public void add(){
        String[] orderItems = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

        TemporaryContainer container = new TemporaryContainer(orderItems);
        cCurrentTransac.setCurrentTransaction(container);

    }
}
