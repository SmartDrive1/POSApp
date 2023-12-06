package com.example.posapp.pendingTrans;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.posapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ordersView extends AppCompatActivity {
    List<servingItems> sItems = new ArrayList<>();
    List<preparingItems> pItems = new ArrayList<>();
    servingAdapter servingAdapter;
    preparingAdapter preparingAdapter;
    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_view);

        scheduleRefresh();
    }

    public void loadOrders() {
        pItems.clear();
        sItems.clear();
        RecyclerView recyclerViewPreparing = findViewById(R.id.recyclePreparing);
        recyclerViewPreparing.setHasFixedSize(true);
        recyclerViewPreparing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        RecyclerView recyclerViewServing = findViewById(R.id.recycleServing);
        recyclerViewServing.setHasFixedSize(true);
        recyclerViewServing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Set<String> uniqueTransIDs = new HashSet<>();
        db.collection("orders")
                .orderBy("transID") // Adjust the orderBy clause based on your data structure
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String transID = document.getString("transID");
                            String status = document.getString("status");

                            // Check if the transID is unique
                            if (uniqueTransIDs.add(transID)) {
                                if ("Preparing".equals(status)) {
                                    pItems.add(new preparingItems(transID));
                                } else if ("Serving".equals(status)) {
                                    sItems.add(new servingItems(transID));
                                }
                            }
                        }

                        // Update RecyclerView adapters outside the loop
                        preparingAdapter = new preparingAdapter(this, pItems);
                        recyclerViewPreparing.setAdapter(preparingAdapter);

                        servingAdapter = new servingAdapter(this, sItems);
                        recyclerViewServing.setAdapter(servingAdapter);

                    } else {
                        Toast.makeText(this, "Error fetching orders: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void scheduleRefresh() {
        // Create a Runnable to run the refreshList() method
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadOrders();

                // Schedule the next refresh after 5 seconds
                handler.postDelayed(this, 5000);
            }
        };

        // Post the initial refresh immediately
        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the callback to prevent memory leaks
        handler.removeCallbacks(refreshRunnable);
    }
}