package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class confirmTransaction extends AppCompatActivity {

    ListView lstCart1;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);

        lstCart1 = findViewById(R.id.lstCart1);

        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, prodName VARCHAR, quantity INTEGER, price DOUBLE)");
            final Cursor c = db.rawQuery("select * from transactions", null);

            int idIndex = c.getColumnIndex("id");
            int prodNameIndex = c.getColumnIndex("prodName");
            int quantityIndex = c.getColumnIndex("quantity");
            int priceIndex = c.getColumnIndex("price");

            titles.clear();
            arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, titles);
            lstCart1.setAdapter(arrayAdapter);

            final ArrayList<tmpConfirm> tmp = new ArrayList<tmpConfirm>();
            if (c.moveToFirst()) {
                do {
                    tmpConfirm pr = new tmpConfirm();
                    pr.id = c.getString(idIndex);
                    pr.prodName = c.getString(prodNameIndex);
                    pr.quantity = c.getString(quantityIndex);
                    pr.price = c.getString(priceIndex);

                    tmp.add(pr);

                    titles.add("Test" + c.getString(idIndex) + "\t\t\t\t\t\t" + c.getString(prodNameIndex) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(quantityIndex) + "\t\t\t\t\t\t\t" + c.getString(priceIndex));

                } while (c.moveToNext());
                arrayAdapter.notifyDataSetChanged();
                lstCart1.invalidateViews();
            }
        } catch (Exception e) {
            // Handle exceptions here, log the error, or display an error message.
            Toast.makeText(this, "Error",Toast.LENGTH_LONG).show();
        }
    }

        }