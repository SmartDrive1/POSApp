package com.example.posapp.inventory;

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

import com.example.posapp.MainScreen;
import com.example.posapp.R;
import com.example.posapp.users.adminAdapter;
import com.example.posapp.users.userAdapter;
import com.example.posapp.users.userItems;
import com.example.posapp.users.userList;

import java.util.ArrayList;
import java.util.List;
import com.example.posapp.mainManageScreen;

public class invList extends AppCompatActivity implements invClickListener {

    Button btnBack, btnAddItem;
    TextView txtSearch;
    invListAdapter invListAdapter;
    List<invItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_list);

        btnBack = findViewById(R.id.btnBack);
        btnAddItem = findViewById(R.id.btnAddItem);
        txtSearch = findViewById(R.id.txtSearch);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this, mainManageScreen.class);
                startActivity(i);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(invList.this, invAdd.class);
                startActivity(i);
            }
        });

        search();
        refreshList();
    }

    public void refreshList(){
        RecyclerView invRecycleView = findViewById(R.id.recycleInv);
        invRecycleView.setHasFixedSize(true);
        invRecycleView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY, itemName VARCHAR, stock INTEGER )"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from inventory", null);
        int count = c.getCount();

        if(count == 0){
            c.close();
            db.close();
            Toast.makeText(this,"No Items Found", Toast.LENGTH_LONG).show();
        }else {
            int id = c.getColumnIndex("id");
            int itemName = c.getColumnIndex("itemName");
            int stock = c.getColumnIndex("stock");

            if(c.moveToFirst()){
                do{
                    items.add(new invItems(c.getString(id), c.getString(itemName), c.getString(stock)));
                    invListAdapter = new invListAdapter(this, items, this);
                    invRecycleView.setAdapter(invListAdapter);
                }while (c.moveToNext());
            }
            c.close();
            db.close();
        }
    }

    @Override
    public void onItemClicked(invItems view) {
        Intent i = new Intent(getApplicationContext(), invEdit.class);
        i.putExtra("id", view.getItemID());
        i.putExtra("itemName", view.getItemName());
        i.putExtra("itemStock", view.getItemStock());
        startActivity(i);
    }

    public void search(){
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtSearch.getText().equals("")){
                    refreshList();
                }else{
                    items.clear();
                    String searchItem;
                    searchItem = txtSearch.getText().toString().trim();
                    RecyclerView invRecycleView = findViewById(R.id.recycleInv);
                    invRecycleView.setHasFixedSize(true);
                    invRecycleView.setLayoutManager(new LinearLayoutManager(invList.this, LinearLayoutManager.HORIZONTAL, false));

                    SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS inventory(id INTEGER PRIMARY KEY, itemName VARCHAR, stock INTEGER )"); //Create database if non-existent, to avoid crash
                    String query = "SELECT * FROM inventory WHERE id = ? OR itemName LIKE ?";
                    String[] selectionArgs = {searchItem, "%" + searchItem + "%"};

                    Cursor c = db.rawQuery(query, selectionArgs);
                    count = c.getCount();

                    if (count == 0) {
                        items.clear();
                        Toast.makeText(invList.this, "No Items Found", Toast.LENGTH_LONG).show();
                    } else {
                        int id = c.getColumnIndex("id");
                        int itemName = c.getColumnIndex("itemName");
                        int stock = c.getColumnIndex("stock");

                        if (c.moveToFirst()) {
                            do {
                                items.add(new invItems(c.getString(id), c.getString(itemName), c.getString(stock)));
                                invListAdapter = new invListAdapter(invList.this, items, invListAdapter.mClickListener);
                                invRecycleView.setAdapter(invListAdapter);
                            } while (c.moveToNext());
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
}