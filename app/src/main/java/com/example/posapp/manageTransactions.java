package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import java.util.ArrayList;

public class manageTransactions extends AppCompatActivity {
    Button back;
    ListView lstTrans;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;
    Integer max_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        back = findViewById(R.id.btnBack);
        lstTrans = findViewById(R.id.lstTrans);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(manageTransactions.this, MainScreen.class);
                startActivity(i);
            }
        });
        refreshList();
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("SELECT MAX(transID) FROM transactions", null);

            if (cursor.moveToFirst()) {
                max_id = cursor.getInt(0);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshList() {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE)");
            final Cursor c = db.rawQuery("select * from transactions", null);
            int id = c.getColumnIndex("transID");
            int prodName = c.getColumnIndex("prodName");
            int quantity = c.getColumnIndex("quantity");
            int price = c.getColumnIndex("price");

            titles.clear();
            arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, titles);
            lstTrans.setAdapter(arrayAdapter);

            final ArrayList<cTrans> trans = new ArrayList<cTrans>();
            int currentId = -1; // Initialize with a value that is not in your database

            if (c.moveToFirst()) {
                double total = 0; // Initialize the total for the current id
                int totalQuantity = 0;
                do {
                    int idValue = c.getInt(id); // Assuming 'id' is an integer column
                    String prodNameValue = c.getString(prodName);
                    String quantityValue = c.getString(quantity);
                    double priceValue = c.getDouble(price); // Assuming 'price' is a double column
                    int quantity1 = c.getInt(quantity);

                    if (idValue != currentId) {
                        // The id has changed, add the total for the previous id
                        if (currentId != -1) {
                            titles.add("Transaction ID: " + currentId + " Total Quantity: " + totalQuantity + " Total Price: " + total); // Add total for the previous id
                        }

                        currentId = idValue; // Set the current id to the new id
                        totalQuantity = 0; // Reset Quantity Value
                        total = 0; // Reset the total for the new id
                    }

                    // Calculate the total for the current id
                    total += priceValue;
                    totalQuantity += quantity1;

                    // Create and add the transaction entry
                    cTrans pr = new cTrans();
                    pr.id = String.valueOf(idValue);
                    pr.prodName = prodNameValue;
                    pr.quantity = quantityValue;
                    pr.price = String.valueOf(priceValue);

                    trans.add(pr);

                    titles.add(prodNameValue + "\t\t\t\t\t\t\t\t\t\t\t" + quantityValue + "\t\t\t\t\t\t\t" + priceValue);

                } while (c.moveToNext());

                // Add the total for the last id in the database
                if (currentId != -1) {
                    titles.add("Transaction ID: " + currentId + " Total Quantity: " + totalQuantity + " Total Price: " + total);
                }
            }
            arrayAdapter.notifyDataSetChanged();
                lstTrans.invalidateViews();
                c.close();
                db.close();

            }catch (Exception e) {
            Toast.makeText(this, "Database Error", Toast.LENGTH_LONG).show();
        }
    }
}