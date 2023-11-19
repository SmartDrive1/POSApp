package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AaUriButton extends AppCompatActivity {

    Button btnClearUsers, btnClearProducts, btnClearInventory, btnBack, btnClearOrders, clearTrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aa_uri_button);

        btnClearInventory = findViewById(R.id.btnClearInventory);
        btnClearUsers = findViewById(R.id.btnClearUsers);
        btnClearProducts = findViewById(R.id.btnClearProducts);
        btnBack = findViewById(R.id.btnBack);
        btnClearOrders = findViewById(R.id.ClearOrders);
        clearTrans = findViewById(R.id.clearTrans);

        clearTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table transactions";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
                db.close();
            }
        });

        btnClearInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table inventory";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
                db.close();
            }
        });

        btnClearProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

                String sql = "drop table products";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();

                db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product TEXT, category VARCHAR, quantity INTEGER, prodPrice INTEGER )"); //Create database if non-existent, to avoid crash

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
                manage.put("product", "Manager Breakfast");
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
        });

        btnClearOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table cartlist";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
                db.close();
            }
        });

        btnClearUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "drop table users";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.execute();
                db.close();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AaUriButton.this, MainScreen.class);
                startActivity(i);
            }
        });
    }

}