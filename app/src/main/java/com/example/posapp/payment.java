package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class payment extends AppCompatActivity {

    TextView price, change, pay;
    Button back, confirm;
    Double eChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        price = findViewById(R.id.totalPrice);
        back = findViewById(R.id.btnBack);
        pay = findViewById(R.id.txtPay);
        change = findViewById(R.id.txtChange);
        confirm = findViewById(R.id.btnConfirm);

        Intent i = getIntent();
        String tPrice = i.getStringExtra("tPrice");
        String tPayment = i.getStringExtra("tPayment");

        eChange = Double.parseDouble(tPayment) - Double.parseDouble(tPrice);

        price.setText("Total: " + tPrice);
        pay.setText("Pay: " + tPayment);
        change.setText("Change: " + eChange.toString());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(payment.this, cart.class);
                startActivity(i);
            }
        });
    }
}