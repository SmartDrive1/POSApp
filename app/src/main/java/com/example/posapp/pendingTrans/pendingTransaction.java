package com.example.posapp.pendingTrans;

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
import android.widget.Toast;

import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class pendingTransaction extends AppCompatActivity implements pendingClickListener{

    Button btnOrdersView, btnBack;
    pendingAdapter pendingAdapter;
    List<pendingItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transaction);

        btnBack = findViewById(R.id.btnBack);
        btnOrdersView = findViewById(R.id.btnOrdersView);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(pendingTransaction.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        btnOrdersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(pendingTransaction.this, ordersView.class);
                startActivity(i);
            }
        });

        loadOrders();
    }

    public void loadOrders() {
        items.clear();

        RecyclerView recyclerView = findViewById(R.id.recycleOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming "orders" is your Firestore collection name
        db.collection("orders")
                .orderBy("transID", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    Set<String> processedTransactions = new HashSet<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String transID = document.getString("transID");
                        String status = document.getString("status");
                        long time = document.getLong("time");

                        if (processedTransactions.add(transID)) {
                            // Process only the first occurrence for each transID
                            if (time != 0) {
                                String formattedDate = dateFormat.format(new Date(time));
                                items.add(new pendingItems(transID, status, formattedDate));
                            }
                        }
                    }

                    if (items.isEmpty()) {
                        Toast.makeText(this, "No Pending Orders", Toast.LENGTH_LONG).show();
                    } else {
                        // Set the adapter only once after processing all documents
                        pendingAdapter = new pendingAdapter(this, items, this);
                        recyclerView.setAdapter(pendingAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving pending orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    @Override
    public void onItemClicked(pendingItems view) {
        Intent i = new Intent(getApplicationContext(), orderEdit.class);
        i.putExtra("id", view.getTransID());
        i.putExtra("status", view.getStatus());
        i.putExtra("time",view.getOrderTime());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
    }
}