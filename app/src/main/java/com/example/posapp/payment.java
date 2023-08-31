package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class payment extends AppCompatActivity {

    ListView lstCart1;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    TextView price, change, pay;
    Button back, confirm;
    Double eChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        price = findViewById(R.id.totalPrice);
        back = findViewById(R.id.btnBack);
        pay = findViewById(R.id.txtPay);
        change = findViewById(R.id.txtChange);
        confirm = findViewById(R.id.btnConfirm);
        lstCart1 = findViewById(R.id.lstCart1);

        Intent i = getIntent();
        String tPrice = i.getStringExtra("tPrice");
        String tPayment = i.getStringExtra("tPayment");

        eChange = Double.parseDouble(tPayment) - Double.parseDouble(tPrice);

        price.setText("Total: " + tPrice);
        pay.setText("Pay: " + tPayment);
        change.setText("Change: " + eChange.toString());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(payment.this, cart.class);
                startActivity(i);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTrans();
                Intent i = new Intent(payment.this, confirmTransaction.class);
                startActivity(i);
            }
        });

        refreshList();

    }

    public void refreshList() {
        Intent i = getIntent();
        String tPrice = i.getStringExtra("tPrice");
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(prodName VARCHAR PRIMARY KEY,quantity INTEGER, price DOUBLE)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from cartlist", null);
        int prodName = c.getColumnIndex("prodName");
        int quantity = c.getColumnIndex("quantity");
        int price = c.getColumnIndex("price");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, titles);
        lstCart1.setAdapter(arrayAdapter);

        final ArrayList<carttmp> cart = new ArrayList<carttmp>();
        if (c.moveToFirst()) {
            do {
                carttmp pr = new carttmp();
                pr.prodName = c.getString(prodName);
                pr.quantity = c.getString(quantity);
                pr.price = c.getString(price);

                cart.add(pr);
                titles.add(c.getString(prodName) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(quantity) + "\t\t\t\t\t\t\t" + c.getString(price));
            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
            lstCart1.invalidateViews();
        }
    }

    public void confirmTrans() {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, prodname TEXT, quantity INTEGER, price DOUBLE, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
            Cursor cursor = db.rawQuery("SELECT prodName, quantity, price FROM cartlist", null);

            while (cursor.moveToNext()) {
                String prodName = cursor.getString(0);
                int quantity = cursor.getInt(1);
                double price = cursor.getDouble(2);

                ContentValues values = new ContentValues();
                values.put("prodname", prodName);
                values.put("quantity", quantity);
                values.put("price", price);

                db.insert("transactions", null, values);
            }

            String sql = "drop table cartlist";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.execute();
            cursor.close();

            Toast.makeText(this, "Data transferred from cartlist to transactions", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to transfer data", Toast.LENGTH_LONG).show();
        }
    }
}