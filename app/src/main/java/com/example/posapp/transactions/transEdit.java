package com.example.posapp.transactions;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class transEdit extends AppCompatActivity {
    Button btnBack, btnDelete;
    TextView txtTotalPrice, txtTransID, txtDateTime;
    transEditAdapter transEditAdapter;
    List<transEditItems> items = new ArrayList<>();
    String transID;
    double sum = 0.0;
    String formattedDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

    private DialogInterface.OnClickListener dialogClickListener;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_edit);

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        txtTransID = findViewById(R.id.txtTransID);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtDateTime = findViewById(R.id.txtDateTime);

        Intent i = getIntent();
        transID = i.getStringExtra("id".toString());

        txtTransID.setText("Transaction ID: " + String.valueOf(transID));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER)");

        String[] columns = {"SUM(price) AS totalAmount, time"};
        String selection = "transID=?";
        String[] selectionArgs = {String.valueOf(transID)};

        Cursor cursor = db.query("transactions", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int time = cursor.getColumnIndex("time");
            formattedDate = dateFormat.format(new Date(cursor.getLong(time)));
            sum = cursor.getDouble(cursor.getColumnIndex("totalAmount"));
            cursor.close();
        }

        txtTotalPrice.setText("Total: " + String.valueOf(sum) + "0");
        txtDateTime.setText("Date/Time:\n" + formattedDate);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.close();
                Intent i = new Intent(transEdit.this, manageTransactions.class);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        refreshList();
    }

    public void refreshList(){
        RecyclerView recyclerView = findViewById(R.id.recycleTransEdit);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        String query = "SELECT * FROM transactions WHERE transid= ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(transID)});

        int prodName = cursor.getColumnIndex("prodName");
        int quantity = cursor.getColumnIndex("quantity");
        int price = cursor.getColumnIndex("price");
        int category = cursor.getColumnIndex("category");

        while(cursor.moveToNext()) {
            items.add(new transEditItems(cursor.getString(prodName), cursor.getString(quantity), cursor.getString(category), cursor.getString(price)));
            transEditAdapter = new transEditAdapter(this, items);
            recyclerView.setAdapter(transEditAdapter);
        }
    }

    public void delete() {
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // on below line we are setting a click listener
                    // for our positive button
                    case DialogInterface.BUTTON_POSITIVE:
                        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                        db.delete("transactions", "transid=?", new String[] { String.valueOf(transID) });
                        db.close();
                        Toast.makeText(transEdit.this, "Transaction: " + transID + " Successfully Deleted", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(transEdit.this, manageTransactions.class);
                        startActivity(i);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(transEdit.this);
        builder.setMessage("Do You Want To Delete Transaction " + transID + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
