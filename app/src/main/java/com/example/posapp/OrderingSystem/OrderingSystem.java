package com.example.posapp.OrderingSystem;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.MainScreen;
import com.example.posapp.R;
import com.example.posapp.login;
import com.example.posapp.products.CakeListAdapter;
import com.example.posapp.products.prodClickListener;
import com.example.posapp.products.prodDrinksListAdapter;
import com.example.posapp.products.prodFoodListAdapter;
import com.example.posapp.products.prodItems;
import com.example.posapp.products.SpecialListAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OrderingSystem extends AppCompatActivity implements prodClickListener {
    Button btnAdd, Orders;
    Button btnLogout;
    TextView txtView;
    ImageView prodImg;
    EditText Quantity, Price, totalPriceUp, prodName;
    prodDrinksListAdapter productListAdapter;
    prodFoodListAdapter foodListAdapter;
    SpecialListAdapter specialListAdapter;
    CakeListAdapter CakeListAdapter;
    List<prodItems> items = new ArrayList<>();
    List<prodItems> foods = new ArrayList<>();
    List<prodItems> Cakes = new ArrayList<>();
    List<prodItems> Special = new ArrayList<>();
    SQLiteDatabase db;
    String currentID, itemCategory;

    private DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_system);
        db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        btnAdd = findViewById(R.id.btnCart);
        btnLogout = findViewById(R.id.btnLogout);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        prodName = findViewById(R.id.prodName);
        txtView = findViewById(R.id.txtView);
        totalPriceUp = findViewById(R.id.txtTotalPrice);
        Orders = findViewById(R.id.btnOrders);
        prodImg = findViewById(R.id.prodImg);

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

        refreshList();
        change();
    }

    @SuppressLint("Range")
    public void add() {
        if (prodName.getText().toString().trim().equals("")) {//Check if ProdName is Blank
            Toast.makeText(this, "Please Select a Product", Toast.LENGTH_LONG).show();
        } else if (Quantity.getText().toString().trim().equals("")) {//check quantity if blank
            Toast.makeText(this, "Please Input a Valid Quantity", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(String.valueOf(Quantity.getText())) <= 0) {//check if more than 0
            Toast.makeText(this, "Please Input Quantity More Than 0", Toast.LENGTH_LONG).show();
        } else
            try {
                total();
                String tPrice1 = totalPriceUp.getText().toString().trim();
                String qty2 = Quantity.getText().toString().trim();
                String prodName1 = prodName.getText().toString().trim();
                boolean repeat = false;

                    db.execSQL("CREATE TABLE IF NOT EXISTS cartlist(id INTEGER PRIMARY KEY, prodName VARCHAR, quantity INTEGER, category VARCHAR, price DOUBLE)");
                    Cursor cursor = db.rawQuery("SELECT * FROM cartlist WHERE id=?", new String[]{currentID});//Check if it is already added in cartlist
                    if (cursor.getCount() > 0) {
                        repeat = true;
                    }

                    String checkQuantityQuery = "SELECT quantity FROM products WHERE id = ?";
                    cursor = db.rawQuery(checkQuantityQuery, new String[]{currentID});
                    if (cursor.moveToFirst()) {//Get qty from products
                        int availableQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                        int requestedQuantity = Integer.parseInt(qty2);

                        if (repeat == true) {//Check if it already exists
                            checkQuantityQuery = "SELECT quantity FROM cartlist WHERE id = ?";
                            cursor = db.rawQuery(checkQuantityQuery, new String[]{currentID});

                            if (cursor.moveToFirst()) {//If exists, add the req qty and curr qty from products
                                availableQuantity = availableQuantity + cursor.getInt(cursor.getColumnIndex("quantity"));
                                if (requestedQuantity <= availableQuantity) {//If less, go
                                    updateCartItemAndReduceProductQuantity(currentID, qty2, tPrice1);
                                    prodName.setText("");
                                    Quantity.setText("");
                                    totalPriceUp.setText("");
                                    Price.setText("");
                                    prodImg.setImageResource(R.drawable.noimage);
                                } else {
                                    Toast.makeText(this, "Insufficient Stock", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else if (requestedQuantity <= availableQuantity) {
                            // Insert a new item if not yet in cartlist
                            String sql = "INSERT INTO cartlist (id, prodName, quantity, category, price) VALUES (?, ?, ?, ?, ?)";
                            SQLiteStatement statement = db.compileStatement(sql);
                            statement.bindString(1, currentID);
                            statement.bindString(2, prodName1);
                            statement.bindString(3, String.valueOf(Integer.parseInt(qty2)));
                            statement.bindString(4, itemCategory);
                            statement.bindString(5, String.valueOf(Double.parseDouble(tPrice1)));
                            statement.execute();
                            decreaseProductQuantity(Integer.parseInt(currentID), Integer.parseInt(qty2));
                            Toast.makeText(this, prodName.getText().toString() + " Added to Cart", Toast.LENGTH_LONG).show();
                            refreshList();
                            prodName.setText("");
                            Quantity.setText("");
                            totalPriceUp.setText("");
                            Price.setText("");
                            prodImg.setImageResource(R.drawable.noimage);
                            cursor.close();
                        }else{
                            Toast.makeText(this, "Insufficient Stock for " + prodName.getText().toString(), Toast.LENGTH_LONG).show();
                            prodName.setText("");
                            Quantity.setText("");
                            totalPriceUp.setText("");
                            Price.setText("");
                            prodImg.setImageResource(R.drawable.noimage);
                        }
                    }
            } catch (Exception e) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
            }
    }

    public void decreaseProductQuantity(int currentID, int quantityToSubtract) {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            String updateQuery = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            Object[] bindArgs = { quantityToSubtract, currentID};

            db.execSQL(updateQuery, bindArgs);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public void updateCartItemAndReduceProductQuantity(String currentID, String newQuantity, String tPrice1) {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            // Step 1: Retrieve current quantity from the 'cartlist'
            String getCurrentQuantityQuery = "SELECT quantity FROM cartlist WHERE id=?";
            Cursor cursor = db.rawQuery(getCurrentQuantityQuery, new String[]{currentID});

            int currentQuantity = 0;
            if (cursor.moveToFirst()) {
                currentQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            }
            cursor.close();

            int quantityDifference = Integer.parseInt(newQuantity) - currentQuantity;

            String updateCartlistQuery = "UPDATE cartlist SET quantity=?, price=? WHERE id=?";
            Object[] cartlistBindArgs = {newQuantity, tPrice1, currentID};
            db.execSQL(updateCartlistQuery, cartlistBindArgs);

            String updateProductsQuery = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            Object[] productsBindArgs = {quantityDifference, currentID};
            db.execSQL(updateProductsQuery, productsBindArgs);

            Toast.makeText(this, "Item Updated in Cart", Toast.LENGTH_LONG).show();

            refreshList();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void updatePrice() {
        Double total;
        final Cursor c = db.rawQuery("SELECT * FROM products WHERE id ='" + currentID + "'", null);

        if (c.moveToFirst()) {
            int prodPriceIndex = c.getColumnIndex("prodPrice");
            int productPrice = c.getInt(prodPriceIndex);
            total = Double.valueOf(productPrice);
            Price.setText(String.valueOf(total) + "0");
        } else {
            Price.setText("");
        }
        c.close();
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
        addOnsRecycle.setLayoutManager(new LinearLayoutManager(OrderingSystem.this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView othersRecyclerView = findViewById(R.id.recycleSpecial);
        othersRecyclerView.setHasFixedSize(true);
        othersRecyclerView.setLayoutManager(new LinearLayoutManager(OrderingSystem.this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, quantity INTEGER, prodPrice DOUBLE, prodImage BLOB)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("SELECT * FROM products ORDER BY product ASC", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Products Found", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int product = c.getColumnIndex("product");
            int category = c.getColumnIndex("category");
            int prodPrice = c.getColumnIndex("prodPrice");
            int quantity = c.getColumnIndex("quantity");
            int prodImage = c.getColumnIndex("prodImage");

            if(c.moveToFirst()){
                do{
                    if (c.getString(category).equals("Drinks")) {
                        items.add(new prodItems(c.getString(id), c.getString(product), c.getString(category), c.getString(prodPrice), c.getString(quantity), c.getBlob(prodImage)));
                        productListAdapter = new prodDrinksListAdapter(this, items, this);
                        recyclerView.setAdapter(productListAdapter);
                    }
                    if (c.getString(category).equals("Food")) {
                        foods.add(new prodItems(c.getString(id), c.getString(product), c.getString(category), c.getString(prodPrice), c.getString(quantity), c.getBlob(prodImage)));
                        foodListAdapter = new prodFoodListAdapter(this, foods, this);
                        foodRecyclerView.setAdapter(foodListAdapter);
                    }
                    if (c.getString(category).equals("Cake")){
                        Cakes.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity), c.getBlob(prodImage)));
                        CakeListAdapter = new CakeListAdapter(this, Cakes, this);
                        addOnsRecycle.setAdapter(CakeListAdapter);}
                    if (c.getString(category).equals("Special")){
                        Special.add(new prodItems(c.getString(id),c.getString(product),c.getString(category),c.getString(prodPrice),c.getString(quantity), c.getBlob(prodImage)));
                        specialListAdapter = new SpecialListAdapter(this, Special, this);
                        othersRecyclerView.setAdapter(specialListAdapter);}
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
                    if(prodName.getText().equals("")){

                    }else if(Price.getText().equals("")) {
                        refreshList();
                    } else if(s.length() != 0) {
                        refreshList();
                        total();
                    } else {
                        refreshList();
                        totalPriceUp.setText("");
                    }
                }catch (Exception e){
                    //blank
                }
            }
        });
    }

    @Override
    public void onItemClicked(prodItems view) {
        currentID = view.getId();
        prodName.setText(view.getProduct());
        itemCategory = view.getCategory();
        if(view.getProdImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(view.getProdImage(), 0, view.getProdImage().length);
            prodImg.setImageBitmap(bitmap);
        }else{
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            defaultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            prodImg.setImageBitmap(defaultBitmap);
        }
        updatePrice();
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
                        // on below line we are setting a click listener
                        // for our positive button
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
            builder.setMessage("Products have not been added. Do you want to proceed in Cart menu?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }
}