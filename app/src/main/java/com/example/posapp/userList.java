package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class userList extends AppCompatActivity implements userClickListener {

    Button btnBack, btnAddUser;
    adminAdapter adminAdapter;
    userAdapter userAdapter;
    List<userItems> admins = new ArrayList<>();
    List<userItems> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        btnBack = findViewById(R.id.btnBack);
        btnAddUser = findViewById(R.id.btnAddUser);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(userList.this,MainScreen.class);
                startActivity(i);
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() { //Add User
            @Override
            public void onClick(View view) {
                Intent i = new Intent(userList.this, userAdd.class);
                startActivity(i);
            }
        });

        refreshList();

    }

    public void refreshList(){
        RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from users", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Users Found", Toast.LENGTH_LONG).show();
        }else{
            int id = c.getColumnIndex("id");
            int fullName = c.getColumnIndex("fullName");
            int userName = c.getColumnIndex("userName");
            int password = c.getColumnIndex("password");
            int access = c.getColumnIndex("access");

            if(c.moveToFirst()) {
                do {
                    if (c.getString(access).equals("Admin")) {
                        admins.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access)));
                        adminAdapter = new adminAdapter(this, admins, this);
                        adminRecyclerView.setAdapter(adminAdapter);
                    } else {
                        users.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access)));
                        userAdapter = new userAdapter(this, users, this);
                        userRecyclerView.setAdapter(userAdapter);
                    }
                }while (c.moveToNext()) ;
            }
            c.close();
            db.close();
        }
    }

    @Override
    public void onItemClicked(userItems view) {
        Intent i = new Intent(getApplicationContext(), userEdit.class);
        i.putExtra("id", view.getId());
        i.putExtra("fullName", view.getFullName());
        i.putExtra("userName", view.getUserName());
        i.putExtra("password", view.getPassword());
        i.putExtra("access", view.getAccess());
        startActivity(i);
    }
}