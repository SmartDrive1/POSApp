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

public class invList extends AppCompatActivity {

    ListView lstInventory;
    Button btnBack, btnAddItem;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        btnBack = findViewById(R.id.btnBack);
        btnAddItem = findViewById(R.id.btnAddItem);
        lstInventory = findViewById(R.id.lstInventory);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this,MainScreen.class);
                startActivity(i);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this, invAdd.class);
                startActivity(i);
            }
        });

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY, itemName VARCHAR, stock INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from inventory", null);
        int id = c.getColumnIndex("id");
        int itemName = c.getColumnIndex("itemName");
        int stock = c.getColumnIndex("stock");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstInventory.setAdapter(arrayAdapter);

        final ArrayList<cInventory> inv = new ArrayList<cInventory>();
        if(c.moveToFirst())
        {
            do{
                cInventory pr = new cInventory();
                pr.id = c.getString(id);
                pr.itemName = c.getString(itemName);
                pr.stock = c.getString(stock);
                inv.add(pr);

                titles.add(c.getString(id) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(itemName) + "\t\t\t\t\t\t\t" + c.getString(stock));

            }while(c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
            lstInventory.invalidateViews();
        }
        c.close();
        db.close();

        lstInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String a = titles.get(position).toString();
                cInventory pr = inv.get((position));
                Intent i = new Intent(getApplicationContext(), invEdit.class);
                i.putExtra("id",pr.id);
                i.putExtra("itemName",pr.itemName);
                i.putExtra("itemStock",pr.stock);
                startActivity(i);
            }
        });
    }
    }