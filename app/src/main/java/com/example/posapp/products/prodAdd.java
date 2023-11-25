package com.example.posapp.products;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

public class prodAdd extends AppCompatActivity {

    EditText txtProduct, txtPrice, txtQuantity;
    Button btnAdd, btnCancel;
    ImageView prodImg;
    Integer max_id;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_add);

        String[] s1 = new String[]{
                "Drinks", "Food", "Cake", "Special"
        };
        Spinner s = (Spinner) findViewById(R.id.catID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        txtProduct = findViewById(R.id.txtProduct);
        txtPrice = findViewById(R.id.txtPrice);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        txtQuantity = findViewById(R.id.txtQuantity);
        prodImg = findViewById(R.id.prodImg);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(prodAdd.this, productList.class);
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

    public void getMax() {
        try {
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

            Cursor cursor = db.rawQuery("SELECT MAX(id) FROM products", null);

            if (cursor.moveToFirst()) {
                max_id = cursor.getInt(0);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert() {
        String prodName = txtProduct.getText().toString().trim();
        String price = txtPrice.getText().toString().trim();
        String quantity = txtQuantity.getText().toString().trim();
        Spinner spinner = (Spinner) findViewById(R.id.catID);

        getMax();
        max_id += 1;
        if (prodName.equals("")) {
            Toast.makeText(this, "Product Name or Price is Blank", Toast.LENGTH_LONG).show();
        } else if (price.equals("")) {
            Toast.makeText(this, "Product Price is Blank", Toast.LENGTH_LONG).show();
        } else if (prodName.equals("None") || prodName.equals("none")) {
            Toast.makeText(this, "Please Enter Another Product Name", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(price) <= 0) {
            Toast.makeText(this, "Please Enter a Price Greater Than 0", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(quantity) <= 0) {
            Toast.makeText(this, "Please Enter a Quantity Greater Than 0", Toast.LENGTH_LONG).show();
        } else {
            try {
                String spTxt = spinner.getSelectedItem().toString();
                if (spTxt.equals("Add-Ons")) {
                    spTxt = "AddOns";
                }
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY,product VARCHAR, category VARCHAR, quantity INTEGER, prodPrice INTEGER, prodImage BLOB)");

                Cursor c = db.rawQuery("SELECT * FROM products WHERE product =?", new String[]{prodName});
                if (c.getCount() > 0) {
                    Toast.makeText(this, "Product Already Exists", Toast.LENGTH_SHORT).show();
                } else {
                    String sql = "insert into products (id, product, category, quantity, prodPrice, prodImage)values(?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1, String.valueOf(max_id));
                    statement.bindString(2, prodName);
                    statement.bindString(3, spTxt);
                    statement.bindString(4, quantity);
                    statement.bindString(5, price);

                    try {//Add image
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        byte[] imageBytes = getBytes(inputStream);
                        statement.bindBlob(6, imageBytes); // Bind the image bytes
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Broken", Toast.LENGTH_SHORT).show();
                    }

                    statement.execute();
                    Toast.makeText(this, "Product Added", Toast.LENGTH_LONG).show();
                    txtProduct.setText("");
                    txtPrice.setText("");
                    txtQuantity.setText("");
                    txtProduct.requestFocus();
                    prodImg.setImageResource(R.drawable.noimage);
                    db.close();
                }
            } catch (Exception e) {
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