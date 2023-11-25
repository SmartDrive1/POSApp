package com.example.posapp.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class userEdit extends AppCompatActivity {

    EditText editID, editName, editUserName, editPassword;
    Button btnEdit, btnDelete, btnCancel;
    ImageView userImg;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;

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
        String id = i.getStringExtra("id".toString());
        String name = i.getStringExtra("fullName".toString());
        String userName = i.getStringExtra("userName".toString());
        String password = i.getStringExtra("password".toString());
        String access = i.getStringExtra("access".toString());
        byte[] userImg1 = i.getByteArrayExtra("userImg");
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
        if(userImg1 != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(userImg1, 0, userImg1.length);
            userImg.setImageBitmap(bitmap);
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

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageSelection();
            }
        });
    }

    public void edit(){
        try{
            String editID1 = editID.getText().toString().trim();
            String editName1 = editName.getText().toString().trim();
            String editUserName1 = editUserName.getText().toString().trim();
            String editPassword1 = editPassword.getText().toString().trim();
            if(editName1.equals("")){
                Toast.makeText(this,"Full Name is Blank. Please Enter a Valid Name", Toast.LENGTH_LONG).show();
            }else if(editUserName1.equals("")){
                Toast.makeText(this,"username is Blank. Please Enter a Valid Username", Toast.LENGTH_LONG).show();
            }else if(editPassword1.equals("")){
                Toast.makeText(this,"Password is Blank. Please Enter a Valid Password", Toast.LENGTH_LONG).show();
            }else {
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * FROM users WHERE userName = ?", new String[]{editUserName1});
                if (c.getCount() > 0) {
                    if(c.moveToFirst()){
                        int existingID = c.getColumnIndex("id");
                        if (c.getString(existingID).equals(editID1)) {
                            updateUser();
                        } else {
                            Toast.makeText(this, "Account/Username Already Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    updateUser();
                }
            }
        }catch (Exception e){
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

    public void updateUser(){
        String editID1 = editID.getText().toString().trim();
        String editName1 = editName.getText().toString().trim();
        String editUserName1 = editUserName.getText().toString().trim();
        String editPassword1 = editPassword.getText().toString().trim();
        Spinner spinner = (Spinner)findViewById(R.id.accID);
        String spTxt = spinner.getSelectedItem().toString();

        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        String sql = "update users set fullName = ?, userName = ?, password = ?, access = ?, userImg = ? where id = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, editName1);
        statement.bindString(2, editUserName1);
        statement.bindString(3, editPassword1);
        statement.bindString(4, spTxt);

        try {
            if (selectedImageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = getBytes(inputStream);
                statement.bindBlob(5, imageBytes); // Bind the image bytes
            } else {
                // If no new image is selected, use the current image in prodImg
                Bitmap bitmap = ((BitmapDrawable) userImg.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                statement.bindBlob(5, imageBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        statement.bindString(6, editID1);
        statement.execute();
        Toast.makeText(this, "User Updated", Toast.LENGTH_LONG).show();
        db.close();
        Intent i = new Intent(userEdit.this, userList.class);
        startActivity(i);
    }
}