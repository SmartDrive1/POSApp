package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AaUriButton extends AppCompatActivity {

    Button btnClearUsers, btnClearProducts, btnClearTransactions, btnClearInventory, btnBack, btnClearOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aa_uri_button);

        btnClearInventory = findViewById(R.id.btnClearInventory);
        btnClearUsers = findViewById(R.id.btnClearUsers);
        btnClearProducts = findViewById(R.id.btnClearProducts);
        btnClearTransactions = findViewById(R.id.btnCLearTransactions);
        btnBack = findViewById(R.id.btnBack);
        btnClearOrders = findViewById(R.id.ClearOrders);

        btnClearProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table products";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
            }
        });

        btnClearOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table cartlist";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
            }
        });

        btnClearUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table users";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AaUriButton.this, MainScreen.class);
                startActivity(i);
            }
        });
    }

}