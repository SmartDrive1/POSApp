package com.example.posapp.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.sql.Blob;

public class prodEdit extends AppCompatActivity {

    EditText editID, editName, editPrice, editQuantity;
    Button btnEdit, btnDelete, btnCancel;
    ImageView prodImg;
    String id;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_edit);

        String[] s1 = new String[] {
                "Drinks", "Food", "Cake","Special"
        };
        Spinner s = (Spinner) findViewById(R.id.catID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

//        editID = findViewById(R.id.txtEditID);
        editName = findViewById(R.id.txtEditName);
        editPrice = findViewById(R.id.txtEditPrice);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);
        editQuantity = findViewById(R.id.editQuantity);
        prodImg = findViewById(R.id.prodImg);

        Intent i = getIntent();
        id = i.getStringExtra("id".toString());
        String product = i.getStringExtra("product".toString());
        String prodPrice = i.getStringExtra("prodPrice".toString());
        String category = i.getStringExtra("category".toString());
        String quantity = i.getStringExtra("quantity".toString());
        byte[] prodImage1 = i.getByteArrayExtra("prodImage");
        Integer category1;

//        editID.setText(id);
        editName.setText(product);
        editPrice.setText(prodPrice + ".00");
        editQuantity.setText(quantity);
        if(prodImage1 != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(prodImage1, 0, prodImage1.length);
            prodImg.setImageBitmap(bitmap);

        }

        switch (category) {
            case "Drinks":
                category1 = 0;
                break;
            case "Food":
                category1 = 1;
                break;
            case "Cake":
                category1 = 2;
                break;
            case "Special":
                category1 = 3;
                break;
            default:
                category1 = 0;
                break;
        }
        s.setSelection(category1);

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
                Intent i = new Intent(prodEdit.this, productList.class);
                startActivity(i);
            }
        });

        prodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageSelection();
            }
        });
    }

    public void edit(){
        try{
            String editName1 = editName.getText().toString().trim();
            String editPrice1 = editPrice.getText().toString().trim();
            String editQuantity1 = editQuantity.getText().toString().trim();
            if (editName1.equals("")){
                Toast.makeText(this,"Please Input a Valid Product Name", Toast.LENGTH_LONG).show();
            }else if (editPrice1.equals("")) {
                Toast.makeText(this,"Please Input a Valid Amount", Toast.LENGTH_LONG).show();
            }else if (editQuantity1.equals("")) {
                Toast.makeText(this,"Please Input a Valid Quantity", Toast.LENGTH_LONG).show();
            }else if (editName1.equals("None") || editPrice1.equals("none")) {
                Toast.makeText(this, "Please Enter Another Product Name", Toast.LENGTH_LONG).show();
            }else{
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * FROM products WHERE product =?", new String[]{editName1});

                if(c.getCount() > 0){
                    if(c.moveToFirst()){
                        int existingID = c.getColumnIndex("id");
                        if(c.getString(existingID).equals(id)){
                            editData();
                        }else{
                            Toast.makeText(this, "Product Name Already Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    editData();
                }
            }
        }catch (Exception e) {

        }
    }

    public void editData(){
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        String editName1 = editName.getText().toString().trim();
        String editPrice1 = editPrice.getText().toString().trim();
        String editQuantity1 = editQuantity.getText().toString().trim();
        Spinner spinner = (Spinner)findViewById(R.id.catID);
        String spTxt = spinner.getSelectedItem().toString();

        String sql = "update products set product = ?, category = ?, prodPrice = ?, quantity = ?, prodImage = ? where id = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, editName1);
        statement.bindString(2, spTxt);
        statement.bindString(3, editPrice1);
        statement.bindString(4, editQuantity1);
        try {
            if (selectedImageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = getBytes(inputStream);
                statement.bindBlob(5, imageBytes); // Bind the image bytes
            } else {
                // If no new image is selected, use the current image in prodImg
                Bitmap bitmap = ((BitmapDrawable) prodImg.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                statement.bindBlob(5, imageBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        statement.bindString(6, id);

        statement.execute();
        Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
        db.close();
        Intent i = new Intent(prodEdit.this, productList.class);
        startActivity(i);
    }

    public void delete(){
        try{
            String editID1 = editID.getText().toString().trim();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "delete from products where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,editID1);
            statement.execute();
            Toast.makeText(this,"Product Deleted", Toast.LENGTH_LONG).show();
            db.close();
            Intent i = new Intent(prodEdit.this, productList.class);
            startActivity(i);
        }catch (Exception e){
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
            Toast.makeText(this, "Image selected: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
            prodImg.setImageURI(selectedImageUri);
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