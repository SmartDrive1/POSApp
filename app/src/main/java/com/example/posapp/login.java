package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.OrderingSystem.accessValue;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class login extends AppCompatActivity {

    EditText txtUser, txtPass;
    Button btnLogin, btnForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgetPass = findViewById(R.id.btnForgetPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessValue.access = "Admin";
                Intent i = new Intent(login.this, forgetPassword.class);
                startActivity(i);
            }
        });

        // Check if it's the first run
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstRun = preferences.getBoolean("firstRun", true);

        if (firstRun) {
            // Drop and recreate the table
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("DROP TABLE IF EXISTS products");
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS cartlist");
            db.execSQL("DROP TABLE IF EXISTS transactions");
            db.execSQL("DROP TABLE IF EXISTS orders");

            db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY, product VARCHAR, category VARCHAR, quantity INTEGER, prodPrice INTEGER, prodImage BLOB)");
            db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR, userImg BLOB)"); //Create database if non-existent, to avoid crash
            db.execSQL("CREATE TABLE cartlist(id INTEGER PRIMARY KEY, prodName VARCHAR,quantity INTEGER, category VARCHAR, price DOUBLE)"); //Create database if non-existent, to avoid crash
            db.execSQL("CREATE TABLE transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER)");
            db.execSQL("CREATE TABLE orders(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER, status VARCHAR)");

            addProducts();
            addUsers();
            addTransaction();
            // Set the firstRun flag to false
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

            Random random = new Random();
            for (int i = 0; i < 50; i++) {
                int transID = i;
                int randomProductIndex = random.nextInt(18) + 1; // Random product ID between 1 and 18
                String randomProdName = getProductById(db, randomProductIndex);
                int randomQuantity = random.nextInt(50) + 1; // Random quantity between 1 and 50
                double randomPrice = getProductPriceById(db, randomProductIndex);
                long x = randomTime();

                ContentValues transactionValues = new ContentValues();
                transactionValues.put("transID", transID);
                transactionValues.put("prodName", randomProdName);
                transactionValues.put("quantity", randomQuantity);
                transactionValues.put("price", randomPrice*randomQuantity);
                transactionValues.put("category", getCategoryByProduct(db, randomProdName));
                transactionValues.put("time", x);

                db.insert("transactions", null, transactionValues);
            }

            db.close();
        }
    }

    public void Login(){
        String username = txtUser.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, fullName VARCHAR, userName VARCHAR, password VARCHAR, access, VARCHAR)");//incase there are no tables yet
        Cursor c = db.rawQuery("select * from users WHERE userName='" + username + "'and password='" + pass + "'", null);

        if (username.equals("")){
            Toast.makeText(login.this, "Username is Blank", Toast.LENGTH_SHORT).show();
        }else if(pass.equals("")){
            Toast.makeText(login.this, "Password is Blank", Toast.LENGTH_SHORT).show();
        }else if(username.equals("admin") && pass.equals("admin")){ //Edit before deployment
            accessValue.access = "Admin";
            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(login.this, MainScreen.class);
            startActivity(i);
        }else if (c.moveToFirst()){
            int accessIndex = c.getColumnIndex("access");
            accessValue.access = c.getString(accessIndex);
            String currentAccess = accessValue.access;
            if(currentAccess.equals("Admin")){
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(login.this, MainScreen.class);
                startActivity(i);
            }else{
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(login.this, OrderingSystem.class);
                startActivity(i);
            }

        }else
        {
            Toast.makeText(login.this, "Wrong Login Credentials. Please Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addProducts(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        // Insert Espresso
        ContentValues espressoValues = new ContentValues();
        espressoValues.put("id", 1);
        espressoValues.put("product", "Espresso");
        espressoValues.put("category", "Drinks");
        espressoValues.put("quantity", 21);
        espressoValues.put("prodPrice", 100);
        db.insert("products", null, espressoValues);

        ContentValues mochaccino = new ContentValues();
        mochaccino.put("id", 2);
        mochaccino.put("product", "Mochaccino");
        mochaccino.put("category", "Drinks");
        mochaccino.put("quantity", 12);
        mochaccino.put("prodPrice", 100);
        db.insert("products", null, mochaccino);

        ContentValues cafelatte = new ContentValues();
        cafelatte.put("id", 3);
        cafelatte.put("product", "Cafe Latte");
        cafelatte.put("category", "Drinks");
        cafelatte.put("quantity", 65);
        cafelatte.put("prodPrice", 100);
        db.insert("products", null, cafelatte);

        ContentValues capuccino = new ContentValues();
        capuccino.put("id", 4);
        capuccino.put("product", "Capuccino");
        capuccino.put("category", "Drinks");
        capuccino.put("quantity", 76);
        capuccino.put("prodPrice", 100);
        db.insert("products", null, capuccino);

        ContentValues Caramel = new ContentValues();
        Caramel.put("id", 5);
        Caramel.put("product", "Caramel Macchiato");
        Caramel.put("category", "Drinks");
        Caramel.put("quantity", 7);
        Caramel.put("prodPrice", 100);
        db.insert("products", null, Caramel);

        ContentValues corn = new ContentValues();
        corn.put("id", 6);
        corn.put("product", "Corned Beef");
        corn.put("category", "Food");
        corn.put("quantity", 32);
        corn.put("prodPrice", 120);
        db.insert("products", null, corn);

        ContentValues danggit = new ContentValues();
        danggit.put("id", 7);
        danggit.put("product", "Danggit");
        danggit.put("category", "Food");
        danggit.put("quantity", 2);
        danggit.put("prodPrice", 120);
        db.insert("products", null, danggit);

        ContentValues hung = new ContentValues();
        hung.put("id", 8);
        hung.put("product", "Hungarian Sausage");
        hung.put("category", "Food");
        hung.put("quantity", 32);
        hung.put("prodPrice", 120);
        db.insert("products", null, hung);

        ContentValues Spam = new ContentValues();
        Spam.put("id", 9);
        Spam.put("product", "Spam");
        Spam.put("category", "Food");
        Spam.put("quantity", 120);
        Spam.put("prodPrice", 120);
        db.insert("products", null, Spam);

        ContentValues tocino = new ContentValues();
        tocino.put("id", 10);
        tocino.put("product", "Tocino");
        tocino.put("category", "Food");
        tocino.put("quantity", 120);
        tocino.put("prodPrice", 120);
        db.insert("products", null, tocino);

        ContentValues Special = new ContentValues();
        Special.put("id", 11);
        Special.put("product", "Special Tapa");
        Special.put("category", "Food");
        Special.put("quantity", 90);
        Special.put("prodPrice", 120);
        db.insert("products", null, Special);

        ContentValues manage = new ContentValues();
        manage.put("id", 12);
        manage.put("product", "Manager's Breakfast");
        manage.put("category", "Food");
        manage.put("quantity", 47);
        manage.put("prodPrice", 180);
        db.insert("products", null, manage);

        ContentValues cupcake = new ContentValues();
        cupcake.put("id", 13);
        cupcake.put("product", "Cupcake");
        cupcake.put("category", "Cake");
        cupcake.put("quantity", 89);
        cupcake.put("prodPrice", 40);
        db.insert("products", null, cupcake);

        ContentValues blue = new ContentValues();
        blue.put("id", 14);
        blue.put("product", "Blueberry Cheesecake");
        blue.put("category", "Cake");
        blue.put("quantity", 150);
        blue.put("prodPrice", 150);
        db.insert("products", null, blue);

        ContentValues straw = new ContentValues();
        straw.put("id", 15);
        straw.put("product", "Strawberry Cheesecake");
        straw.put("category", "Cake");
        straw.put("quantity", 48);
        straw.put("prodPrice", 150);
        db.insert("products", null, straw);

        ContentValues sisig = new ContentValues();
        sisig.put("id", 16);
        sisig.put("product", "Sisig");
        sisig.put("category", "Special");
        sisig.put("quantity", 100);
        sisig.put("prodPrice", 150);
        db.insert("products", null, sisig);

        ContentValues sauce = new ContentValues();
        sauce.put("id", 17);
        sauce.put("product", "Saucy Back Ribs");
        sauce.put("category", "Special");
        sauce.put("quantity", 87);
        sauce.put("prodPrice", 200);
        db.insert("products", null, sauce);

        ContentValues dinakdakan = new ContentValues();
        dinakdakan.put("id", 18);
        dinakdakan.put("product", "Dinakdakan");
        dinakdakan.put("category", "Special");
        dinakdakan.put("quantity", 780);
        dinakdakan.put("prodPrice", 150);
        db.insert("products", null, dinakdakan);

        db.close();
    }

    public void addUsers(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        ContentValues yeriel = new ContentValues();
        yeriel.put("id", 1);
        yeriel.put("fullname", "yeriel");
        yeriel.put("userName", "yeriel");
        yeriel.put("password", "yeriel");
        yeriel.put("access", "Admin");
        db.insert("users", null, yeriel);

        ContentValues user1 = new ContentValues();
        user1.put("id", 2);
        user1.put("fullname", "user1");
        user1.put("userName", "user1");
        user1.put("password", "user1");
        user1.put("access", "User");
        db.insert("users", null, user1);
    }

    public void addTransaction(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        ContentValues trans1 = new ContentValues();
        trans1.put("transID", 2); // Assuming transID is the primary key and auto-incremented
        trans1.put("prodName", "Dinakdakan");
        trans1.put("category", "Special");
        trans1.put("quantity", 780);
        trans1.put("price", 150.0); // Use the correct column name for price
        trans1.put("time", System.currentTimeMillis()); // Use the current time as an example
        db.insert("transactions", null, trans1);

        ContentValues trans2 = new ContentValues();
        trans2.put("transID", 2); // Assuming transID is the primary key and auto-incremented
        trans2.put("prodName", "Blueberry Cheesecake");
        trans2.put("category", "Cake");
        trans2.put("quantity", 150);
        trans2.put("price", 150.0); // Use the correct column name for price
        trans2.put("time", System.currentTimeMillis()); // Use the current time as an example
        db.insert("transactions", null, trans2);
    }

    // Helper method to get product name by ID
    @SuppressLint("Range")
    private String getProductById(SQLiteDatabase db, int productId) {
        String query = "SELECT product FROM products WHERE id = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        String productName = "";
        if (cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndex("product"));
        }
        cursor.close();
        return productName;
    }

    // Helper method to get product price by ID
    @SuppressLint("Range")
    private double getProductPriceById(SQLiteDatabase db, int productId) {
        String query = "SELECT prodPrice FROM products WHERE id = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        double productPrice = 0;
        if (cursor.moveToFirst()) {
            productPrice = cursor.getDouble(cursor.getColumnIndex("prodPrice"));
        }
        cursor.close();
        return productPrice;
    }

    // Helper method to get category by product name
    @SuppressLint("Range")
    private String getCategoryByProduct(SQLiteDatabase db, String productName) {
        String query = "SELECT category FROM products WHERE product = ?";
        String[] selectionArgs = {productName};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        String category = "";
        if (cursor.moveToFirst()) {
            category = cursor.getString(cursor.getColumnIndex("category"));
        }
        cursor.close();
        return category;
    }

    public long randomTime(){
        long x, startTime, startTime0, currentTime24;

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        startTime0 = calendar.getTimeInMillis();
        System.out.println("Current date and time as int (set to 00:00:00): " + startTime0);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        currentTime24 = calendar.getTimeInMillis();
        System.out.println("Current date and time as int (set to 23:59:59): " + currentTime24);

        startTime = startTime0 - 518400000;

        x = generateRandomLong(startTime, currentTime24);
        return x;
    }

    private static long generateRandomLong(long min, long max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        Random random = new Random();
        return min + (long) (random.nextDouble() * (max - min));
    }

}