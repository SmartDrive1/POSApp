package com.example.posapp.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.posapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class userAdd extends AppCompatActivity {

    EditText  txtFullName, txtUserName, txtPassword;
    Button btnAddUser, btnCancel;
    ImageView userImg;
    Integer max_id;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        String[] s1 = new String[] {
                "Admin", "User"
        };
        Spinner s = (Spinner) findViewById(R.id.accID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        txtFullName = findViewById(R.id.txtFullName);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword =  findViewById(R.id.txtPassWord);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddUser = findViewById(R.id.btnAddUser);
        userImg = findViewById(R.id.userImg);

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

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageSelection();
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
        String fName = txtFullName.getText().toString().trim();
        String uName = txtUserName.getText().toString().trim();
        String pass = txtPassword.getText().toString().trim();
        Spinner spinner = (Spinner)findViewById(R.id.accID);

        getMax();
        max_id += 1;
        if(fName.equals("") || uName.equals("") || pass.equals("")){
            Toast.makeText(this,"Full Name, UserName, or Password is Blank", Toast.LENGTH_LONG).show();
        }else if(uName.equals("Admin") || uName.equals("admin")) {
            Toast.makeText(this,"Please Choose Another Username", Toast.LENGTH_LONG).show();
        }else{
            try{
                String fName1 = txtFullName.getText().toString().trim();
                String uName1 = txtUserName.getText().toString().trim();
                String pass1 = txtPassword.getText().toString().trim();
                String spTxt = spinner.getSelectedItem().toString();
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,fullName VARCHAR, userName VARCHAR, password VARCHAR, access VARCHAR, userImg BLOB)"); //Create database if non-existent, to avoid crash

                Cursor c = db.rawQuery("SELECT * FROM users WHERE userName =?", new String[]{uName1});
                if(c.getCount() > 0){
                    Toast.makeText(this, "Account/Username Already Exists", Toast.LENGTH_SHORT).show();
                }else{
                    String sql = "INSERT INTO users (id, fullName, userName, password, access, userImg)values(?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1,String.valueOf(max_id));
                    statement.bindString(2,fName1);
                    statement.bindString(3,uName1);
                    statement.bindString(4,pass1);
                    statement.bindString(5,spTxt);

                    if (selectedImageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            byte[] imageBytes = getBytes(inputStream);
                            statement.bindBlob(6, imageBytes); // Bind the image bytes
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        defaultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] defaultImageBytes = stream.toByteArray();
                        statement.bindBlob(6, defaultImageBytes);
                    }

                    statement.execute();
                    Toast.makeText(this,"User Added", Toast.LENGTH_LONG).show();
                    txtFullName.setText("");
                    txtUserName.setText("");
                    txtPassword.setText("");
                    txtFullName.requestFocus();
                    userImg.setImageResource(R.drawable.noimage);
                }
                c.close();
                db.close();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startImageSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            userImg.setImageURI(selectedImageUri);
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}