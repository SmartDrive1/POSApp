package com.example.posapp.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.posapp.R;

public class userAdd extends AppCompatActivity {

    EditText  txtFullName, txtUserName, txtPassword;
    Button btnAddUser, btnCancel;
    Integer max_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        String[] s1 = new String[] {
                "Admin", "User"
        };
        Spinner s = (Spinner) findViewById(R.id.accID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        txtFullName = findViewById(R.id.txtFullName);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword =  findViewById(R.id.txtPassWord);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddUser = findViewById(R.id.btnAddUser);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(userAdd.this, userList.class);
                startActivity(i);
            }
        });
    }

    public void getMax(){
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("SELECT MAX(id) FROM users", null);

            if (cursor.moveToFirst()) {
                max_id = cursor.getInt(0);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert()
    {
        String fName = txtFullName.getText().toString();
        String uName = txtUserName.getText().toString();
        String pass = txtPassword.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.accID);

        getMax();
        max_id += 1;
        if(fName.equals("") || uName.equals("") || pass.equals(""))
        {
            Toast.makeText(this,"Full Name, UserName, or Password is Blank", Toast.LENGTH_LONG).show();
        }else{
            try{
                String fName1 = txtFullName.getText().toString();
                String uName1 = txtUserName.getText().toString();
                String pass1 = txtPassword.getText().toString();
                String spTxt = spinner.getSelectedItem().toString();
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR)"); //Create database if non-existent, to avoid crash

                Cursor c = db.rawQuery("SELECT * FROM users WHERE userName =?", new String[]{uName1});
                if(c.getCount() > 0){
                    Toast.makeText(this, "Account/Username Already Exists", Toast.LENGTH_SHORT).show();
                }else{
                    String sql = "INSERT INTO users (id, fullName, userName, password, access)values(?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1,String.valueOf(max_id));
                    statement.bindString(2,fName1);
                    statement.bindString(3,uName1);
                    statement.bindString(4,pass1);
                    statement.bindString(5,spTxt);
                    statement.execute();
                    Toast.makeText(this,"User Added", Toast.LENGTH_LONG).show();
                    txtFullName.setText("");
                    txtUserName.setText("");
                    txtPassword.setText("");
                    txtFullName.requestFocus();
                }
                c.close();
                db.close();
            }catch (Exception e)
            {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    }