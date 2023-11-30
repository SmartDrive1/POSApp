package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.kmeans.KMeans;

public class MainScreen extends AppCompatActivity {
    Button btnOrderingSystem, btnAnalysis, btnMoreInfo, btnManagement, btnLogout, btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btnLogout = findViewById(R.id.btnLogout);
        btnMoreInfo = findViewById(R.id.btnMoreInfo);
        btnAnalysis = findViewById(R.id.btnAnalysis);
        btnManagement = findViewById(R.id.btnManagement);
        btnOrderingSystem = findViewById(R.id.btnOrderingSystem);
//        btnAdmin = findViewById(R.id.btnAdmin); //Remove This


//        btnAdmin.setOnClickListener(new View.OnClickListener() { //Remove This
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainScreen.this, AaUriButton.class);
//                startActivity(i);
//            }
//        });

        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreen.this, KMeans.class);
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

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, moreinfo.class);
                startActivity(i);
            }
        });

        btnManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreen.this, mainManageScreen.class);
                startActivity(i);
            }
        });
    }
}