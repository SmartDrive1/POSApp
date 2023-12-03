package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.posapp.OrderingSystem.OrderingSystem;

import com.example.posapp.inventory.invList;
import com.example.posapp.products.productList;
import com.example.posapp.transactions.manageTransactions;
import com.example.posapp.users.userList;

public class mainManageScreen extends AppCompatActivity {
    Button btnUsers, btnProducts, btnTransactions, btnInventory, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manage_screen);

        btnUsers = findViewById(R.id.btnUsers);
        btnProducts = findViewById(R.id.btnProducts);
        btnTransactions = findViewById(R.id.btnTransactions);
        //btnInventory = findViewById(R.id.btnInventory);
        btnBack = findViewById(R.id.btnBack);

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainManageScreen.this, userList.class);
                startActivity(i);
            }
        });

        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainManageScreen.this, productList.class);
                startActivity(i);
            }
        });

        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainManageScreen.this, manageTransactions.class);
                startActivity(i);
            }
        });

//        btnInventory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mainManageScreen.this, invList.class);
//                startActivity(i);
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainManageScreen.this, MainScreen.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}