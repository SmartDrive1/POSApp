package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.ScrollCaptureSession;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class OrderingSystem extends AppCompatActivity {
    Button btnAdd, btnBack, Total, Orders;
    EditText Quantity, Price, totalPriceUp;
    Spinner sp1;
    ArrayList<String> getP = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_system);


        btnAdd = findViewById(R.id.btnCart);
        btnBack = findViewById(R.id.btnCancel);
        Quantity = findViewById(R.id.txtQty);
        Price = findViewById(R.id.txtPrice);
        sp1 = findViewById(R.id.prodName);
        totalPriceUp = findViewById(R.id.txtTotalPrice);
        Total = findViewById(R.id.btnTotal);
        Orders = findViewById(R.id.btnOrders);

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

                Toast.makeText(OrderingSystem.this,"Order Added", Toast.LENGTH_LONG).show();
            }
        });

        Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderingSystem.this, cart.class);
                startActivity(i);
            }
        });

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getProducts();

        Total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    public void getProducts() {
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )");//in case there are no tables yet
        final Cursor c = db.rawQuery("select * from products", null);
        int product = c.getColumnIndex("product");

        getP.clear();
        arrayAdapter = new ArrayAdapter(this, R.layout.spinnerlayout, getP);
        sp1.setAdapter(arrayAdapter);

        final ArrayList<cProd> prods = new ArrayList<cProd>();
        if (c.moveToFirst()) {
            do {
                cProd pr = new cProd();
                pr.product = c.getString(product);
                prods.add(pr);
                getP.add(c.getString(product));

            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void add() {
        double qty1 = Double.parseDouble(Quantity.getText().toString());
        double price1 = Double.parseDouble(Price.getText().toString());
        double totalPrice = qty1 * price1;

        totalPriceUp.setText(String.valueOf(Double.valueOf(totalPrice)));
        transacProducts[] orderProducts = {new transacProducts(sp1.getSelectedItem().toString(), Double.valueOf(String.valueOf(totalPriceUp.getText())), Integer.valueOf(String.valueOf(Quantity.getText())))};
        TmpContainer container = new TmpContainer(orderProducts);
        cCurrentTransac.setCurrentTransaction(container);
    }

    public void updatePrice() {
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY AUTOINCREMENT,product VARCHAR, category VARCHAR, prodPrice INTEGER )");//incase there are no tables yet
        final Cursor c = db.rawQuery("SELECT * FROM products WHERE product ='" + sp1.getSelectedItem().toString() + "'", null);

        if (c.moveToFirst()) {
            int prodPriceIndex = c.getColumnIndex("prodPrice");
            int productPrice = c.getInt(prodPriceIndex);
            Price.setText(String.valueOf(productPrice));
        } else {
            Price.setText("N/A");
        }

        c.close();
    }
}
