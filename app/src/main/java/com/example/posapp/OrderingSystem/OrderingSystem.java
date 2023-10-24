package com.example.posapp.OrderingSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.MainScreen;
import com.example.posapp.R;
import com.example.posapp.login;
import com.example.posapp.products.prodClickListener;
import com.example.posapp.products.prodDrinksListAdapter;
import com.example.posapp.products.prodFoodListAdapter;
import com.example.posapp.products.prodItems;
import com.example.posapp.products.prodOthersListAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrderingSystem extends AppCompatActivity implements prodClickListener {
    Button btnAdd, btnBack, Orders;
    ImageButton btnLogout;
    TextView txtView;
    EditText Quantity, Price, totalPriceUp, prodName;
    prodDrinksListAdapter productListAdapter;
    prodFoodListAdapter foodListAdapter;
    prodOthersListAdapter othersListAdapter;
    List<prodItems> items = new ArrayList<>();
    List<prodItems> foods = new ArrayList<>();
    List<prodItems> others = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_system);

        btnAdd = findViewById(R.id.btnCart);
        btnBack = findViewById(R.id.btnCancel);
        btnLogout = findViewById(R.id.btnLogout);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        prodName = findViewById(R.id.prodName);
        txtView = findViewById(R.id.txtView);
        totalPriceUp = findViewById(R.id.txtTotalPrice);
        Orders = findViewById(R.id.btnOrders);

        if (accessValue.access.equals("User")){
            btnBack.setVisibility(View.GONE);
        }else{
            btnLogout.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = txtView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            txtView.setLayoutParams(params);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderingSystem.this, login.class);
                startActivity(i);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderingSystem.this, MainScreen.class);
                startActivity(i);
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
                Intent i = new Intent(OrderingSystem.this, osCart.class);
                startActivity(i);
            }
        });

        refreshList();
        change();
    }

    public void add() {
        if(prodName.getText().toString().equals("")){//Check if ProdName is Blank
            Toast.makeText(this, "Please Select a Product", Toast.LENGTH_LONG).show();
        }else if (Quantity.getText().toString().equals("")) {//check if blank
            Toast.makeText(this, "Please Input a Valid Quantity", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(String.valueOf(Quantity.getText())) <= 0) {//check if more than 0
            Toast.makeText(this, "Please Input Quantity More Than 0", Toast.LENGTH_LONG).show();
        }else
            try {
            total();
            String tPrice1 = totalPriceUp.getText().toString();
            String qty2 = Quantity.getText().toString();
            String prodName1 = prodName.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(prodName VARCHAR PRIMARY KEY, quantity INTEGER, price DOUBLE)");

            // Check if an item with the same prodName already exists
            Cursor cursor = db.rawQuery("SELECT * FROM cartlist WHERE prodName=?", new String[]{prodName1});
            if (cursor.getCount() > 0) {
                // Update the existing item's quantity and price
                db.execSQL("UPDATE cartlist SET quantity=?, price=? WHERE prodName=?", new String[]{qty2, tPrice1, prodName1});
                Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();
            } else {
                // Insert a new item
                String sql = "INSERT INTO cartlist (prodName, quantity, price) VALUES (?, ?, ?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, prodName1);
                statement.bindString(2, String.valueOf(Integer.parseInt(qty2)));
                statement.bindString(3, String.valueOf(Double.parseDouble(tPrice1)));
                statement.execute();
                Toast.makeText(this, "Item Added to Cart", Toast.LENGTH_LONG).show();
            }

            Quantity.setText("");
            totalPriceUp.setText("");
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void updatePrice() {
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, prodPrice INTEGER)");//in case there are no tables yet
        final Cursor c = db.rawQuery("SELECT * FROM products WHERE product ='" + prodName.getText() + "'", null);

        if (c.moveToFirst()) {
            int prodPriceIndex = c.getColumnIndex("prodPrice");
            int productPrice = c.getInt(prodPriceIndex);
            Price.setText(String.valueOf(productPrice));
        } else {
            Price.setText("N/A");
        }
        c.close();
        db.close();
    }

    public void total(){
        if (String.valueOf(Quantity.getText()).equals("")){
            Toast.makeText(OrderingSystem.this,"Please Input a Quantity", Toast.LENGTH_LONG).show();
        }else if(Integer.parseInt(String.valueOf(Quantity.getText())) <= 0){
            Toast.makeText(OrderingSystem.this,"Quantity Must Be Greater Than 1", Toast.LENGTH_LONG).show();
        }else{
            double qty1 = Double.parseDouble(Quantity.getText().toString());
            double price1 = Double.parseDouble(Price.getText().toString());
            double totalPrice = qty1 * price1;

            totalPriceUp.setText(String.valueOf(Double.valueOf(totalPrice)));
        }
    }

    public void refreshList() {
        RecyclerView recyclerView = findViewById(R.id.recycleProds);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView foodRecyclerView = findViewById(R.id.recycleFoods);
        foodRecyclerView.setHasFixedSize(true);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

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
                        recyclerView.setAdapter(productListAdapter);
                    }else if(c.getString(category).equals("Food")){
                        foods.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        foodListAdapter = new prodFoodListAdapter(this, foods, this);
                        foodRecyclerView.setAdapter(foodListAdapter);
                    }else{
                        others.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice)));
                        othersListAdapter = new prodOthersListAdapter(this, others, this);
                        othersRecyclerView.setAdapter(othersListAdapter);
                    }
                }while(c.moveToNext());
            }
            c.close();
            db.close();
        }
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
                    if (Price.getText().equals("")) {
                        Toast.makeText(OrderingSystem.this, "No Product Selected", Toast.LENGTH_LONG).show();
                    } else if (s.length() != 0) {
                        total();
                    } else {
                        totalPriceUp.setText("");
                    }
                }catch (Exception e){
                    Toast.makeText(OrderingSystem.this, "Please Input a Valid Value", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemClicked(prodItems view) {
        prodName.setText(view.getProduct());
        updatePrice();
    }
}