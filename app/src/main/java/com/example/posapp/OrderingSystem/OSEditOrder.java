package com.example.posapp.OrderingSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_edit_order);
        db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        Intent i = getIntent();
        id = i.getStringExtra("id".toString());
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
    @SuppressLint("Range")
    public void edit() {
        try {
            String qty1 = qty.getText().toString().trim();

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

                String checkQuantityQuery = "SELECT quantity FROM products WHERE id = ?";
                Cursor cursor = db.rawQuery(checkQuantityQuery, new String[]{id});
                if (cursor.moveToFirst()) {//Get qty from products
                    int availableQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                    int requestedQuantity = Integer.parseInt(String.valueOf(qty.getText()));
                    availableQuantity = Integer.parseInt(availableQuantity + quantity);

                    if (requestedQuantity <= availableQuantity) {
                        updateCartItemAndReduceProductQuantity(id, qty1, newPrice);
                        String sql = "update cartlist set quantity = ?, price = ? where id = ?";
                        SQLiteStatement statement = db.compileStatement(sql);
                        statement.bindString(1, qty1);
                        statement.bindString(2, newPrice);
                        statement.bindString(3, id);
                        statement.execute();
                        Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(OSEditOrder.this, OrderingSystem.class);
                        startActivity(i);
                        db.close();
                    }else{
                        Toast.makeText(this, "Insufficient Stock", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Input a Valid Quantity", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("Range")
    public void updateCartItemAndReduceProductQuantity(String currentID, String newQuantity, String tPrice1) {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            // Step 1: Retrieve current quantity from the 'cartlist'
            String getCurrentQuantityQuery = "SELECT quantity FROM cartlist WHERE id=?";
            Cursor cursor = db.rawQuery(getCurrentQuantityQuery, new String[]{currentID});

            int currentQuantity = 0;
            if (cursor.moveToFirst()) {
                currentQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            }
            cursor.close();

            int quantityDifference = Integer.parseInt(newQuantity) - currentQuantity;

            String updateCartlistQuery = "UPDATE cartlist SET quantity=?, price=? WHERE id=?";
            Object[] cartlistBindArgs = {newQuantity, tPrice1, currentID};
            db.execSQL(updateCartlistQuery, cartlistBindArgs);

            String updateProductsQuery = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            Object[] productsBindArgs = {quantityDifference, currentID};
            db.execSQL(updateProductsQuery, productsBindArgs);

            Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();

            db.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void delete() {
        try {
            String product1 = editProduct.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            //Add to products stock
            String updateQuantity = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
            Object[] bindArgs = {quantity, id};
            db.execSQL(updateQuantity, bindArgs);
            //Delete
            String sql = "DELETE FROM cartlist WHERE id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, id);
            statement.execute();
            Toast.makeText(this, "Cart Item Deleted", Toast.LENGTH_LONG).show();
            Intent i = new Intent(OSEditOrder.this, osCart.class);
            startActivity(i);
            db.close();
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