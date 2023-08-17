package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class cart extends AppCompatActivity {

    ListView lstCart1;
    String tmpName, tmpQty, tmpPrice;
    Integer arrayLength, ctr = 0;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lstCart1 = findViewById(R.id.lstCart1);
        btnBack = findViewById(R.id.btnBack);

        TmpContainer container = cCurrentTransac.getCurrentTransaction();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cart.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstCart1.setAdapter(arrayAdapter);

        if (container != null) {
            transacProducts[] orderProducts = container.getProducts();

            ArrayAdapter<transacProducts> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    orderProducts
            );

            lstCart1.setAdapter(adapter);
        }

        lstCart1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final ArrayList<carttmp> ccart = new ArrayList<carttmp>();
                String a = titles.get(position).toString();
                carttmp pr = ccart.get((position));
                Intent i = new Intent(getApplicationContext(), editOrder.class);
                i.putExtra("prodName",pr.prodName);
                i.putExtra("quantity",pr.quantity);
                i.putExtra("price",pr.price);
                startActivity(i);

            }
        });
    }
}