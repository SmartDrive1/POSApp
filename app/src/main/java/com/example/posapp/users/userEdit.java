package com.example.posapp.users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class userEdit extends AppCompatActivity {

    EditText editID, editName, editUserName, editPassword;
    Button btnEdit, btnDelete, btnCancel;
    ImageView userImg;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;
    String id;
    String userName;
    String name;
    private DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        String[] s1 = new String[] {
                "Admin", "User"
        };

        Spinner s = (Spinner) findViewById(R.id.accID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        editID = findViewById(R.id.txtEditID);
        editName = findViewById(R.id.txtEditName);
        editUserName = findViewById(R.id.txtEditUserName);
        editPassword = findViewById(R.id.txtEditPassword);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);
        userImg = findViewById(R.id.userImg);

        Intent i = getIntent();
        id = i.getStringExtra("id".toString());
        name = i.getStringExtra("fullName".toString());
        userName = i.getStringExtra("userName".toString());
        String password = i.getStringExtra("password".toString());
        String access = i.getStringExtra("access".toString());
        Integer access1;
        String userImgBase64 = getIntent().getStringExtra("userImg");

        if (userImgBase64 != null && !userImgBase64.isEmpty()) {
            byte[] userImgBytes = Base64.decode(userImgBase64, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(userImgBytes, 0, userImgBytes.length);

            userImg.setImageBitmap(bitmap);
        } else {
            userImg.setImageResource(R.drawable.noimage);
        }
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
                toDelete();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(userEdit.this, userList.class);
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

    public void edit(){
        String editID1 = editID.getText().toString().trim();
        String editName1 = editName.getText().toString().trim();
        String editUserName1 = editUserName.getText().toString().trim();
        String editPassword1 = editPassword.getText().toString().trim();
        if(editName1.equals("")){
            Toast.makeText(this,"Name is Blank. Please Enter a Name", Toast.LENGTH_LONG).show();
        }else if(editUserName1.equals("")){
            Toast.makeText(this,"Username is Blank. Please Enter a Username", Toast.LENGTH_LONG).show();
        }else if(editPassword1.equals("")){
            Toast.makeText(this,"Password is Blank. Please Enter a Password", Toast.LENGTH_LONG).show();
        }else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = db.collection("users");

            usersCollection.whereEqualTo("userName", editUserName1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String existingID = document.getString("id");
                                 if (existingID.equals(editID1)) {
                                     updateUser();
                                 } else {
                                     Toast.makeText(this, "Account/Username Already Exists", Toast.LENGTH_SHORT).show();
                                 }
                            }
                        } else {
                            // No user found with the provided username, proceed with the update
                            updateUser();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle query failure
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to query user", Toast.LENGTH_LONG).show();
                    });
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
            // Handle the selected image URI here
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

    public void updateUser() {
        String editUserName1 = editUserName.getText().toString().trim();
        String editName1 = editName.getText().toString().trim();
        String editPassword1 = editPassword.getText().toString().trim();
        Spinner spinner = findViewById(R.id.accID);
        String spTxt = spinner.getSelectedItem().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        // Query to find the document with the given username
        usersCollection.whereEqualTo("userName", userName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Iterate through the results (there should be only one match)
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getId(); // Get the document ID

                            // Update the user document with the new data
                            DocumentReference userDocRef = usersCollection.document(userId);

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("fullName", editName1);
                            updateData.put("userName", editUserName1);
                            updateData.put("password", editPassword1);
                            updateData.put("access", spTxt);

                            try {
                                if (selectedImageUri != null) {
                                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                    byte[] imageBytes = getBytes(inputStream);
                                    String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    updateData.put("userImg", imageBase64);
                                } else {
                                    // Handle the case when no new image is selected
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            userDocRef.update(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        // User updated successfully
                                        Toast.makeText(this, "Account Updated", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(userEdit.this, userList.class);
                                        startActivity(i);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle update failure
                                        e.printStackTrace();
                                        Toast.makeText(this, "Failed to update account", Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle query failure
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to query user", Toast.LENGTH_LONG).show();
                });
    }

    public void toDelete() {
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference usersCollection = db.collection("users");
                            usersCollection.document(id)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(userEdit.this, "Account: " + userName + " Deleted", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(userEdit.this, userList.class);
                                        startActivity(i);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle deletion failure
                                        e.printStackTrace();
                                        Toast.makeText(userEdit.this, "Failed to delete account", Toast.LENGTH_LONG).show();
                                    });
                        } catch (Exception e) {
                            Toast.makeText(userEdit.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(userEdit.this);
        builder.setMessage("Do You Want to Delete the Account: " + userName + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
    }
}