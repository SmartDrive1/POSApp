package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;

public class userList extends AppCompatActivity {

    ListView lstUsers;
    Button btnBack, btnAddUser;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        btnBack = findViewById(R.id.btnBack);
        btnAddUser = findViewById(R.id.btnAddUser);
        lstUsers = findViewById(R.id.lstUsers);

        btnBack.setOnClickListener(new View.OnClickListener() { //Back
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

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("select * from users", null);
        int id = c.getColumnIndex("id");
        int fullName = c.getColumnIndex("fullName");
        int userName = c.getColumnIndex("userName");
        int password = c.getColumnIndex("password");
        int access = c.getColumnIndex("access");

        titles.clear();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,titles);
        lstUsers.setAdapter(arrayAdapter);

        final ArrayList<cUsers> userL = new ArrayList<cUsers>();
        if(c.moveToFirst())
        {
            do{
                cUsers ul = new cUsers();
                ul.id = c.getString(id);
                ul.fullName = c.getString(fullName);
                ul.userName = c.getString(userName);
                ul.password = c.getString(password);
                ul.access = c.getString(access);
                userL.add(ul);

                titles.add(c.getString(id) + "\t\t\t\t\t\t\t\t\t\t\t" + c.getString(fullName) + "\t\t\t\t\t\t\t"
                        + c.getString(userName) + "\t\t\t\t\t\t\t" + c.getString(password) + "\t\t\t\t\t\t\t" + c.getString(access));

            }while(c.moveToNext());
            c.close();
            db.close();
            arrayAdapter.notifyDataSetChanged();
            lstUsers.invalidateViews();
        }

        lstUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                cUsers ul = userL.get((position));
                Intent i = new Intent(getApplicationContext(), userEdit.class);
                i.putExtra("id",ul.id);
                i.putExtra("fullName",ul.fullName);
                i.putExtra("userName",ul.userName);
                i.putExtra("password",ul.password);
                i.putExtra("access",ul.access);
                startActivity(i);
            }
        });
    }
    }