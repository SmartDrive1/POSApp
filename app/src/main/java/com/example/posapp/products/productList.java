package com.example.posapp.products;

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

import com.example.posapp.MainScreen;
import com.example.posapp.R;

import java.util.ArrayList;
import java.util.List;

public class productList extends AppCompatActivity implements prodClickListener {

    Button btnAddProduct, back;
    prodDrinksListAdapter productListAdapter;
    prodFoodListAdapter foodListAdapter;
    prodOthersListAdapter othersListAdapter;
    addOnsListAdapter addOnsListAdapter;
    List<prodItems> items = new ArrayList<>();
    List<prodItems> foods = new ArrayList<>();
    List<prodItems> addOns = new ArrayList<>();
    List<prodItems> others = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        back = findViewById(R.id.btnBack);
        btnAddProduct = findViewById(R.id.btnAddProduct);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productList.this, MainScreen.class);
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
    }

    public void loadProducts(){
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
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from products", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Products Found", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int product = c.getColumnIndex("product");
            int category = c.getColumnIndex("category");
            int prodPrice = c.getColumnIndex("prodPrice");

            if(c.moveToFirst()){
                do{
                    if(c.getString(category).equals("Drinks")){
                        items.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        productListAdapter = new prodDrinksListAdapter(this, items, this);
                        recyclerView.setAdapter(productListAdapter);}
                    if(c.getString(category).equals("Food")){
                        foods.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        foodListAdapter = new prodFoodListAdapter(this, foods, this);
                        foodRecyclerView.setAdapter(foodListAdapter);}
                    if (c.getString(category).equals("AddOns")){
                        addOns.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        addOnsListAdapter = new addOnsListAdapter(this, addOns, this);
                        addOnsRecycle.setAdapter(addOnsListAdapter);}
                    if (c.getString(category).equals("Others")){
                        others.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        othersListAdapter = new prodOthersListAdapter(this, others, this);
                        othersRecyclerView.setAdapter(othersListAdapter);}
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
        startActivity(i);
    }
}