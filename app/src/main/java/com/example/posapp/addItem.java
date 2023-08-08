package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class addItem extends AppCompatActivity {

    EditText txtItemName, txtStock;
    Button btnAddItem, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        txtItemName = findViewById(R.id.txtItemName);
        txtStock = findViewById(R.id.txtStock);
        btnAddItem =  findViewById(R.id.btnAddItem);
        btnCancel = findViewById(R.id.btnCancel);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insert();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(addItem.this, inventory.class);
                startActivity(i);
            }
        });
    }

    public void insert()
    {
        String itemName = txtItemName.getText().toString();
        String Stock = txtStock.getText().toString();
        if(itemName.equals("") || Stock.equals(""))
        {
            Toast.makeText(this,"Item Name or Stock is Blank", Toast.LENGTH_LONG).show();
        }else if (Integer.parseInt(Stock) < 0)
        {
            Toast.makeText(this,"Please enter a Stock Amount Greater Than or Equals to 0", Toast.LENGTH_LONG).show();
        }else{
            try{
                String product = txtItemName.getText().toString();
                String prodPrice = txtStock.getText().toString();
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY AUTOINCREMENT,itemName VARCHAR, stock INTEGER )");

                String sql = "insert into inventory (itemName, stock)values(?,?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,itemName);
                statement.bindString(2,Stock);
                statement.execute();
                Toast.makeText(this,"Product Added", Toast.LENGTH_LONG).show();
                txtItemName.setText("");
                txtStock.setText("");
                txtItemName.requestFocus();
            }catch (Exception e)
            {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    }