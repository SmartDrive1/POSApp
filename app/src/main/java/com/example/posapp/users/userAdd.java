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
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.posapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if(fName.equals("")){
            Toast.makeText(this,"Name is Blank. Please Input a Name", Toast.LENGTH_LONG).show();
        }else if(uName.equals("")) {
            Toast.makeText(this,"Username is Blank. Please Input a Username", Toast.LENGTH_LONG).show();
        }else if(pass.equals("")) {
            Toast.makeText(this, "Password is Blank. Please Input a Password", Toast.LENGTH_LONG).show();
        }else if(uName.equals("Admin") || uName.equals("admin")) {
            Toast.makeText(this,"Please Choose Another Username", Toast.LENGTH_LONG).show();
        }else{
            try {
                String fName1 = txtFullName.getText().toString().trim();
                String uName1 = txtUserName.getText().toString().trim();
                String pass1 = txtPassword.getText().toString().trim();
                String spTxt = spinner.getSelectedItem().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollection = db.collection("users");

                // Check if username already exists
                usersCollection.whereEqualTo("userName", uName1)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Username already exists
                                Toast.makeText(this, "Account/Username Already Exists", Toast.LENGTH_SHORT).show();
                            } else {
                                // Username is unique, proceed with adding the user

                                // Generate a unique ID for the new user
                                String userId = UUID.randomUUID().toString();

                                // Create a new user document
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("id", userId);
                                userData.put("fullName", fName1);
                                userData.put("userName", uName1);
                                userData.put("password", pass1);
                                userData.put("access", spTxt);

                                // Upload image to Firebase Storage (assuming selectedImageUri is the image URI)
                                if (selectedImageUri != null) {
                                    try {
                                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                        byte[] imageBytes = getBytes(inputStream);

                                        // Convert byte array to Base64 encoded string
                                        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                                        // Add the Base64 encoded string to the userData map
                                        userData.put("userImg", base64Image);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    defaultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] defaultImageBytes = stream.toByteArray();

                                    String defaultBase64Image = Base64.encodeToString(defaultImageBytes, Base64.DEFAULT);

                                    userData.put("userImg", defaultBase64Image);
                                }

                                // Add the user document to Firestore
                                usersCollection.document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            // User added successfully
                                            Toast.makeText(this, "User Added", Toast.LENGTH_LONG).show();
                                            txtFullName.setText("");
                                            txtUserName.setText("");
                                            txtPassword.setText("");
                                            txtFullName.requestFocus();
                                            userImg.setImageResource(R.drawable.noimage);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle Firestore write error
                                            e.printStackTrace();
                                            Toast.makeText(this, "Failed to add user", Toast.LENGTH_LONG).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle Firestore query error
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to check username", Toast.LENGTH_LONG).show();
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
    }
}