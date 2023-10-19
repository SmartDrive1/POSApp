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

public class login extends AppCompatActivity {

    EditText txtUser, txtPass;
    Button btnLogin, btnForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgetPass = findViewById(R.id.btnForgetPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
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

    public void Login(){
        String username = txtUser.getText().toString();
        String pass = txtPass.getText().toString();

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, fullName VARCHAR, userName VARCHAR, password VARCHAR, access, VARCHAR)");//incase there are no tables yet
        Cursor c = db.rawQuery("select * from users WHERE userName='" + username + "'and password='" + pass + "'", null);

        if (username.equals("") || pass.equals("")){
            Toast.makeText(login.this, "Username or Password Blank", Toast.LENGTH_SHORT).show();
        }else if(username.equals("admin") && pass.equals("admin")){
            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(login.this, MainScreen.class);
            startActivity(i);
        }else if (c.moveToFirst())
        {
            int accessIndex = c.getColumnIndex("access");
            accessValue.access = c.getString(accessIndex);
            String currentAccess = accessValue.access;
            if(currentAccess.equals("Admin")){
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(login.this, MainScreen.class);
                startActivity(i);
            }else{
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(login.this, OrderingSystem.class);
                startActivity(i);
            }

        }else
        {
            Toast.makeText(login.this, "Wrong Login Credentials. Please Try Again.", Toast.LENGTH_SHORT).show();
        }
    }
}