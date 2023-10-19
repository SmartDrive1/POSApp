package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class userEdit extends AppCompatActivity {

    EditText editID, editName, editUserName, editPassword;

    Button btnEdit, btnDelete, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users);

        String[] s1 = new String[] {
                "Admin", "User"
        };

        Spinner s = (Spinner) findViewById(R.id.accID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        editID = findViewById(R.id.txtEditID);
        editName = findViewById(R.id.txtEditName);
        editUserName = findViewById(R.id.txtEditUserName);
        editPassword = findViewById(R.id.txtEditPassword);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        String id = i.getStringExtra("id".toString());
        String name = i.getStringExtra("fullName".toString());
        String userName = i.getStringExtra("userName".toString());
        String password = i.getStringExtra("password".toString());
        String access = i.getStringExtra("access".toString());
        Integer access1;

        editID.setText(id);
        editName.setText(name);
        editUserName.setText(userName);
        editPassword.setText(password);
        if (access.equals("Admin")){
            access1 = 0;
        }else{
            access1 = 1;
        }
        s.setSelection(access1);


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(userEdit.this, userList.class);
                startActivity(i);
            }
        });
    }

    public void edit(){
        try{
            String editID1 = editID.getText().toString();
            String editName1 = editName.getText().toString();
            String editUserName1 = editUserName.getText().toString();
            String editPassword1 = editPassword.getText().toString();
            Spinner spinner = (Spinner)findViewById(R.id.accID);
            String spTxt = spinner.getSelectedItem().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "update users set fullName = ?, userName = ?, password = ?, access = ? where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,editName1);
            statement.bindString(2,editUserName1);
            statement.bindString(3,editPassword1);
            statement.bindString(4,spTxt);
            statement.bindString(5,editID1);
            statement.execute();
            Toast.makeText(this,"User Updated", Toast.LENGTH_LONG).show();
            db.close();
            Intent i = new Intent(userEdit.this, userList.class);
            startActivity(i);
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(){
        try{
            String editID1 = editID.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "delete from users where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,editID1);
            statement.execute();
            Toast.makeText(this,"User Deleted", Toast.LENGTH_LONG).show();
            db.close();
            Intent i = new Intent(userEdit.this, userList.class);
            startActivity(i);
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }
    }