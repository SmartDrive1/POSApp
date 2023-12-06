package com.example.posapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.OrderingSystem.OrderingSystem;
import com.example.posapp.OrderingSystem.accessValue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class login extends AppCompatActivity {

    EditText txtUser, txtPass;
    Button btnLogin, btnForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FirebaseApp.initializeApp(this);

        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgetPass = findViewById(R.id.btnForgetPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessValue.access = "Admin";
                Intent i = new Intent(login.this, forgetPassword.class);
                startActivity(i);
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

                            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if ("Admin".equals(currentAccess)) {
                                intent = new Intent(login.this, MainScreen.class);
                            } else {
                                intent = new Intent(login.this, OrderingSystem.class);
                            }

                            startActivity(intent);
                        } else {
                            // User not found in Firestore
                            Toast.makeText(login.this, "Wrong Login Credentials. Please Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error in Firestore query
                        Toast.makeText(login.this, "Failed to authenticate user: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }
}