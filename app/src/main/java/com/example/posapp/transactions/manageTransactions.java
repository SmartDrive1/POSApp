package com.example.posapp.transactions;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.posapp.MainScreen;
import com.example.posapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class manageTransactions extends AppCompatActivity {
    Button back;
    ListView lstTrans;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

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
    }

    public void refreshList() {
        String formattedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, time INTEGER)");
            final Cursor c = db.rawQuery("select * from transactions", null);
            int id = c.getColumnIndex("transID");
            int prodName = c.getColumnIndex("prodName");
            int quantity = c.getColumnIndex("quantity");
            int price = c.getColumnIndex("price");
            int time = c.getColumnIndex("time");

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
                    long time1 = c.getLong(time); //Get Date using Long
                    formattedDate = dateFormat.format(new Date(time1));//Convert Long to Date Format

                    if (idValue != currentId) {
                        // The id has changed, add the total for the previous id
                        if (currentId != -1) {
                            titles.add("Transaction ID: " + currentId + " Total Quantity: " + totalQuantity + " Total Price: " + total + " Date: " + formattedDate); // Add total for the previous id
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
                    pr.time = formattedDate;

                    trans.add(pr);

                    titles.add(prodNameValue + "\t\t\t\t\t\t\t\t\t\t\t" + quantityValue + "\t\t\t\t\t\t\t" + priceValue);

                } while (c.moveToNext());

                // Add the total for the last id in the database
                if (currentId != -1) {
                    titles.add("Transaction ID: " + currentId + " Total Quantity: " + totalQuantity + " Total Price: " + "Date" + formattedDate);
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