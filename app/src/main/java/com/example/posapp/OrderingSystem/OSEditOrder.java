package com.example.posapp.OrderingSystem;

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

import com.example.posapp.R;

public class OSEditOrder extends AppCompatActivity {

    EditText editProduct, qty, editPrice, txtTprice;
    Button edit, remove, back;
    String newPrice;
    SQLiteDatabase db;
    String quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_edit_order);
        db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        Intent i = getIntent();
        String prodName = i.getStringExtra("prodName".toString());
        quantity = i.getStringExtra("quantity".toString());
        String price = i.getStringExtra("price".toString());

        edit = findViewById(R.id.btnEdit);
        remove = findViewById(R.id.btnRemove);
        back = findViewById(R.id.btnCancel);
        txtTprice = findViewById(R.id.txtTPrice);
        editProduct = findViewById(R.id.txtEditProduct);
        qty = findViewById(R.id.txtQuantity);
        editPrice = findViewById(R.id.txtEditPrice);

        editProduct.setText(prodName);
        qty.setText(quantity);
        txtTprice.setText(price);

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
                Intent i = new Intent(OSEditOrder.this, osCart.class);
                startActivity(i);
            }
        });

        setPrice();
        change();
    }

    public void edit() {
        try {
            String qty1 = qty.getText().toString();
            String prodName = editProduct.getText().toString();

            if (qty1.equals("")) {
                Toast.makeText(this, "Quantity is Blank. Please Input a Value", Toast.LENGTH_LONG).show();
            } else if(quantity.equals(qty1)){
                Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(OSEditOrder.this, OrderingSystem.class);
                startActivity(i);
            }else if (Integer.parseInt(qty1) <= 0) {
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
            Intent i = new Intent(OSEditOrder.this, osCart.class);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void newTotal() {
        double nPrice;
        nPrice = Double.parseDouble(qty.getText().toString()) * Double.parseDouble(editPrice.getText().toString());

        newPrice = String.valueOf(nPrice);
        txtTprice.setText(String.valueOf(nPrice));
    }

    public void setPrice(){
        Double inPrice = Double.parseDouble(txtTprice.getText().toString())/Double.parseDouble(qty.getText().toString());
        editPrice.setText(String.valueOf(inPrice));
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
                        Toast.makeText(OSEditOrder.this, "Quantity is Blank", Toast.LENGTH_LONG).show();
                    } else if (s.length() != 0) {
                        newTotal();
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