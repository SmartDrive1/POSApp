package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.ScrollCaptureSession;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class OrderingSystem extends AppCompatActivity {
    Button btnAdd, btnBack, Orders;
    EditText Quantity, Price, totalPriceUp, prodName;
    ListView lstProds;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderingsystem);


        btnAdd = findViewById(R.id.btnCart);
        btnBack = findViewById(R.id.btnCancel);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        lstProds = findViewById(R.id.lstProds);
        prodName = findViewById(R.id.prodName);
        totalPriceUp = findViewById(R.id.txtTotalPrice);
        Orders = findViewById(R.id.btnOrders);
        final ArrayList<cProd> prods = new ArrayList<cProd>();

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

                Quantity.setText("");
                totalPriceUp.setText("");
            }
        });

        Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderingSystem.this, cart.class);
                startActivity(i);
            }
        });


        refreshList();

        String a = titles.get(0);
        prodName.setText(a);
        updatePrice();

        lstProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String a = titles.get(position).toString();
                prodName.setText(a);
                updatePrice();
            }
        });
        change();
    }

    public void add() {
        if(prodName.getText().toString().equals("")){//Check if ProdName is Blank
            Toast.makeText(this, "Please Select a Product", Toast.LENGTH_LONG).show();
        }else if (Quantity.getText().toString().equals("")) {//check if blank
            Toast.makeText(this, "Please Input a Valid Quantity", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(String.valueOf(Quantity.getText())) <= 0) {//check if more than 0
            Toast.makeText(this, "Please Input Quantity More Than 0", Toast.LENGTH_LONG).show();
        }else try {
            total();
            String tPrice1 = totalPriceUp.getText().toString();
            String qty2 = Quantity.getText().toString();
            String prodName1 = prodName.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(prodName VARCHAR PRIMARY KEY, quantity INTEGER, price DOUBLE)");

            // Check if an item with the same prodName already exists
            Cursor cursor = db.rawQuery("SELECT * FROM cartlist WHERE prodName=?", new String[]{prodName1});
            if (cursor.getCount() > 0) {
                // Update the existing item's quantity and price
                db.execSQL("UPDATE cartlist SET quantity=?, price=? WHERE prodName=?", new String[]{qty2, tPrice1, prodName1});
                Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();
            } else {
                // Insert a new item
                String sql = "INSERT INTO cartlist (prodName, quantity, price) VALUES (?, ?, ?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, prodName1);
                statement.bindString(2, String.valueOf(Integer.parseInt(qty2)));
                statement.bindString(3, String.valueOf(Double.parseDouble(tPrice1)));
                statement.execute();
                Toast.makeText(this, "Item Added to Cart", Toast.LENGTH_LONG).show();
            }

            Quantity.setText("");
            totalPriceUp.setText("");
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }

    }

    public void updatePrice() {
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER)");//in case there are no tables yet
        final Cursor c = db.rawQuery("SELECT * FROM products WHERE product ='" + prodName.getText() + "'", null);

        if (c.moveToFirst()) {
            int prodPriceIndex = c.getColumnIndex("prodPrice");
            int productPrice = c.getInt(prodPriceIndex);
            Price.setText(String.valueOf(productPrice));
        } else {
            Price.setText("N/A");
        }
        c.close();
        db.close();
    }

    public void total(){
        if (String.valueOf(Quantity.getText()).equals("")){
            Toast.makeText(OrderingSystem.this,"Please Input a Quantity", Toast.LENGTH_LONG).show();
        }else if(Integer.parseInt(String.valueOf(Quantity.getText())) <= 0){
            Toast.makeText(OrderingSystem.this,"Quantity Must Be Greater Than 1", Toast.LENGTH_LONG).show();
        }else{
            double qty1 = Double.parseDouble(Quantity.getText().toString());
            double price1 = Double.parseDouble(Price.getText().toString());
            double totalPrice = qty1 * price1;

            totalPriceUp.setText(String.valueOf(Double.valueOf(totalPrice)));
        }
    }

    public void refreshList() {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
            final Cursor c = db.rawQuery("select * from products", null);
            int product = c.getColumnIndex("product");

            titles.clear();
            arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, titles);
            lstProds.setAdapter(arrayAdapter);

            final ArrayList<cProd> prods = new ArrayList<cProd>();
            if (c.moveToFirst()) {
                do {
                    cProd pr = new cProd();
                    pr.product = c.getString(product);
                    prods.add(pr);

                    titles.add(c.getString(product));

                } while (c.moveToNext());
                arrayAdapter.notifyDataSetChanged();
                lstProds.invalidateViews();
            }
        }catch (Exception e) {
            Toast.makeText(this, "Database Error", Toast.LENGTH_LONG).show();
        }
    }

    public void change(){
        Quantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (Price.getText().equals("")) {
                        Toast.makeText(OrderingSystem.this, "No Product Selected", Toast.LENGTH_LONG).show();
                    } else if (s.length() != 0) {
                        total();
                    } else {
                        totalPriceUp.setText("");
                    }
                }catch (Exception e){
                    Toast.makeText(OrderingSystem.this, "Please Input a Valid Value", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}