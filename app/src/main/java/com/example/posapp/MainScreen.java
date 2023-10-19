package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainScreen extends AppCompatActivity {
    Button btnUsers, btnTransactions, btnOrderingSystem, btnAnalysis, btnMoreInfo, btnProducts, btnInventory, btnLogout, btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btnLogout = findViewById(R.id.btnLogout);
        btnMoreInfo = findViewById(R.id.btnMoreInfo);
        btnUsers = findViewById(R.id.btnUsers);
        btnProducts = findViewById(R.id.btnProducts);
        btnOrderingSystem = findViewById(R.id.btnOrderingSystem);
        btnInventory = findViewById(R.id.btnInventory);
        btnTransactions = findViewById(R.id.btnTransactions);
        btnAdmin = findViewById(R.id.btnAdmin); //Remove This

        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, manageTransactions.class);
                startActivity(i);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() { //Remove This
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, AaUriButton.class);
                startActivity(i);
            }
        });

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, userList.class);
                startActivity(i);
            }
        });
        btnOrderingSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreen.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreen.this, login.class);
                startActivity(i);
            }
        });
        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, productList.class);
                startActivity(i);
            }
        });

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, moreinfo.class);
                startActivity(i);
            }
        });
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, invList.class);
                startActivity(i);
            }
        });
    }
}