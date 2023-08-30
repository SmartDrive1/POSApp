package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class payment extends AppCompatActivity {

    TextView price;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        price = findViewById(R.id.txtPrice);
        back = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        String tPrice = i.getStringExtra("tPrice".toString());


    }
}