package com.example.posapp.users;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.posapp.R;
import com.example.posapp.mainManageScreen;

import java.util.ArrayList;
import java.util.List;

public class userList extends AppCompatActivity implements userClickListener {

    Button btnBack, btnAddUser;
    EditText txtSearch;
    com.example.posapp.users.adminAdapter adminAdapter;
    userAdapter userAdapter;
    List<userItems> admins = new ArrayList<>();
    List<userItems> users = new ArrayList<>();
    ImageView rightIndicator1, leftIndicator1, rightIndicator2, leftIndicator2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        btnBack = findViewById(R.id.btnBack);
        btnAddUser = findViewById(R.id.btnAddUser);
        txtSearch = findViewById(R.id.txtSearch);
        rightIndicator1 = findViewById(R.id.rightIndicator1);
        leftIndicator1 = findViewById(R.id.leftIndicator1);
        rightIndicator2 = findViewById(R.id.rightIndicator2);
        leftIndicator2 = findViewById(R.id.leftIndicator2);

        RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(userList.this, mainManageScreen.class);
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

        adminRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator1.setVisibility(View.GONE);
                    leftIndicator1.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator1.setVisibility(View.GONE);
                        leftIndicator1.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator1.setVisibility(View.VISIBLE);
                        leftIndicator1.setVisibility(View.GONE);
                    }
                }
            }
        });

        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();

                if (itemCount <= visibleItemCount) {
                    rightIndicator2.setVisibility(View.GONE);
                    leftIndicator2.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    int itemsPerPage = 1;

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        rightIndicator2.setVisibility(View.GONE);
                        leftIndicator2.setVisibility(View.VISIBLE);
                    } else {
                        rightIndicator2.setVisibility(View.VISIBLE);
                        leftIndicator2.setVisibility(View.GONE);
                    }
                }
            }
        });

        refreshList();
        search();
    }

    public void refreshList(){
        RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR, userImg BLOB)"); //Create database if non-existent, to avoid crash
        final Cursor c = db.rawQuery("SELECT * FROM users ORDER BY id ASC", null);
        int count = c.getCount();

        if(count == 0){
            Toast.makeText(this,"No Users Found", Toast.LENGTH_LONG).show();
            clearTable();
        }else{
            int id = c.getColumnIndex("id");
            int fullName = c.getColumnIndex("fullName");
            int userName = c.getColumnIndex("userName");
            int password = c.getColumnIndex("password");
            int access = c.getColumnIndex("access");
            int userImg = c.getColumnIndex("userImg");

            if(c.moveToFirst()) {
                do {
                    if (c.getString(access).equals("Admin")) {
                        admins.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access), c.getBlob(userImg)));
                        adminAdapter = new adminAdapter(this, admins, this);
                        adminRecyclerView.setAdapter(adminAdapter);
                    } else {
                        users.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access), c.getBlob(userImg)));
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
        if(view.getUserImg() != null){
            i.putExtra("userImg", view.getUserImg());
        }else{
            //None
        }
        startActivity(i);
    }

    public void search(){
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtSearch.getText().equals("")){
                    refreshList();
                }else{
                    clearTable();
                    String searchUser = "";
                    searchUser = txtSearch.getText().toString().trim();
                    RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
                    adminRecyclerView.setHasFixedSize(true);
                    adminRecyclerView.setLayoutManager(new LinearLayoutManager(userList.this,LinearLayoutManager.HORIZONTAL, false));

                    RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
                    userRecyclerView.setHasFixedSize(true);
                    userRecyclerView.setLayoutManager(new LinearLayoutManager(userList.this,LinearLayoutManager.HORIZONTAL, false));

                    SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR, userImg BLOB)"); //Create database if non-existent, to avoid crash
                    String query = "SELECT * FROM users WHERE id = ? OR fullname LIKE ? OR username LIKE ?";
                    String[] selectionArgs = {searchUser, "%" + searchUser + "%", "%" + searchUser + "%"};

                    Cursor c = db.rawQuery(query, selectionArgs);
                    count = c.getCount();

                    if(count == 0){
                        Toast.makeText(userList.this,"No Users Found", Toast.LENGTH_LONG).show();
                    }else{
                        int id = c.getColumnIndex("id");
                        int fullName = c.getColumnIndex("fullName");
                        int userName = c.getColumnIndex("userName");
                        int password = c.getColumnIndex("password");
                        int access = c.getColumnIndex("access");
                        int userImg = c.getColumnIndex("userImg");

                        if(c.moveToFirst()) {
                            do {
                                if (c.getString(access).equals("Admin")) {
                                    admins.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access), c.getBlob(userImg)));
                                    adminAdapter = new adminAdapter(userList.this, admins, adminAdapter.mClickListener);
                                    adminRecyclerView.setAdapter(adminAdapter);
                                } else {
                                    users.add(new userItems(c.getString(id), c.getString(fullName), c.getString(userName), c.getString(password), c.getString(access), c.getBlob(userImg)));
                                    userAdapter = new userAdapter(userList.this, users, userAdapter.mClickListener);
                                    userRecyclerView.setAdapter(userAdapter);
                                }
                            }while (c.moveToNext());
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

    public void clearTable(){
        admins.clear();
        users.clear();
    }

    @Override
    public void onBackPressed() {
    }
}