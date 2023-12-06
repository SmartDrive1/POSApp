package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.OrderingSystem.accessValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class forgetPassword extends AppCompatActivity {

    Button btnBack, btnLogin;
    EditText txtUser, txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btnBack = findViewById(R.id.btnBack);
        btnLogin = findViewById(R.id.btnLogin);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(forgetPassword.this, login.class);
                startActivity(i);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login() {
        String username = txtUser.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userName", username)
                .whereEqualTo("password", pass)
                .whereEqualTo("access", "Admin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // User found in Firestore
                            DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                            String currentAccess = userDoc.getString("access");

                            if ("admin".equals(username) && "admin".equals(pass)) { // Edit before deployment
                                currentAccess = "Admin";
                            }

                            accessValue.access = currentAccess;
                            accessValue.user = username;

                            Toast.makeText(forgetPassword.this, "Login successful", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if ("Admin".equals(currentAccess)) {
                                intent = new Intent(forgetPassword.this, MainScreen.class);
                            } else {
                                intent = new Intent(forgetPassword.this, OrderingSystem.class);
                            }

                            startActivity(intent);
                        } else {
                            // User not found in Firestore
                            Toast.makeText(forgetPassword.this, "Please Enter an Admin Account", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error in Firestore query
                        Toast.makeText(forgetPassword.this, "Failed to authenticate user: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}