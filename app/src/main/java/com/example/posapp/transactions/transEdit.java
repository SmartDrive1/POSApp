package com.example.posapp.transactions;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class transEdit extends AppCompatActivity {
    Button btnBack, btnDelete;
    TextView txtTotalPrice, txtTransID, txtDateTime;
    transEditAdapter transEditAdapter;
    List<transEditItems> items = new ArrayList<>();
    String transID;
    double sum = 0.0;
    String formattedDate;
    Long newTrans;

    private DialogInterface.OnClickListener dialogClickListener;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_edit);

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        txtTransID = findViewById(R.id.txtTransID);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtDateTime = findViewById(R.id.txtDateTime);

        Intent i = getIntent();
        transID = i.getStringExtra("id".toString());
        formattedDate = i.getStringExtra("time".toString());

        txtTransID.setText("Transaction ID: " + String.valueOf(transID));

        newTrans = Long.valueOf(transID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionsCollection = db.collection("transactions");

        transactionsCollection
                .whereEqualTo("transID", newTrans)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    sum = 0.0;

                    if (queryDocumentSnapshots.isEmpty()) {
                        System.out.println("No documents found with transID: " + transID);
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String priceString = document.getString("price");

                            // Convert priceString to a numeric value (double) and add to the sum
                            try {
                                double price = Double.parseDouble(priceString);
                                sum += price;
                            } catch (NumberFormatException e) {
                                // Handle parsing error if the priceString is not a valid double
                                e.printStackTrace();
                            }
                        }
                        txtTotalPrice.setText("Total: " + sum + "0");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });


        txtDateTime.setText("Date/Time:\n" + formattedDate);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(transEdit.this, manageTransactions.class);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        refreshList();
    }

    public void refreshList() {
        RecyclerView recyclerView = findViewById(R.id.recycleTransEdit);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionsCollection = db.collection("transactions");

        transactionsCollection
                .whereEqualTo("transID", newTrans) // Replace transID with the desired transID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    items.clear(); // Clear existing items before adding new ones

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String prodName = document.getString("prodName");
                        String quantity = document.getString("quantity");
                        String category = document.getString("category");
                        String price = document.getString("price");

                        items.add(new transEditItems(prodName, quantity, category, price));
                    }

                    transEditAdapter = new transEditAdapter(this, items);
                    recyclerView.setAdapter(transEditAdapter);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    // Handle failure, e.g., show an error message
                });
    }

    public void delete() {
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference transactionsCollection = db.collection("transactions");

                        // Assuming "transID" is the field name in Firestore
                        transactionsCollection.whereEqualTo("transID", Integer.parseInt(transID))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        document.getReference().delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(transEdit.this, "Transaction: " + transID + " Successfully Deleted", Toast.LENGTH_SHORT).show();

                                                        // Navigate to the desired activity after deletion
                                                        Intent i = new Intent(transEdit.this, manageTransactions.class);
                                                        startActivity(i);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        e.printStackTrace();
                                                        // Handle failure, e.g., show an error message
                                                        Toast.makeText(transEdit.this, "Failed to delete transaction: " + transID, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        // Handle failure, e.g., show an error message
                                        Toast.makeText(transEdit.this, "Failed to find transaction: " + transID, Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(transEdit.this);
        builder.setMessage("Do You Want To Delete Transaction " + transID + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
    }
}
