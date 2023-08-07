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

public class addproduct extends AppCompatActivity {

    EditText txtProduct, txtPrice;
    Button btnAdd, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);

        String[] s1 = new String[] {
                "Drinks", "Food", "Others"
        };
        Spinner s = (Spinner) findViewById(R.id.catID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        txtProduct = findViewById(R.id.txtProduct);
        txtPrice = findViewById(R.id.txtPrice);
        btnAdd =  findViewById(R.id.btnCart);
        btnCancel = findViewById(R.id.btnCancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insert();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(addproduct.this, productList.class);
                startActivity(i);
            }
        });
    }

    public void insert()
    {
        String prodName = txtProduct.getText().toString();
        String price = txtPrice.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.catID);
        if(prodName.equals("") || price.equals(""))
        {
            Toast.makeText(this,"Product Name or Price is Blank", Toast.LENGTH_LONG).show();
        }else if (Integer.parseInt(price) <= 0)
            {
                Toast.makeText(this,"Please Enter a Price Greater Than 0", Toast.LENGTH_LONG).show();
            }else{
            try{
                String product = txtProduct.getText().toString();
                String prodPrice = txtPrice.getText().toString();
                String spTxt = spinner.getSelectedItem().toString();
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )");

                String sql = "insert into products (product, category, prodPrice)values(?,?,?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,product);
                statement.bindString(2,spTxt);
                statement.bindString(3,prodPrice);
                statement.execute();
                Toast.makeText(this,"Product Added", Toast.LENGTH_LONG).show();
                txtProduct.setText("");
                txtPrice.setText("");
                txtProduct.requestFocus();
            }catch (Exception e)
            {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}