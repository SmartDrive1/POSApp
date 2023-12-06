package com.example.posapp.pendingTrans;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;
import com.example.posapp.transactions.manageTransactions;
import com.example.posapp.transactions.transEdit;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class orderEdit extends AppCompatActivity {
    Button btnDone, btnCancel, btnBack, btnServing;
    TextView txtOrderID, txtOrderTime, txtStatus;
    String transID;
    String formattedDate, status;
    private DialogInterface.OnClickListener dialogClickListener;
    List<orderEditItems> items = new ArrayList<>();
    editOrderAdapter editOrderAdapter;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_edit);

        btnServing = findViewById(R.id.btnServing);
        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        txtOrderID = findViewById(R.id.txtOrderID);
        txtOrderTime = findViewById(R.id.txtOrderTime);
        txtStatus = findViewById(R.id.txtStatus);

        Intent i = getIntent();
        transID = i.getStringExtra("id".toString());
        status = i.getStringExtra("status".toString());
        formattedDate = i.getStringExtra("time".toString());

        txtOrderID.setText("Order ID: " + transID);
        txtStatus.setText("Status: " + status);
        txtOrderTime.setText("Order Time:\n" + formattedDate);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDone();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        btnServing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toServe();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(orderEdit.this, pendingTransaction.class);
                startActivity(i);
            }
        });

        if(status.equals("Serving")){
            btnServing.setEnabled(false);
            btnDone.setEnabled(true);
        }

        if(status.equals("Preparing")){
            btnDone.setEnabled(false);
        }

        refreshList();
    }

    public void refreshList(){
        RecyclerView recyclerView = findViewById(R.id.recycleOrderEdit);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .whereEqualTo("transID", transID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<orderEditItems> items = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String prodName = document.getString("prodName");
                        String quantity = document.getString("quantity");

                        items.add(new orderEditItems(prodName, quantity));
                    }

                    // Move the adapter and setAdapter out of the loop to set the adapter only once
                    editOrderAdapter = new editOrderAdapter(this, items);
                    recyclerView.setAdapter(editOrderAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle the case where there is an error fetching data from Firestore
                    e.printStackTrace();
                });
    }

    @SuppressLint("Range")
    public void orderDone() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionsCollection = db.collection("transactions");
        CollectionReference ordersCollection = db.collection("orders");

        int[] maxID = {0}; // Using an array to hold the value so it can be modified in the listener

        // Fetch the max transID from transactions collection
        db.collection("transactions")
                .orderBy("transID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        maxID[0] = documentSnapshot.getLong("transID").intValue();
                    }

                    // Increment the maxID
                    maxID[0]++;

                    // Fetch orders from Firestore
                    ordersCollection
                            .whereEqualTo("transID", transID)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots1) {
                                    String prodName = document.getString("prodName");
                                    String quantity = document.getString("quantity");
                                    String price = document.getString("price");
                                    String category = document.getString("category");
                                    long time = document.getLong("time");

                                    // Create a new transaction document
                                    Map<String, Object> transactionData = new HashMap<>();
                                    transactionData.put("transID", maxID[0]);
                                    transactionData.put("prodName", prodName);
                                    transactionData.put("quantity", quantity);
                                    transactionData.put("price", price);
                                    transactionData.put("category", category);
                                    transactionData.put("time", time);

                                    // Add the transaction document to the transactions collection
                                    transactionsCollection.add(transactionData);

                                    // Delete the order document
                                    document.getReference().delete();
                                }

                                Toast.makeText(orderEdit.this, "Order Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(orderEdit.this, pendingTransaction.class);
                                startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void cancelOrder(){
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        cancelOrderFunction();
                        Toast.makeText(orderEdit.this, "Order ID: " + transID + " Cancelled Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(orderEdit.this, pendingTransaction.class);
                        startActivity(i);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(orderEdit.this);
        builder.setMessage("Do You Want To Cancel Order " + transID + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void cancelOrderFunction() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ordersCollection = db.collection("orders");
        CollectionReference productsCollection = db.collection("products");

        ordersCollection.whereEqualTo("transID", transID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Retrieve the quantity and id from the document
                        String quantityString = document.getString("quantity");
                        String prodID = document.getString("prodID"); // Assuming there is an 'id' field in the orders collection

                        productsCollection.whereEqualTo("id", prodID)
                                .get()
                                .addOnSuccessListener(productsSnapshots -> {
                                    for (QueryDocumentSnapshot productDocument : productsSnapshots) {
                                        String currentQuantity = productDocument.getString("quantity");
                                        int newQuantity = Integer.parseInt(currentQuantity) + Integer.parseInt(quantityString);
                                        productDocument.getReference().update("quantity", String.valueOf(newQuantity));
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                });

                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Document deleted successfully
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void toServe(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();;
        CollectionReference ordersCollection = db.collection("orders");
        ordersCollection.whereEqualTo("transID", transID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("status", "Serving");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the case where there is an error fetching data from Firestore
                    e.printStackTrace();
                });
        txtStatus.setText("Status: Serving");
        btnServing.setEnabled(false);
        btnDone.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
    }
}