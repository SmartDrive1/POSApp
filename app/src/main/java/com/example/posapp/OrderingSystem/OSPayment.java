package com.example.posapp.OrderingSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;

public class OSPayment extends AppCompatActivity {

    OSPaymentAdapter OSPaymentAdapter;
    List<OSItems> items = new ArrayList<>();
    TextView price, change, pay;
    Button back, confirm;
    Double eChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        price = findViewById(R.id.totalPrice);
        back = findViewById(R.id.btnBack);
        pay = findViewById(R.id.txtPay);
        change = findViewById(R.id.txtChange);
        confirm = findViewById(R.id.btnConfirm);

        Intent i = getIntent();
        String tPrice = i.getStringExtra("tPrice");
        String tPayment = i.getStringExtra("tPayment");

        eChange = Double.parseDouble(tPayment) - Double.parseDouble(tPrice);

        price.setText("Total: " + tPrice + "0");
        pay.setText("Pay: " + tPayment + "0");
        change.setText("Change: " + eChange.toString() + "0");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OSPayment.this, osCart.class);
                startActivity(i);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTrans();
                Toast.makeText(OSPayment.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                Intent i = new Intent(OSPayment.this, OrderingSystem.class);
                startActivity(i);
            }
        });
        refreshList();
    }

    public void refreshList() {
        RecyclerView recyclerView = findViewById(R.id.recycleCart1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(id INTEGER PRIMARY KEY, prodName VARCHAR,quantity INTEGER, category VARCHAR, price DOUBLE)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from cartlist", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Products Found", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int prodName = c.getColumnIndex("prodName");
            int quantity = c.getColumnIndex("quantity");
            int price = c.getColumnIndex("price");
            int category = c.getColumnIndex("category");

            if(c.moveToFirst()){
                do{
                    items.add(new OSItems(c.getString(id), c.getString(prodName),c.getString(quantity),c.getString(price), c.getString(category)));
                    OSPaymentAdapter = new OSPaymentAdapter(this, items);
                    recyclerView.setAdapter(OSPaymentAdapter);
                }while(c.moveToNext());
            }
        }
        c.close();
        db.close();
    }

    @SuppressLint("Range")
    public void confirmTrans() {
        int max_id = 0, max_id2 = 0, highestID = 0;

        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT MAX(transID) FROM transactions", null);

            if (cursor.moveToNext()) {
                max_id = cursor.getInt(0);
            }

            cursor = db.rawQuery("SELECT MAX(transID) FROM orders", null);
            if(cursor.moveToNext()){
                max_id2 = cursor.getInt(0);
            }


            cursor = db.rawQuery("SELECT * FROM cartlist", null);

            max_id += 1;
            max_id2 += 1;

            if(max_id > max_id2){
                highestID = max_id;
            }else{
                highestID = max_id2;
            }

            if(cursor.moveToFirst()){
                do{
                    String prodID = cursor.getString(cursor.getColumnIndex("id"));
                    String prodName = cursor.getString(cursor.getColumnIndex("prodName"));
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String price = cursor.getString(cursor.getColumnIndex("price"));
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    long currentTime = System.currentTimeMillis();

                    String sql = "INSERT INTO orders(transID, id, prodName, quantity, price, category, time, status)values(?,?,?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1, String.valueOf(highestID));
                    statement.bindString(2, prodID);
                    statement.bindString(3, prodName);
                    statement.bindString(4, quantity);
                    statement.bindString(5, price);
                    statement.bindString(6, category);
                    statement.bindString(7, String.valueOf(currentTime));
                    statement.bindString(8, "Pending");
                    statement.execute();
                }while (cursor.moveToNext());
            }

            String sql = "drop table cartlist";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.execute();

            cursor.close();
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to transfer data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}