package com.example.posapp.OrderingSystem;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class osCart extends AppCompatActivity implements cartClickListener {

    TextView total;
    EditText tPayment;
    Button btnBack, payment;
    Double tPrice = 0.00;
    Double xPayment = 0.00;
    OSAdapter OSAdapter;
    List<OSItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_cart);

        btnBack = findViewById(R.id.btnBack);
        payment = findViewById(R.id.btnPayment);
        total = findViewById(R.id.totalPrice);
        tPayment = findViewById(R.id.txtPayment);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(osCart.this, OrderingSystem.class);
                startActivity(i);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tPrice <= 0){
                    Toast.makeText(getApplicationContext(), "No Items in Cart", Toast.LENGTH_SHORT).show();
                }else if (tPayment.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Payment Value is Blank", Toast.LENGTH_SHORT).show();
                    } else {
                        xPayment = Double.parseDouble(tPayment.getText().toString().trim());
                        if (xPayment < tPrice) {
                            Toast.makeText(getApplicationContext(), "Input Payment Value More Than the Price", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(getApplicationContext(), OSPayment.class);
                            i.putExtra("tPrice", tPrice.toString());
                            i.putExtra("tPayment", xPayment.toString());
                            startActivity(i);
                        }
                    }
            }
        });

        refreshList();
        total.setText("Total: ₱" + tPrice.toString() + "0");
    }

    public void refreshList() {
        RecyclerView recyclerView = findViewById(R.id.recycleCart1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartlistCollection = db.collection("cartlist");

        // Assuming you have the current user's ID stored in a variable called currentUserID
        String currentUserID = accessValue.user; // Replace this with the actual user ID

        cartlistCollection.whereEqualTo("user", currentUserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OSItems> items = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        String prodName = document.getString("prodName");
                        String quantity = document.getString("quantity");
                        String price = document.getString("price");
                        String category = document.getString("category");

                        items.add(new OSItems(id, prodName, quantity, price, category));

                        // Calculate total price and add it to the totalSum
                        tPrice += Double.parseDouble(price);
                    }

                    if (items.isEmpty()) {
                        Toast.makeText(this, "The Cart is Empty", Toast.LENGTH_LONG).show();
                    } else {
                        OSAdapter = new OSAdapter(this, items, this);
                        recyclerView.setAdapter(OSAdapter);

                        // Set the total sum to the TextView
                        total.setText(String.format("Total: ₱%.2f", tPrice));
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error fetching data from Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to fetch cart items", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onItemClicked(OSItems view) {
        Intent i = new Intent(getApplicationContext(), OSEditOrder.class);
        i.putExtra("id",view.getId());
        i.putExtra("prodName",view.getpName());
        i.putExtra("quantity",view.getpQuantity());
        i.putExtra("price",view.getpPrice());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
    }
}