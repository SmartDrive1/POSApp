package com.example.posapp.pendingTrans;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;

public class ordersView extends AppCompatActivity {
    List<servingItems> sItems = new ArrayList<>();
    List<preparingItems> pItems = new ArrayList<>();
    servingAdapter servingAdapter;
    preparingAdapter preparingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_view);

        loadOrders();
    }

    public void loadOrders(){
        RecyclerView recyclerView = findViewById(R.id.recyclePreparing);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        RecyclerView recyclerView1 = findViewById(R.id.recycleServing);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        String query = "SELECT * FROM orders GROUP BY transID";
        Cursor cursor = db.rawQuery(query, null);

        int id = cursor.getColumnIndex("transID");
        int status = cursor.getColumnIndex("status");

        if(cursor.getCount() == 0){

        }else{
            while(cursor.moveToNext()){
                if(cursor.getString(status).equals("Preparing")){
                    pItems.add(new preparingItems(cursor.getString(id)));
                    preparingAdapter = new preparingAdapter(this, pItems);
                    recyclerView.setAdapter(preparingAdapter);

                }
                if(cursor.getString(status).equals("Serving")){
                    sItems.add(new servingItems(cursor.getString(id)));
                    servingAdapter = new servingAdapter(this, sItems);
                    recyclerView1.setAdapter(servingAdapter);
                }
            }
        }
    }
}