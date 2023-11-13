package com.example.posapp.products;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;
import com.example.posapp.mainManageScreen;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        back = findViewById(R.id.btnBack);
        txtSearch = findViewById(R.id.txtSearch);
        btnAddProduct = findViewById(R.id.btnAddProduct);

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

        loadProducts();
        search();
    }

    public void loadProducts(){
        items.clear();
        foods.clear();
        others.clear();
        RecyclerView recyclerView = findViewById(R.id.recycleProds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView foodRecyclerView = findViewById(R.id.recycleFoods);
        foodRecyclerView.setHasFixedSize(true);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView addOnsRecycle = findViewById(R.id.recycleAddOns);
        addOnsRecycle.setHasFixedSize(true);
        addOnsRecycle.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView othersRecyclerView = findViewById(R.id.recycleOthers);
        othersRecyclerView.setHasFixedSize(true);
        othersRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, quantity INTEGER, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from products", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Products Found", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int product = c.getColumnIndex("product");
            int category = c.getColumnIndex("category");
            int prodPrice = c.getColumnIndex("prodPrice");
            int quantity = c.getColumnIndex("quantity");

            if(c.moveToFirst()){
                do{
                    if(c.getString(category).equals("Drinks")){
                        items.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                        productListAdapter = new prodDrinksListAdapter(this, items, this);
                        recyclerView.setAdapter(productListAdapter);}
                    if(c.getString(category).equals("Food")){
                        foods.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                        foodListAdapter = new prodFoodListAdapter(this, foods, this);
                        foodRecyclerView.setAdapter(foodListAdapter);}
                    if (c.getString(category).equals("Cake")){
                        addOns.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                        CakeListAdapter = new CakeListAdapter(this, addOns, this);
                        addOnsRecycle.setAdapter(CakeListAdapter);}
                    if (c.getString(category).equals("Special")){
                        others.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                        specialListAdapter = new SpecialListAdapter(this, others, this);
                        othersRecyclerView.setAdapter(specialListAdapter);}
                }while(c.moveToNext());
            }
            c.close();
            db.close();
        }
    }

    @Override
    public void onItemClicked(prodItems view) {
        Intent i = new Intent(getApplicationContext(), prodEdit.class);
        i.putExtra("id", view.getId());
        i.putExtra("product", view.getProduct());
        i.putExtra("prodPrice", view.getProdPrice());
        i.putExtra("category", view.getCategory());
        i.putExtra("quantity", view.getQuantity());
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
                    RecyclerView recyclerView = findViewById(R.id.recycleProds);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                    RecyclerView foodRecyclerView = findViewById(R.id.recycleFoods);
                    foodRecyclerView.setHasFixedSize(true);
                    foodRecyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                    RecyclerView addOnsRecycle = findViewById(R.id.recycleAddOns);
                    addOnsRecycle.setHasFixedSize(true);
                    addOnsRecycle.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                    RecyclerView othersRecyclerView = findViewById(R.id.recycleOthers);
                    othersRecyclerView.setHasFixedSize(true);
                    othersRecyclerView.setLayoutManager(new LinearLayoutManager(productList.this,LinearLayoutManager.HORIZONTAL, false));

                    SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, quantity, INTEGER, prodPrice INTEGER)"); //Create database if non-existent, to avoid crash
                    String query = "SELECT * FROM products WHERE product LIKE ?";
                    String[] selectionArgs = {"%" + searchItem + "%"};

                    Cursor c = db.rawQuery(query, selectionArgs);
                    count = c.getCount();

                    if(count == 0){
                        Toast.makeText(productList.this,"No Products Found", Toast.LENGTH_LONG).show();
                    }else{
                        int id = c.getColumnIndex("id");
                        int product = c.getColumnIndex("product");
                        int category = c.getColumnIndex("category");
                        int prodPrice = c.getColumnIndex("prodPrice");
                        int quantity = c.getColumnIndex("quantity");

                        if(c.moveToFirst()){
                            do{
                                if(c.getString(category).equals("Drinks")){
                                    items.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                                    productListAdapter = new prodDrinksListAdapter(productList.this, items, productListAdapter.mClickListener);
                                    recyclerView.setAdapter(productListAdapter);}
                                if(c.getString(category).equals("Food")){
                                    foods.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                                    foodListAdapter = new prodFoodListAdapter(productList.this, foods, foodListAdapter.mClickListener);
                                    foodRecyclerView.setAdapter(foodListAdapter);}
                                if (c.getString(category).equals("AddOns")){
                                    addOns.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                                    CakeListAdapter = new CakeListAdapter(productList.this, addOns, CakeListAdapter.mClickListener);
                                    addOnsRecycle.setAdapter(CakeListAdapter);}
                                if (c.getString(category).equals("Others")){
                                    others.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity)));
                                    specialListAdapter = new SpecialListAdapter(productList.this, others, specialListAdapter.mClickListener);
                                    othersRecyclerView.setAdapter(specialListAdapter);}
                            }while(c.moveToNext());
                        }
                        c.close();
                        db.close();
                    }
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
}