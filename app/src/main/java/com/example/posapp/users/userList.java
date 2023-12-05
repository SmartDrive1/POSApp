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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.posapp.R;
import com.example.posapp.mainManageScreen;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        clearTable();
        RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
        adminRecyclerView.setHasFixedSize(true);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        usersCollection.orderBy("id", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    int count = documents.size();

                    if (count == 0) {
                        Toast.makeText(this, "No Users Found", Toast.LENGTH_LONG).show();
                        // Handle the case when there are no users
                    } else {
                        for (DocumentSnapshot document : documents) {
                            String id = document.getString("id");
                            String fullName = document.getString("fullName");
                            String userName = document.getString("userName");
                            String password = document.getString("password");
                            String access = document.getString("access");

                            Object userImgObject = document.get("userImg");
                            String userImgBase64 = null;

                            if (userImgObject instanceof String) {
                                // Handle the case when 'userImg' is stored as a Base64 encoded string in Firestore
                                userImgBase64 = (String) userImgObject;
                            }

                            if (access != null && access.equals("Admin")) {
                                admins.add(new userItems(id, fullName, userName, password, access, userImgBase64));
                            } else {
                                users.add(new userItems(id, fullName, userName, password, access, userImgBase64));
                            }
                        }

                        // Set up your RecyclerView adapters here
                        adminAdapter = new adminAdapter(this, admins, this);
                        adminRecyclerView.setAdapter(adminAdapter);

                        userAdapter = new userAdapter(this, users, this);
                        userRecyclerView.setAdapter(userAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error fetching data from Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onItemClicked(userItems view) {
        Intent i = new Intent(getApplicationContext(), userEdit.class);
        i.putExtra("id", view.getId());
        i.putExtra("fullName", view.getFullName());
        i.putExtra("userName", view.getUserName());
        i.putExtra("password", view.getPassword());
        i.putExtra("access", view.getAccess());
        if(view.getUserImgBase64() != null){
            i.putExtra("userImg", view.getUserImgBase64());
        }else{
            //None
        }
        startActivity(i);
    }

    public void search() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearTable();
                String searchUser = txtSearch.getText().toString().trim().toLowerCase();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollection = db.collection("users");

                Query query = usersCollection
                        .orderBy("fullName") // Order by the field you want to search
                        .startAfter(searchUser)
                        .endBefore(searchUser + "\uf8ff"); // \uf8ff is a high surrogate code point

                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            int count1 = documents.size();

                            if (count1 == 0) {
                                refreshList();
                                Toast.makeText(userList.this, "No Users Found", Toast.LENGTH_LONG).show();
                            } else {
                                List<userItems> admins = new ArrayList<>();
                                List<userItems> users = new ArrayList<>();

                                for (DocumentSnapshot document : documents) {
                                    String id = document.getString("id");
                                    String fullName = document.getString("fullName");
                                    String userName = document.getString("userName");
                                    String password = document.getString("password");
                                    String access = document.getString("access");

                                    // Ensure that the fields are not null or empty before adding to the list
                                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(fullName)
                                            && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)
                                            && !TextUtils.isEmpty(access)) {

                                        Object userImgObject = document.get("userImg");
                                        String userImgBase64 = null;

                                        if (userImgObject instanceof String) {
                                            userImgBase64 = (String) userImgObject;
                                        }

                                        if (access.equals("Admin")) {
                                            admins.add(new userItems(id, fullName, userName, password, access, userImgBase64));
                                        } else {
                                            users.add(new userItems(id, fullName, userName, password, access, userImgBase64));
                                        }
                                    }
                                }

                                // Set up your RecyclerView adapters here
                                RecyclerView adminRecyclerView = findViewById(R.id.recycleAdmin);
                                adminRecyclerView.setHasFixedSize(true);
                                adminRecyclerView.setLayoutManager(new LinearLayoutManager(userList.this, LinearLayoutManager.HORIZONTAL, false));
                                adminAdapter = new adminAdapter(userList.this, admins, adminAdapter.mClickListener);
                                adminRecyclerView.setAdapter(adminAdapter);

                                RecyclerView userRecyclerView = findViewById(R.id.recycleUser);
                                userRecyclerView.setHasFixedSize(true);
                                userRecyclerView.setLayoutManager(new LinearLayoutManager(userList.this, LinearLayoutManager.HORIZONTAL, false));
                                userAdapter = new userAdapter(userList.this, users, userAdapter.mClickListener);
                                userRecyclerView.setAdapter(userAdapter);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle the case when there is an error fetching data from Firestore
                            e.printStackTrace();
                            Toast.makeText(userList.this, "Failed to fetch users", Toast.LENGTH_LONG).show();
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private byte[] getUserImgBytesFromUrl(String imageUrl) {
        // Use your preferred method to download the image from the URL and convert it to a byte array
        // This is a simplified example and might not work in all cases
        try {
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0]; // Handle the error case appropriately
        }
    }

    public void clearTable(){
        admins.clear();
        users.clear();
    }

    @Override
    public void onBackPressed() {
    }
}