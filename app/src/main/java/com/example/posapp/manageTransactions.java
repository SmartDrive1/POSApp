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
import android.widget.ListView;

import java.util.ArrayList;

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(manageTransactions.this, MainScreen.class);
                startActivity(i);
            }
        });
    }

    public void refreshList(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions(prodName VARCHAR PRIMARY KEY,quantity INTEGER, price DOUBLE)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from cartlist", null);
        int prodName = c.getColumnIndex("prodName");
        int quantity = c.getColumnIndex("quantity");
        int price = c.getColumnIndex("price");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstTrans.setAdapter(arrayAdapter);

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
            lstTrans.invalidateViews();
            c.close();
            db.close();
        }
    }
}