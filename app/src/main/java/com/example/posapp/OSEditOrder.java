package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OSEditOrder extends AppCompatActivity {

    EditText editProduct, qty, editPrice, txtTprice;
    Button edit, remove, back;
    String newPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_edit_order);

        Intent i = getIntent();
        String prodName = i.getStringExtra("prodName".toString());
        String quantity = i.getStringExtra("quantity".toString());

        edit = findViewById(R.id.btnEdit);
        remove = findViewById(R.id.btnRemove);
        back = findViewById(R.id.btnCancel);
        txtTprice = findViewById(R.id.txtTPrice);
        editProduct = findViewById(R.id.txtEditProduct);
        qty = findViewById(R.id.txtQuantity);
        editPrice = findViewById(R.id.txtEditPrice);

        editProduct.setText(prodName);
        qty.setText(quantity);



        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null); //set Price
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER)");//in case there are no tables yet
        final Cursor c = db.rawQuery("SELECT * FROM products WHERE product ='" + editProduct.getText().toString() + "'", null);

        if (c.moveToFirst()) {
            int prodPriceIndex = c.getColumnIndex("prodPrice");
            int productPrice = c.getInt(prodPriceIndex);
            editPrice.setText(String.valueOf(productPrice));
        } else {
            editPrice.setText("N/A");
        }
        c.close();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }

        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OSEditOrder.this, cart.class);
                startActivity(i);
            }
        });

        total();
        change();
    }

    public void edit() {
        try {
            newTotal();
            String qty1 = qty.getText().toString();
            String prodName = editProduct.getText().toString();

            if (qty1.equals("")) {
                Toast.makeText(this, "Quantity is Blank. Please Input a Value", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(qty1) <= 0) {
                Toast.makeText(this, "Quantity is Invalid. Please Input More Than 0", Toast.LENGTH_LONG).show();
            } else {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

                String sql = "update cartlist set quantity = ?, price = ? where prodName = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, qty1);
                statement.bindString(2, newPrice);
                statement.bindString(3, prodName);
                statement.execute();
                Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(OSEditOrder.this, OrderingSystem.class);
                startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Input a Valid Quantity", Toast.LENGTH_LONG).show();
        }
    }

    public void delete() {
        try {
            String product1 = editProduct.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            String sql = "delete from cartlist where prodName = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, product1);
            statement.execute();
            Toast.makeText(this, "Cart Item Deleted", Toast.LENGTH_LONG).show();
            Intent i = new Intent(OSEditOrder.this, cart.class);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void newTotal() {
        double nPrice;
        nPrice = Double.parseDouble(qty.getText().toString()) * Double.parseDouble(editPrice.getText().toString());

        newPrice = String.valueOf(nPrice);
    }

    public void total(){
        if (String.valueOf(qty.getText()).equals("")){
            Toast.makeText(OSEditOrder.this,"Please Input a Quantity", Toast.LENGTH_LONG).show();
        }else if(Integer.parseInt(String.valueOf(qty.getText())) <= 0){
            Toast.makeText(OSEditOrder.this,"Quantity Must Be Greater Than 1", Toast.LENGTH_LONG).show();
        }else{
            double qty1 = Double.parseDouble(qty.getText().toString());
            double price1 = Double.parseDouble(editPrice.getText().toString());
            double totalPrice = qty1 * price1;

            txtTprice.setText(String.valueOf(Double.valueOf(totalPrice)));
        }
    }

    public void change() {
        qty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (qty.getText().equals("")) {
                        Toast.makeText(OSEditOrder.this, "No Product Selected", Toast.LENGTH_LONG).show();
                    } else if (s.length() != 0) {
                        total();
                    } else {
                        txtTprice.setText("");
                    }
                } catch (Exception e) {
                    Toast.makeText(OSEditOrder.this, "Please Input a Valid Value", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}