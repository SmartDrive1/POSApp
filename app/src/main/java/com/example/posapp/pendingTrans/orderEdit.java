package com.example.posapp.pendingTrans;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;
import com.example.posapp.transactions.manageTransactions;
import com.example.posapp.transactions.transEdit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class orderEdit extends AppCompatActivity {
    Button btnDone, btnCancel, btnBack;
    TextView txtOrderID, txtOrderTime, txtStatus;
    String transID;
    String formattedDate, status;
    private DialogInterface.OnClickListener dialogClickListener;
    List<orderEditItems> items = new ArrayList<>();
    editOrderAdapter editOrderAdapter;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_edit);

        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        txtOrderID = findViewById(R.id.txtOrderID);
        txtOrderTime = findViewById(R.id.txtOrderTime);
        txtStatus = findViewById(R.id.txtStatus);

        Intent i = getIntent();
        transID = i.getStringExtra("id".toString());

        txtOrderID.setText("Order ID: " + transID);

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS orders(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER, status VARCHAR)");

        String[] columns = {"status, time"};
        String selection = "transID=?";
        String[] selectionArgs = {String.valueOf(transID)};

        Cursor cursor = db.query("orders", columns, selection, selectionArgs, null, null, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (cursor != null && cursor.moveToFirst()) {
            int time = cursor.getColumnIndex("time");
            formattedDate = dateFormat.format(new Date(cursor.getLong(time)));
            status = cursor.getString(cursor.getColumnIndex("status"));
            cursor.close();
        }

        txtStatus.setText("Status: " + status);
        txtOrderTime.setText("Order Time:\n" + formattedDate);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDone();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(orderEdit.this, pendingTransaction.class);
                startActivity(i);
            }
        });

        refreshList();
    }

    public void refreshList(){
        RecyclerView recyclerView = findViewById(R.id.recycleOrderEdit);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        String query = "SELECT * FROM orders WHERE transid= ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(transID)});

        int prodName = cursor.getColumnIndex("prodName");
        int quantity = cursor.getColumnIndex("quantity");

        while(cursor.moveToNext()){
            items.add(new orderEditItems(cursor.getString(prodName), cursor.getString(quantity)));
            editOrderAdapter = new editOrderAdapter(this, items);
            recyclerView.setAdapter(editOrderAdapter);
        }
    }

    @SuppressLint("Range")
    public void orderDone(){
        int max_id = 0;

        try{
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT MAX(transID) from transactions", null);

            if(cursor.moveToNext()){
                max_id = cursor.getInt(0);
            }

            max_id += 1;

            cursor = db.rawQuery("SELECT * FROM orders", null);

            if(cursor.moveToNext()){
                do{
                    String prodName = cursor.getString(cursor.getColumnIndex("prodName"));
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String price = cursor.getString(cursor.getColumnIndex("price"));
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    long currentTime = cursor.getLong(cursor.getColumnIndex("time"));

                    String sql = "INSERT INTO transactions(transID, prodName, quantity, price, category, time)values(?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1, String.valueOf(max_id));
                    statement.bindString(2, prodName);
                    statement.bindString(3, quantity);
                    statement.bindString(4, price);
                    statement.bindString(5, category);
                    statement.bindString(6, String.valueOf(currentTime));
                    statement.execute();
                }while(cursor.moveToNext());
            }

            db.delete("orders", "transID=?", new String[] { String.valueOf(transID) });

            cursor.close();
            db.close();
            Toast.makeText(orderEdit.this, "Order Successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(orderEdit.this, pendingTransaction.class);
            startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancelOrder(){
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                        cancelOrderFunction();
                        db.delete("orders", "transID=?", new String[] { String.valueOf(transID) });
                        db.close();
                        Toast.makeText(orderEdit.this, "Order ID: " + transID + " Cancelled Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(orderEdit.this, pendingTransaction.class);
                        startActivity(i);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(orderEdit.this);
        builder.setMessage("Do You Want To Cancel Order " + transID + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @SuppressLint("Range")
    public void cancelOrderFunction(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);


        Cursor cursor = db.rawQuery("SELECT * FROM orders WHERE transID = " + transID, null);

        if(cursor.moveToNext()){
            do{
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String updateQuantity = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
                Object[] bindArgs = {quantity, id};
                db.execSQL(updateQuantity, bindArgs);
            }while(cursor.moveToNext());
        }
    }

    @Override
    public void onBackPressed() {
    }
}