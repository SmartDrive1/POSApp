package com.example.posapp.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.posapp.MainScreen;
import com.example.posapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.posapp.mainManageScreen;

public class manageTransactions extends AppCompatActivity implements transClickListener {
    Button back;
    transAdapter transAdapter;
    List<transItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        back = findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(manageTransactions.this, mainManageScreen.class);
                startActivity(i);
            }
        });
        refreshList();
    }

    @SuppressLint("Range")
    public void refreshList() {
        String formattedDate;
        RecyclerView recyclerView = findViewById(R.id.recycleTrans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, time INTEGER)");
            String query = "SELECT transID, time, SUM(price) AS totalAmount, SUM(quantity) AS totalQuantity FROM transactions GROUP BY transID";
            Cursor cursor = db.rawQuery(query, null);

            int id = cursor.getColumnIndex("transID");
            int time = cursor.getColumnIndex("time");
            int totalAmount = cursor.getColumnIndex("totalAmount");
            int totalQuantity = cursor.getColumnIndex("totalQuantity");


            while(cursor.moveToNext()){
                formattedDate = dateFormat.format(new Date(cursor.getLong(time)));
                items.add(new transItems(cursor.getString(id), "", cursor.getString(totalQuantity), cursor.getString(totalAmount), formattedDate));
                transAdapter = new transAdapter(this, items, this);
                recyclerView.setAdapter(transAdapter);
            }

            cursor.close();
            db.close();

            }catch (Exception e) {
            Toast.makeText(this, "Database Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClicked(transItems view) {
        Intent i = new Intent(getApplicationContext(), transEdit.class);
        i.putExtra("id",view.getTransID());
        startActivity(i);
    }
}