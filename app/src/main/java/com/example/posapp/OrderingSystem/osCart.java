package com.example.posapp.OrderingSystem;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;

public class osCart extends AppCompatActivity implements cartClickListener {

    TextView total;
    EditText tPayment;
    Button btnBack, payment;
    Double tPrice = 0.00;
    Double xPayment = 0.00;
    OSAdapter OSAdapter;
    List<OSItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_cart);

        btnBack = findViewById(R.id.btnBack);
        payment = findViewById(R.id.btnPayment);
        total = findViewById(R.id.totalPrice);
        tPayment = findViewById(R.id.txtPayment);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(osCart.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tPrice <= 0){
                    Toast.makeText(getApplicationContext(), "No Items in Cart", Toast.LENGTH_SHORT).show();
                }else if (tPayment.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Payment Value is Blank", Toast.LENGTH_SHORT).show();
                    } else {
                        xPayment = Double.parseDouble(tPayment.getText().toString().trim());
                        if (xPayment < tPrice) {
                            Toast.makeText(getApplicationContext(), "Input Payment Value More Than the Price", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(getApplicationContext(), OSPayment.class);
                            i.putExtra("tPrice", tPrice.toString());
                            i.putExtra("tPayment", xPayment.toString());
                            startActivity(i);
                        }
                    }
            }
        });

        refreshList();
        total.setText("Total: ₱" + tPrice.toString() + "0");
    }

    public void refreshList(){
        RecyclerView recyclerView = findViewById(R.id.recycleCart1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(id INTEGER PRIMARY KEY, prodName VARCHAR,quantity INTEGER, category VARCHAR, price DOUBLE)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from cartlist", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"The Cart is Empty", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int prodName = c.getColumnIndex("prodName");
            int quantity = c.getColumnIndex("quantity");
            int price = c.getColumnIndex("price");
            int category = c.getColumnIndex("category");

            if(c.moveToFirst()){
                do{
                    items.add(new OSItems(c.getString(id),c.getString(prodName),c.getString(quantity),c.getString(price),c.getString(category)));
                    OSAdapter = new OSAdapter(this, items, this);
                    recyclerView.setAdapter(OSAdapter);
                    tPrice = tPrice + Double.parseDouble(c.getString(price));
                }while(c.moveToNext());
            }
        }
        c.close();
        db.close();
    }

    @Override
    public void onItemClicked(OSItems view) {
        Intent i = new Intent(getApplicationContext(), OSEditOrder.class);
        i.putExtra("id",view.getId());
        i.putExtra("prodName",view.getpName());
        i.putExtra("quantity",view.getpQuantity());
        i.putExtra("price",view.getpPrice());
        startActivity(i);
    }
}