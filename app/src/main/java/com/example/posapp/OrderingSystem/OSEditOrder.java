package com.example.posapp.OrderingSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.posapp.R;
import com.example.posapp.pendingTrans.pendingTransaction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OSEditOrder extends AppCompatActivity {

    EditText editProduct, qty, editPrice, txtTprice;
    Button edit, remove, back;
    String newPrice;
    SQLiteDatabase db;
    String quantity;
    String id;
    String prodName;
    private DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_edit_order);
        db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        Intent i = getIntent();
        id = i.getStringExtra("id".toString());
        prodName = i.getStringExtra("prodName".toString());
        quantity = i.getStringExtra("quantity".toString());
        String price = i.getStringExtra("price".toString());

        edit = findViewById(R.id.btnEdit);
        remove = findViewById(R.id.btnRemove);
        back = findViewById(R.id.btnCancel);
        txtTprice = findViewById(R.id.txtTPrice);
        editProduct = findViewById(R.id.txtEditProduct);
        qty = findViewById(R.id.txtQuantity);
        editPrice = findViewById(R.id.txtEditPrice);

        editProduct.setText(prodName);
        qty.setText(quantity);
        txtTprice.setText(price);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }

        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OSEditOrder.this, osCart.class);
                startActivity(i);
            }
        });

        setPrice();
        change();
    }

    public void edit() {
        try {
            String qty1 = qty.getText().toString().trim();

            if (qty1.equals("")) {
                Toast.makeText(this, "Quantity is Blank. Please Input a Value", Toast.LENGTH_LONG).show();
            } else if (quantity.equals(qty1)) {
                Toast.makeText(this, "Order Updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(OSEditOrder.this, OrderingSystem.class);
                startActivity(i);
            } else if (Integer.parseInt(qty1) <= 0) {
                Toast.makeText(this, "Quantity is Invalid. Please Input More Than 0", Toast.LENGTH_LONG).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Step 1: Retrieve the product quantity from Firestore
                db.collection("products")
                        .whereEqualTo("id", id)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot productDocument = queryDocumentSnapshots.getDocuments().get(0);
                                String availableQuantityString = productDocument.getString("quantity");
                                if (availableQuantityString != null && availableQuantityString.matches("\\d+")) {
                                    int availableQuantity = Integer.parseInt(availableQuantityString);
                                    int requestedQuantity = Integer.parseInt(qty1);

                                    if (requestedQuantity <= availableQuantity) {
                                        // Step 2: Update the cartlist in Firestore
                                        updateCartItemAndReduceProductQuantity(id, qty1, newPrice);
                                        db.collection("cartlist")
                                                .whereEqualTo("id", id)
                                                .get()
                                                .addOnSuccessListener(cartListSnapshot -> {
                                                    if (!cartListSnapshot.isEmpty()) {
                                                        DocumentSnapshot cartDocument = cartListSnapshot.getDocuments().get(0);
                                                        cartDocument.getReference().update("quantity", qty1, "price", newPrice)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
                                                                    Intent i = new Intent(OSEditOrder.this, OrderingSystem.class);
                                                                    startActivity(i);
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(this, "Failed to update cartlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                    e.printStackTrace();
                                                                });
                                                    } else {
                                                        Toast.makeText(this, "Cart item not found", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Failed to retrieve cart item from Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                });
                                    } else {
                                        Toast.makeText(this, "Insufficient Stock", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // Handle the case where "quantity" in products is not a valid integer
                                    Toast.makeText(this, "Invalid quantity in products", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "Product not found", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to retrieve product from Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Input a Valid Quantity", Toast.LENGTH_LONG).show();
        }
    }

    public void updateCartItemAndReduceProductQuantity(String currentID, String newQuantity, String tPrice1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Retrieve current quantity from the 'cartlist'
        db.collection("cartlist")
                .whereEqualTo("id", currentID)
                .whereEqualTo("user",accessValue.user)
                .get()
                .addOnSuccessListener(cartListTask -> {
                    for (QueryDocumentSnapshot cartDocument : cartListTask) {
                        // If the cartlist document exists, retrieve the current quantity
                        String currentQuantityString = cartDocument.getString("quantity");

                        if (currentQuantityString != null && currentQuantityString.matches("\\d+")) {
                            int currentQuantity = Integer.parseInt(currentQuantityString);

                            int quantityDifference = Integer.parseInt(newQuantity) - currentQuantity;

                            // Step 2: Update cartlist document in Firestore
                            cartDocument.getReference()
                                    .update("quantity", newQuantity, "price", tPrice1)
                                    .addOnSuccessListener(aVoid -> {
                                        // Step 3: Successfully updated cartlist, now update the product quantity
                                        updateProductQuantity(currentID, quantityDifference);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update item in cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    });
                        } else {
                            // Handle the case where "quantity" in cartlist is not a valid integer
                            Toast.makeText(this, "Invalid quantity in cartlist", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve item from cartlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    private void updateProductQuantity(String currentID, int quantityDifference) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 4: Retrieve the product document from Firestore
        db.collection("products")
                .whereEqualTo("id", currentID)
                .get()
                .addOnSuccessListener(productTask -> {
                    for (QueryDocumentSnapshot productDocument : productTask) {
                        // If the product document exists, retrieve the existing quantity
                        String currentProductQuantityString = productDocument.getString("quantity");

                        if (currentProductQuantityString != null && currentProductQuantityString.matches("\\d+")) {
                            int currentProductQuantity = Integer.parseInt(currentProductQuantityString);

                            // Calculate the new available quantity
                            int newAvailableQuantity = currentProductQuantity - quantityDifference;

                            // Step 5: Update the quantity in the products collection
                            productDocument.getReference()
                                    .update("quantity", String.valueOf(newAvailableQuantity))
                                    .addOnSuccessListener(aVoid -> {
                                        // Additional actions after successful update
                                        Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update quantity in products: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    });
                        } else {
                            // Handle the case where "quantity" in products is not a valid integer
                            Toast.makeText(this, "Invalid quantity in products", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve item from products: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    public void delete() {
        removeitem();
    }

    public void newTotal() {
        double nPrice;
        nPrice = Double.parseDouble(qty.getText().toString()) * Double.parseDouble(editPrice.getText().toString());

        newPrice = String.valueOf(nPrice);
        txtTprice.setText(String.valueOf(nPrice));
    }

    public void setPrice(){
        Double inPrice = Double.parseDouble(txtTprice.getText().toString())/Double.parseDouble(qty.getText().toString());
        editPrice.setText(String.valueOf(inPrice));
    }

    public void change() {
        qty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (qty.getText().equals("")) {
                        Toast.makeText(OSEditOrder.this, "Quantity is Blank", Toast.LENGTH_LONG).show();
                    } else if (s.length() != 0) {
                        newTotal();
                    } else {
                        txtTprice.setText("");
                    }
                } catch (Exception e) {
                    Toast.makeText(OSEditOrder.this, "Please Input a Valid Value", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void removeitem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OSEditOrder.this);
        builder.setMessage("Are you sure you want to remove " + prodName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Update quantity in products collection
                        db.collection("products")
                                .whereEqualTo("id", id)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String quantityString = document.getString("quantity");

                                            if (quantityString != null) {
                                                int currentQuantity = Integer.parseInt(quantityString);
                                                int quantityToRemove = Integer.parseInt(quantity);
                                                int newQuantity = currentQuantity + quantityToRemove;

                                                if (newQuantity >= 0) {
                                                    // Update quantity in products
                                                    document.getReference().update("quantity", String.valueOf(newQuantity));

                                                    // Delete item from cartlist
                                                    db.collection("cartlist")
                                                            .whereEqualTo("prodName", prodName)
                                                            .whereEqualTo("user", accessValue.user)
                                                            .get()
                                                            .addOnCompleteListener(cartListTask -> {
                                                                if (cartListTask.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot cartDocument : cartListTask.getResult()) {
                                                                        cartDocument.getReference().delete();
                                                                        Toast.makeText(OSEditOrder.this, prodName + " Removed from Cart", Toast.LENGTH_LONG).show();
                                                                        Intent i = new Intent(OSEditOrder.this, osCart.class);
                                                                        startActivity(i);
                                                                    }
                                                                } else {
                                                                    Toast.makeText(OSEditOrder.this, "Failed to retrieve item from cartlist: " + cartListTask.getException(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(OSEditOrder.this, "Invalid quantity in products2", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(OSEditOrder.this, "Invalid quantity in products1", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(OSEditOrder.this, "Failed to retrieve item from products: " + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } catch (Exception e) {
                        Toast.makeText(OSEditOrder.this, "Failed", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}