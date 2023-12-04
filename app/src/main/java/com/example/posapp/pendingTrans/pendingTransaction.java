package com.example.posapp.pendingTrans;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class pendingTransaction extends AppCompatActivity implements pendingClickListener{

    Button btnOrdersView, btnBack;
    pendingAdapter pendingAdapter;
    List<pendingItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transaction);

        btnBack = findViewById(R.id.btnBack);
        btnOrdersView = findViewById(R.id.btnOrdersView);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(pendingTransaction.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        btnOrdersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(pendingTransaction.this, ordersView.class);
                startActivity(i);
            }
        });

        loadOrders();
    }

    public void loadOrders(){
        items.clear();

        RecyclerView recyclerView = findViewById(R.id.recycleOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS orders(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER, status VARCHAR)");
            String query = "SELECT transID, status, time FROM orders GROUP BY transID";
            Cursor cursor = db.rawQuery(query, null);

            int transIDIndex = cursor.getColumnIndex("transID");
            int statusIndex = cursor.getColumnIndex("status");
            int timeIndex = cursor.getColumnIndex("time");
            String formattedDate;
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No Pending Orders", Toast.LENGTH_LONG).show();
            } else {
                while (cursor.moveToNext()) {
                    long time = cursor.getLong(timeIndex);
                    formattedDate = dateFormat.format(new Date(time));
                    items.add(new pendingItems(cursor.getString(transIDIndex), cursor.getString(statusIndex), formattedDate));
                }
                cursor.close();
                db.close();
                // Move the adapter and setAdapter out of the loop to set the adapter only once
                pendingAdapter = new pendingAdapter(this, items, this);
                recyclerView.setAdapter(pendingAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving pending orders", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClicked(pendingItems view) {
        Intent i = new Intent(getApplicationContext(), orderEdit.class);
        i.putExtra("id", view.getTransID());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
    }
}