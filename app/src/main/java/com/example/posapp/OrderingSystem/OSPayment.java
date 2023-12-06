package com.example.posapp.OrderingSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSPayment extends AppCompatActivity {

    OSPaymentAdapter OSPaymentAdapter;
    List<OSItems> items = new ArrayList<>();
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

        price.setText("Total: " + tPrice + "0");
        pay.setText("Pay: " + tPayment + "0");
        change.setText("Change: " + eChange.toString() + "0");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OSPayment.this, osCart.class);
                startActivity(i);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTrans();
                Toast.makeText(OSPayment.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                Intent i = new Intent(OSPayment.this, OrderingSystem.class);
                startActivity(i);
            }
        });
        refreshList();
    }

    public void refreshList() {
        RecyclerView recyclerView = findViewById(R.id.recycleCart1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming you have the current user's ID stored in a variable called currentUserID
        String currentUserID = accessValue.user; // Replace this with the actual user ID

        db.collection("cartlist")
                .whereEqualTo("user", currentUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OSItems> items = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");; // Use document ID as the ID
                        String prodName = document.getString("prodName");
                        String quantity = document.getString("quantity");
                        String price = document.getString("price");
                        String category = document.getString("category");

                        items.add(new OSItems(id, prodName, quantity, price, category));
                    }

                    if (items.isEmpty()) {
                        Toast.makeText(this, "No Products Found", Toast.LENGTH_LONG).show();
                    } else {
                        OSPaymentAdapter = new OSPaymentAdapter(this, items);
                        recyclerView.setAdapter(OSPaymentAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error fetching data from Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to fetch cart items", Toast.LENGTH_LONG).show();
                });
    }

    public void confirmTrans() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = accessValue.user;

        db.collection("transactions")
                .orderBy("transID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long highestID = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            highestID = document.getLong("transID");
                        }

                        long finalHighestID = highestID;
                        db.collection("cartlist")
                                .whereEqualTo("user", currentUserID)
                                .get()
                                .addOnCompleteListener(cartListTask -> {
                                    if (cartListTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot cartDocument : cartListTask.getResult()) {
                                            String prodID = cartDocument.getString("id");
                                            String prodName = cartDocument.getString("prodName");
                                            String quantity = cartDocument.getString("quantity");
                                            String price = cartDocument.getString("price");
                                            String category = cartDocument.getString("category");

                                            long currentTime = System.currentTimeMillis();

                                            // Create a map to represent the order
                                            Map<String, Object> orderData = new HashMap<>();
                                            orderData.put("transID", String.valueOf(finalHighestID + 1));
                                            orderData.put("prodID", prodID);
                                            orderData.put("prodName", prodName);
                                            orderData.put("quantity", quantity);
                                            orderData.put("price", price);
                                            orderData.put("category", category);
                                            orderData.put("time", currentTime);
                                            orderData.put("status", "Preparing");

                                            // Add the order to Firestore
                                            db.collection("orders").add(orderData)
                                                    .addOnSuccessListener(documentReference -> {
                                                        Toast.makeText(getApplicationContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Handle failure
                                                    });
                                        }

                                        // Drop the cartlist collection or clear the user's cart
                                        db.collection("cartlist")
                                                .whereEqualTo("user", currentUserID)
                                                .get()
                                                .addOnCompleteListener(cartListDeleteTask -> {
                                                    if (cartListDeleteTask.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : cartListDeleteTask.getResult()) {
                                                            document.getReference().delete();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Handle failure to retrieve cart items
                                    }
                                });
                    } else {
                        // Handle failure to retrieve highest transID
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }
}