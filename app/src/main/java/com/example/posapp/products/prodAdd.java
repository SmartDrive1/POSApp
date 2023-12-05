package com.example.posapp.products;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    public void insert() {
        String prodName = txtProduct.getText().toString().trim();
        String price = txtPrice.getText().toString().trim();
        String quantity = txtQuantity.getText().toString().trim();
        Spinner spinner = findViewById(R.id.catID);

        if (prodName.equals("")) {
            Toast.makeText(this, "Product Name is Blank. Please Input a Product Name", Toast.LENGTH_LONG).show();
        } else if (quantity.equals("")) {
            Toast.makeText(this, "Quantity is Blank. Please Input a Quantity", Toast.LENGTH_LONG).show();
        } else if (price.equals("")) {
            Toast.makeText(this, "Product Price is Blank. Please Input a Product Price", Toast.LENGTH_LONG).show();
        } else if (prodName.equals("None") || prodName.equals("none")) {
            Toast.makeText(this, "Please Enter Another Product Name", Toast.LENGTH_LONG).show();
        } else {
            try {
                String spTxt = spinner.getSelectedItem().toString();

                // Initialize Firebase Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference productsCollection = db.collection("products");

                // Check if product already exists
                productsCollection.whereEqualTo("product", prodName)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Product already exists
                                Toast.makeText(this, "Product Already Exists", Toast.LENGTH_SHORT).show();
                            } else {
                                // Product is unique, proceed with adding to Firestore

                                // Generate a unique ID for the new product
                                String productId = UUID.randomUUID().toString();

                                // Create a new product document
                                Map<String, Object> productData = new HashMap<>();
                                productData.put("id", productId);
                                productData.put("product", prodName);
                                productData.put("category", spTxt);
                                productData.put("quantity", quantity);
                                productData.put("prodPrice", price);

                                // Upload image to Firebase Storage (assuming selectedImageUri is the image URI)
                                if (selectedImageUri != null) {
                                    try {
                                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                        byte[] imageBytes = getBytes(inputStream);

                                        // Convert byte array to Base64 encoded string
                                        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                                        // Add the Base64 encoded string to the userData map
                                        productData.put("prodImg", base64Image);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    defaultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] defaultImageBytes = stream.toByteArray();

                                    String defaultBase64Image = Base64.encodeToString(defaultImageBytes, Base64.DEFAULT);

                                    productData.put("prodImg", defaultBase64Image);
                                }

                                // Add the product document to Firestore
                                productsCollection.document(productId)
                                        .set(productData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Product added successfully
                                            Toast.makeText(this, "Product Added", Toast.LENGTH_LONG).show();
                                            txtProduct.setText("");
                                            txtPrice.setText("");
                                            txtQuantity.setText("");
                                            txtProduct.requestFocus();
                                            prodImg.setImageResource(R.drawable.noimage);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle Firestore write error
                                            e.printStackTrace();
                                            Toast.makeText(this, "Failed to add product", Toast.LENGTH_LONG).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle Firestore query error
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to check product", Toast.LENGTH_LONG).show();
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Function to convert an image URI to Base64
    private String imageToBase64(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        byte[] bytes = new byte[Objects.requireNonNull(inputStream).available()];
        inputStream.read(bytes);
        inputStream.close();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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

    @Override
    public void onBackPressed() {
    }
}