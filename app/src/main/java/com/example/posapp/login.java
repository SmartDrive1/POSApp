package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    }

    public void Login(){
        String username = txtUser.getText().toString();
        String pass = txtPass.getText().toString();

        if(username.equals("") || pass.equals(""))
        {
            Toast.makeText(this, "Username or Password Blank", Toast.LENGTH_LONG).show();
        }else if(username.equals("john") && pass.equals("123"))
        {
            Intent i = new Intent(login.this, MainScreen.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Username or Password Do Not Match", Toast.LENGTH_LONG).show();
        }
    }
}