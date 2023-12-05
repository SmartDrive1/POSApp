package com.example.posapp.OrderingSystem;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.MainScreen;
import com.example.posapp.R;
import com.example.posapp.login;
import com.example.posapp.pendingTrans.pendingTransaction;
import com.example.posapp.products.CakeListAdapter;
import com.example.posapp.products.prodClickListener;
import com.example.posapp.products.prodDrinksListAdapter;
import com.example.posapp.products.prodFoodListAdapter;
import com.example.posapp.products.prodItems;
import com.example.posapp.products.SpecialListAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderingSystem extends AppCompatActivity implements prodClickListener {
    Button btnAdd, Orders, btnPending;
    Button btnLogout;
    TextView txtView;
    ImageView prodImg;
    EditText Quantity, Price, totalPriceUp, prodName;
    RadioGroup radioGroup;
    RadioButton medium, large;
    prodDrinksListAdapter productListAdapter;
    prodFoodListAdapter foodListAdapter;
    SpecialListAdapter specialListAdapter;
    CakeListAdapter CakeListAdapter;
    List<prodItems> items = new ArrayList<>();
    List<prodItems> foods = new ArrayList<>();
    List<prodItems> Cakes = new ArrayList<>();
    List<prodItems> Special = new ArrayList<>();
    SQLiteDatabase db;
    String currentID, itemCategory, currentProduct;
    ImageView rightIndicator1, leftIndicator1, rightIndicator2, leftIndicator2, rightIndicator3, leftIndicator3 , rightIndicator4, leftIndicator4;
    View v;
    Double addOn = 0.00;
    RecyclerView recyclerView, foodRecyclerView, addOnsRecycle, othersRecyclerView;

    private DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_system);
        db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        v = findViewById(R.id.sizeLayout);
        btnAdd = findViewById(R.id.btnCart);
        btnLogout = findViewById(R.id.btnLogout);
        btnPending = findViewById(R.id.btnPending);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        prodName = findViewById(R.id.prodName);
        txtView = findViewById(R.id.txtView);
        totalPriceUp = findViewById(R.id.txtTotalPrice);
        Orders = findViewById(R.id.btnOrders);
        prodImg = findViewById(R.id.prodImg);
        radioGroup = findViewById(R.id.radioGroup);
        medium = findViewById(R.id.medium);
        large = findViewById(R.id.large);
        v.setVisibility(View.GONE);
        Quantity.setEnabled(false);
        rightIndicator1 = findViewById(R.id.rightIndicator1);
        leftIndicator1 = findViewById(R.id.leftIndicator1);
        rightIndicator2 = findViewById(R.id.rightIndicator2);
        leftIndicator2 = findViewById(R.id.leftIndicator2);
        rightIndicator3 = findViewById(R.id.rightIndicator3);
        leftIndicator3 = findViewById(R.id.leftIndicator3);
        rightIndicator4 = findViewById(R.id.rightIndicator4);
        leftIndicator4 = findViewById(R.id.leftIndicator4);

        recyclerView = findViewById(R.id.recycleProds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        foodRecyclerView = findViewById(R.id.recycleFoods);
        foodRecyclerView.setHasFixedSize(true);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        addOnsRecycle = findViewById(R.id.recycleCakes);
        addOnsRecycle.setHasFixedSize(true);
        addOnsRecycle.setLayoutManager(new LinearLayoutManager(OrderingSystem.this,LinearLayoutManager.HORIZONTAL, false));

        othersRecyclerView = findViewById(R.id.recycleSpecial);
        othersRecyclerView.setHasFixedSize(true);
        othersRecyclerView.setLayoutManager(new LinearLayoutManager(OrderingSystem.this,LinearLayoutManager.HORIZONTAL, false));

        productListAdapter = new prodDrinksListAdapter(this, items, this);
        foodListAdapter = new prodFoodListAdapter(this, foods, this);
        CakeListAdapter = new CakeListAdapter(this, Cakes, this);
        specialListAdapter = new SpecialListAdapter(this, Special, this);

        recyclerView.setAdapter(productListAdapter);
        foodRecyclerView.setAdapter(foodListAdapter);
        addOnsRecycle.setAdapter(CakeListAdapter);
        othersRecyclerView.setAdapter(specialListAdapter);


        switch (accessValue.access){
            case "User":
                btnLogout.setText("Logout");
                break;
            default:
                btnLogout.setText("Back");
                break;
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accessValue.access.equals("User")){
                    db.close();
                    Intent i = new Intent(OrderingSystem.this, login.class);
                    startActivity(i);
                }else{
                    db.close();
                    Intent i = new Intent(OrderingSystem.this, MainScreen.class);
                    startActivity(i);
                }
            }
        });

        btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Quantity.getText().toString().equals("")) {
                    db.close();
                    Intent i = new Intent(OrderingSystem.this, pendingTransaction.class);
                    startActivity(i);
                }else{
                    toOrders();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
                Quantity.setText("");
                totalPriceUp.setText("");
            }
        });

        Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCart();
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodName.setText(currentProduct + "(M)");
                addOn = 0.00;
                updatePrice();
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodName.setText(currentProduct + "(L)");
                addOn = 20.00;
                updatePrice();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator1.setVisibility(View.GONE);
                    leftIndicator1.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator1.setVisibility(View.GONE);
                        leftIndicator1.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator1.setVisibility(View.VISIBLE);
                        leftIndicator1.setVisibility(View.GONE);
                    }
                }
            }
        });

        foodRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator2.setVisibility(View.GONE);
                    leftIndicator2.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator2.setVisibility(View.GONE);
                        leftIndicator2.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator2.setVisibility(View.VISIBLE);
                        leftIndicator2.setVisibility(View.GONE);
                    }
                }
            }
        });

        addOnsRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator3.setVisibility(View.GONE);
                    leftIndicator3.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator3.setVisibility(View.GONE);
                        leftIndicator3.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator3.setVisibility(View.VISIBLE);
                        leftIndicator3.setVisibility(View.GONE);
                    }
                }
            }
        });

        othersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator4.setVisibility(View.GONE);
                    leftIndicator4.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator4.setVisibility(View.GONE);
                        leftIndicator4.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator4.setVisibility(View.VISIBLE);
                        leftIndicator4.setVisibility(View.GONE);
                    }
                }
            }
        });

        refreshList();
        change();
    }

    @SuppressLint("Range")
    public void add() {
        if (prodName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please Select a Product", Toast.LENGTH_LONG).show();
        } else if (Quantity.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please Input a Valid Quantity", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(String.valueOf(Quantity.getText())) <= 0) {
            Toast.makeText(this, "Please Input Quantity More Than 0", Toast.LENGTH_LONG).show();
        } else {
            try {
                total();
                String tPrice1 = totalPriceUp.getText().toString().trim();
                String qty2 = Quantity.getText().toString().trim();
                String prodName1 = prodName.getText().toString().trim();
                AtomicBoolean repeat = new AtomicBoolean(false);

                // Assuming you have initialized Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Check if the product is already in the cartlist
                db.collection("cartlist")
                        .whereEqualTo("id", currentID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String storedProdName = document.getString("prodName");
                                    if (prodName1.equals(storedProdName)) {
                                        repeat.set(true);
                                        break;
                                    }
                                }
                                checkQuantityAndPerformAction(repeat.get(), currentID, qty2, tPrice1);
                            } else {
                                Toast.makeText(this, "Failed to check cartlist: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void checkQuantityAndPerformAction(boolean repeat, String currentID, String qty2, String tPrice1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .document(currentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the 'quantity' field exists and is not null
                            if (document.contains("quantity") && document.get("quantity") != null) {
                                // Convert the 'quantity' field from string to int
                                AtomicInteger availableQuantity = new AtomicInteger(Integer.parseInt(String.valueOf(document.get("quantity"))));
                                int requestedQuantity = Integer.parseInt(qty2);

                                if (repeat) {
                                    // If the item is already in the cartlist, check and update
                                    db.collection("cartlist")
                                            .document(currentID)
                                            .get()
                                            .addOnCompleteListener(cartTask -> {
                                                if (cartTask.isSuccessful()) {
                                                    DocumentSnapshot cartDocument = cartTask.getResult();
                                                    if (cartDocument.exists()) {
                                                        int currentCartQuantity = Integer.parseInt(String.valueOf(cartDocument.get("quantity")));
                                                        availableQuantity.addAndGet(currentCartQuantity);

                                                        if (requestedQuantity <= availableQuantity.get()) {
                                                            updateCartItemAndReduceProductQuantityFirestore(currentID, qty2, tPrice1);
                                                            prodName.setText("");
                                                            Quantity.setText("");
                                                            totalPriceUp.setText("");
                                                            Price.setText("");
                                                            v.setVisibility(View.GONE);
                                                            prodImg.setImageResource(R.drawable.noimage);
                                                            Quantity.setEnabled(false);
                                                        } else {
                                                            Toast.makeText(this, "Insufficient Stock", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(this, "Failed to check cartlist: " + cartTask.getException(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    // If the item is not in the cartlist, check and add
                                    if (requestedQuantity <= availableQuantity.get()) {
                                        insertFirestore(currentID,  prodName.getText().toString(), qty2, itemCategory, tPrice1);
                                    } else {
                                        Toast.makeText(this, "Insufficient Stock for " + prodName.getText().toString(), Toast.LENGTH_LONG).show();
                                        prodName.setText("");
                                        Quantity.setText("");
                                        totalPriceUp.setText("");
                                        Price.setText("");
                                        prodImg.setImageResource(R.drawable.noimage);
                                        Quantity.setEnabled(false);
                                    }
                                }
                            } else {
                                // Handle the case where 'quantity' is not present or is null.
                                Toast.makeText(this, "Quantity field is missing or null in Firestore document", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to check products: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void insertFirestore(String currentID, String prodName, String quantity, String category, String price) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Convert quantity and price to the appropriate types
        int quantityValue = Integer.parseInt(quantity);
        double priceValue = Double.parseDouble(price);

        // Create an instance of the ProductData class
        ProductData productData = new ProductData(prodName, quantityValue, category, priceValue);

        // Add the product data to Firestore
        db.collection("cartlist")
                .document(currentID)
                .set(productData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, prodName + " Added to Cart", Toast.LENGTH_LONG).show();
                    refreshList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    private void decreaseProductQuantityFirestore(int currentID, int quantityToSubtract) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference productRef = db.collection("products").document(String.valueOf(currentID));

        productRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int currentQuantity = document.getLong("quantity").intValue();

                            // Log the current and requested quantities for debugging
                            Log.d("Firestore", "Current Quantity: " + currentQuantity);
                            Log.d("Firestore", "Requested Quantity To Subtract: " + quantityToSubtract);

                            // Calculate the new quantity
                            int newQuantity = currentQuantity - quantityToSubtract;

                            // Log the new quantity for debugging
                            Log.d("Firestore", "New Quantity: " + newQuantity);

                            // Update the product quantity
                            productRef
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        // Successfully updated product quantity
                                        Log.d("Firestore", "Product Quantity Updated: " + newQuantity);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Failed to update product quantity: " + e.getMessage());
                                        e.printStackTrace();
                                    });
                        } else {
                            Log.e("Firestore", "Product document does not exist");
                        }
                    } else {
                        Log.e("Firestore", "Failed to get product document: " + task.getException());
                        task.getException().printStackTrace();
                    }
                });
    }

    private void updateCartItemAndReduceProductQuantityFirestore(String currentID, String newQuantity, String tPrice1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current quantity in the cartlist
        db.collection("cartlist")
                .document(currentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int currentQuantityInCart = document.getLong("quantity").intValue();

                            // Calculate the quantity difference
                            int quantityDifference = Integer.parseInt(newQuantity) - currentQuantityInCart;

                            // Update the cartlist document
                            db.collection("cartlist")
                                    .document(currentID)
                                    .update("quantity", Integer.parseInt(newQuantity), "price", Double.parseDouble(tPrice1))
                                    .addOnSuccessListener(aVoid -> {
                                        // Successfully updated cartlist, now update products
                                        decreaseProductQuantityFirestore(Integer.parseInt(currentID), quantityDifference);

                                        Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();
                                        refreshList();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update item in cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    });
                        } else {
                            Toast.makeText(this, "Cartlist document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to get cartlist document: " + task.getException(), Toast.LENGTH_LONG).show();
                        task.getException().printStackTrace();
                    }
                });
    }

    public void updatePrice() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(currentID);

        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String prodPrice = document.getString("prodPrice");
                    if (prodPrice != null) {
                        // Convert the price to double
                        double productPrice = Double.parseDouble(prodPrice);
                        double total = productPrice + addOn;
                        Price.setText(String.format("%.2f", total));
                    } else {
                        // Handle the case where prodPrice is null
                        Price.setText("");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    // Handle the case where the document does not exist
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                // Handle errors while fetching the document
            }
        });
    }

    public void total(){
        if (String.valueOf(Quantity.getText()).equals("")){

        }else if(Integer.parseInt(String.valueOf(Quantity.getText())) <= 0){
            Toast.makeText(OrderingSystem.this,"Quantity Must Be Greater Than 1", Toast.LENGTH_LONG).show();
        }else{
            double qty1 = Double.parseDouble(Quantity.getText().toString());
            double price1 = Double.parseDouble(Price.getText().toString());
            double totalPrice = qty1 * price1;

            totalPriceUp.setText(String.valueOf(Double.valueOf(totalPrice)) + "0");
        }
    }

    public void refreshList() {
        items.clear();
        foods.clear();
        Cakes.clear();
        Special.clear();

        RecyclerView recyclerView = findViewById(R.id.recycleProds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView foodRecyclerView = findViewById(R.id.recycleFoods);
        foodRecyclerView.setHasFixedSize(true);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView addOnsRecycle = findViewById(R.id.recycleCakes);
        addOnsRecycle.setHasFixedSize(true);
        addOnsRecycle.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView othersRecyclerView = findViewById(R.id.recycleSpecial);
        othersRecyclerView.setHasFixedSize(true);
        othersRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsCollection = db.collection("products");

        productsCollection.orderBy("product", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        String product = document.getString("product");
                        String category = document.getString("category");
                        String prodPrice = document.getString("prodPrice");
                        String quantity = document.getString("quantity");

                        Object prodImgObject = document.get("prodImg");
                        String prodImageBase64 = null;

                        if (prodImgObject instanceof String) {
                            // Handle the case when 'userImg' is stored as a Base64 encoded string in Firestore
                            prodImageBase64 = (String) prodImgObject;
                        }

                        prodItems productItem = new prodItems(id, product, category, prodPrice, quantity, prodImageBase64);

                        if (category.equals("Drinks")) {
                            items.add(productItem);
                        } else if (category.equals("Food")) {
                            foods.add(productItem);
                        } else if (category.equals("Cake")) {
                            Cakes.add(productItem);
                        } else if (category.equals("Special")) {
                            Special.add(productItem);
                        }
                    }

                    // Set up RecyclerView adapters here
                    productListAdapter = new prodDrinksListAdapter(this, items, this);
                    recyclerView.setAdapter(productListAdapter);

                    foodListAdapter = new prodFoodListAdapter(this, foods, this);
                    foodRecyclerView.setAdapter(foodListAdapter);

                    CakeListAdapter = new CakeListAdapter(this, Cakes, this);
                    addOnsRecycle.setAdapter(CakeListAdapter);

                    specialListAdapter = new SpecialListAdapter(this, Special, this);
                    othersRecyclerView.setAdapter(specialListAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error fetching data from Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to fetch products", Toast.LENGTH_LONG).show();
                });
    }

    public void change(){//text change
        Quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (prodName.getText().equals("")) {
                        // If prodName is blank, do nothing
                    } else if (Price.getText().equals("")) {
                        // If Price is blank, refresh the list
                    } else if (s.length() != 0) {
                        total();
                    } else {
                        totalPriceUp.setText("");
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClicked(prodItems view) {
        currentID = view.getId();
        prodName.setText(view.getProduct());
        itemCategory = view.getCategory();
        currentProduct = view.getProduct();
        medium.toggle();
        Quantity.setText("");
        Quantity.setEnabled(true);

        if (view.getCategory().equals("Drinks")) {
            v.setVisibility(View.VISIBLE);
            addOn = 0.00;
            prodName.setText(view.getProduct() + "(M)");
        } else {
            v.setVisibility(View.GONE);
        }

        // Assuming you have a Firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(view.getId());

        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String prodImageBase64 = document.getString("prodImg");
                    if (prodImageBase64 != null) {
                        // Decode the base64 string to a byte array
                        byte[] prodImageBytes = Base64.decode(prodImageBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(prodImageBytes, 0, prodImageBytes.length);
                        prodImg.setImageBitmap(bitmap);
                    } else {
                        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                        prodImg.setImageBitmap(defaultBitmap);
                    }
                    updatePrice();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void toCart(){
        if(Quantity.getText().toString().trim().equals("")){
            db.close();
            Intent i = new Intent(OrderingSystem.this, osCart.class);
            startActivity(i);
        }else {
            dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            db.close();
                            Intent i = new Intent(OrderingSystem.this, osCart.class);
                            startActivity(i);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderingSystem.this);
            builder.setMessage("Products have not been added. Do you want to proceed in Cart Menu?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public void toOrders(){
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        db.close();
                        Intent i = new Intent(OrderingSystem.this, pendingTransaction.class);
                        startActivity(i);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderingSystem.this);
        builder.setMessage("Products have not been added. Do you want to proceed in Orders?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
    }
}