package com.example.posapp.products;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.posapp.R;

public class prodAdd extends AppCompatActivity {

    EditText txtProduct, txtPrice;
    Button btnAdd, btnCancel;
    Integer max_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_add);

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
                Intent i = new Intent(prodAdd.this, productList.class);
                startActivity(i);
            }
        });
    }

    public void getMax(){
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("SELECT MAX(id) FROM products", null);

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
        String prodName = txtProduct.getText().toString();
        String price = txtPrice.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.catID);

        getMax();
        max_id += 1;
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
                db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, prodPrice INTEGER )");

                String sql = "insert into products (id, product, category, prodPrice)values(?,?,?,?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,String.valueOf(max_id));
                statement.bindString(2,product);
                statement.bindString(3,spTxt);
                statement.bindString(4,prodPrice);
                statement.execute();
                Toast.makeText(this,"Product Added", Toast.LENGTH_LONG).show();
                txtProduct.setText("");
                txtPrice.setText("");
                txtProduct.requestFocus();
                db.close();
            }catch (Exception e)
            {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}