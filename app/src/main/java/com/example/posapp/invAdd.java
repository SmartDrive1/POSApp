package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class invAdd extends AppCompatActivity {

    EditText txtItemName, txtStock;
    Button btnAddItem, btnCancel;
    Integer max_id;

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
                Intent i = new Intent(invAdd.this, invList.class);
                startActivity(i);
            }
        });
    }

    public void getMax(){
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("SELECT MAX(id) FROM inventory", null);

            if (cursor.moveToFirst()) {
                max_id = cursor.getInt(0);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert()
    {
        String itemName = txtItemName.getText().toString();
        String Stock = txtStock.getText().toString();

        getMax();
        max_id += 1;
        if(itemName.equals("") || Stock.equals(""))
        {
            Toast.makeText(this,"Item Name or Stock is Blank", Toast.LENGTH_LONG).show();
        }else if (Integer.parseInt(Stock) < 0)
        {
            Toast.makeText(this,"Please enter a Stock Amount Greater Than or Equals to 0", Toast.LENGTH_LONG).show();
        }else{
            try{
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY,itemName VARCHAR, stock INTEGER )");

                String sql = "insert into inventory (id, itemName, stock)values(?,?,?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,String.valueOf(max_id));
                statement.bindString(2,itemName);
                statement.bindString(3,Stock);
                statement.execute();
                Toast.makeText(this,"Product Added", Toast.LENGTH_LONG).show();
                txtItemName.setText("");
                txtStock.setText("");
                txtItemName.requestFocus();
                db.close();
            }catch (Exception e)
            {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}