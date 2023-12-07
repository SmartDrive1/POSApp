package com.example.posapp.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;
import com.example.posapp.mainManageScreen;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class productList extends AppCompatActivity implements prodClickListener {

    Button btnAddProduct, back;
    TextView txtSearch;
    prodDrinksListAdapter productListAdapter;
    prodFoodListAdapter foodListAdapter;
    SpecialListAdapter specialListAdapter;
    CakeListAdapter CakeListAdapter;
    List<prodItems> items = new ArrayList<>();
    List<prodItems> foods = new ArrayList<>();
    List<prodItems> addOns = new ArrayList<>();
    List<prodItems> others = new ArrayList<>();
    ImageView rightIndicator1, leftIndicator1, rightIndicator2, leftIndicator2, rightIndicator3, leftIndicator3 , rightIndicator4, leftIndicator4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        back = findViewById(R.id.btnBack);
        txtSearch = findViewById(R.id.txtSearch);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        rightIndicator1 = findViewById(R.id.rightIndicator1);
        leftIndicator1 = findViewById(R.id.leftIndicator1);
        rightIndicator2 = findViewById(R.id.rightIndicator2);
        leftIndicator2 = findViewById(R.id.leftIndicator2);
        rightIndicator3 = findViewById(R.id.rightIndicator3);
        leftIndicator3 = findViewById(R.id.leftIndicator3);
        rightIndicator4 = findViewById(R.id.rightIndicator4);
        leftIndicator4 = findViewById(R.id.leftIndicator4);

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, mainManageScreen.class);
                startActivity(i);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, prodAdd.class);
                startActivity(i);
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

        loadProducts();
        search();
    }

    public void loadProducts() {
        items.clear();
        foods.clear();
        others.clear();

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
                            addOns.add(productItem);
                        } else if (category.equals("Special")) {
                            others.add(productItem);
                        }
                    }

                    // Set up RecyclerView adapters here
                    productListAdapter = new prodDrinksListAdapter(this, items, this);
                    recyclerView.setAdapter(productListAdapter);

                    foodListAdapter = new prodFoodListAdapter(this, foods, this);
                    foodRecyclerView.setAdapter(foodListAdapter);

                    CakeListAdapter = new CakeListAdapter(this, addOns, this);
                    addOnsRecycle.setAdapter(CakeListAdapter);

                    specialListAdapter = new SpecialListAdapter(this, others, this);
                    othersRecyclerView.setAdapter(specialListAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error fetching data from Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to fetch products", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onItemClicked(prodItems view) {
        Intent i = new Intent(getApplicationContext(), prodEdit.class);
        i.putExtra("id", view.getId());
        i.putExtra("product", view.getProduct());
        i.putExtra("prodPrice", view.getProdPrice());
        i.putExtra("category", view.getCategory());
        i.putExtra("quantity", view.getQuantity());
        if(view.getProdImage() != null){
            i.putExtra("prodImg", view.getProdImage());
        }else{
            //None
        }
        startActivity(i);
    }

    public void search(){
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtSearch.getText().equals("")){
                    loadProducts();
                }else{
                    clearTable();
                    String searchItem;
                    searchItem = txtSearch.getText().toString();

                    List<prodItems> items = new ArrayList<>();
                    List<prodItems> foods = new ArrayList<>();
                    List<prodItems> addOns = new ArrayList<>();
                    List<prodItems> others = new ArrayList<>();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference productsCollection = db.collection("products");


                    productsCollection
                            .whereGreaterThanOrEqualTo("product", searchItem)
                            .whereLessThanOrEqualTo("product", searchItem + "\uf8ff")
                            .orderBy("product", Query.Direction.ASCENDING)
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
                                        addOns.add(productItem);
                                    } else if (category.equals("Special")) {
                                        others.add(productItem);
                                    }
                                }

                                RecyclerView recyclerView = findViewById(R.id.recycleProds);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                                RecyclerView foodRecyclerView = findViewById(R.id.recycleFoods);
                                foodRecyclerView.setHasFixedSize(true);
                                foodRecyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                                RecyclerView addOnsRecycle = findViewById(R.id.recycleCakes);
                                addOnsRecycle.setHasFixedSize(true);
                                addOnsRecycle.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                                RecyclerView othersRecyclerView = findViewById(R.id.recycleSpecial);
                                othersRecyclerView.setHasFixedSize(true);
                                othersRecyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                                // Set up RecyclerView adapters here
                                productListAdapter = new prodDrinksListAdapter(productList.this, items, productListAdapter.mClickListener);
                                recyclerView.setAdapter(productListAdapter);

                                foodListAdapter = new prodFoodListAdapter(productList.this, foods, foodListAdapter.mClickListener);
                                foodRecyclerView.setAdapter(foodListAdapter);

                                CakeListAdapter = new CakeListAdapter(productList.this, addOns, CakeListAdapter.mClickListener);
                                addOnsRecycle.setAdapter(CakeListAdapter);

                                specialListAdapter = new SpecialListAdapter(productList.this, others, specialListAdapter.mClickListener);
                                othersRecyclerView.setAdapter(specialListAdapter);
                            })
                            .addOnFailureListener(e -> {
                                // Handle the case when there is an error fetching data from Firestore
                                e.printStackTrace();
                                Toast.makeText(productList.this, "Failed to fetch products", Toast.LENGTH_LONG).show();
                            });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void clearTable(){
        items.clear();
        foods.clear();
        addOns.clear();
        others.clear();
    }

    @Override
    public void onBackPressed() {
    }
}