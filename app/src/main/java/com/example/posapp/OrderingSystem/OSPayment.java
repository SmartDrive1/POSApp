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

        price.setText("Total: " + tPrice);
        pay.setText("Pay: " + tPayment);
        change.setText("Change: " + eChange.toString());

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
                Intent i = new Intent(OSPayment.this, confirmTransaction.class);
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
        int max_id = 0;

        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER)");
            Cursor cursor = db.rawQuery("SELECT MAX(transID) FROM transactions", null);

            if (cursor.moveToNext()) {
                max_id = cursor.getInt(0);
            }

            cursor = db.rawQuery("SELECT * FROM cartlist", null);

            max_id += 1;

            if(cursor.moveToFirst()){
                do{
                    String prodName = cursor.getString(cursor.getColumnIndex("prodName"));
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String price = cursor.getString(cursor.getColumnIndex("price"));
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    long currentTime = System.currentTimeMillis();

                    String sql = "insert into transactions (transID, prodName, quantity, price, category, time)values(?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1, String.valueOf(max_id));
                    statement.bindString(2, prodName);
                    statement.bindString(3, quantity);
                    statement.bindString(4, price);
                    statement.bindString(5, category);
                    statement.bindString(6, String.valueOf(currentTime));
                    statement.execute();
                }while (cursor.moveToNext());
            }

            String sql = "drop table cartlist";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.execute();

            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to transfer data", Toast.LENGTH_LONG).show();
        }
    }
}