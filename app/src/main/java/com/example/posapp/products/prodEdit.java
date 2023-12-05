package com.example.posapp.products;

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
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

public class prodEdit extends AppCompatActivity {

    EditText editName, editPrice, editQuantity;
    Button btnEdit, btnDelete, btnCancel;
    ImageView prodImg;
    String id, product;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;

    private DialogInterface.OnClickListener dialogClickListener;

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
        product = i.getStringExtra("product".toString());
        String prodPrice = i.getStringExtra("prodPrice".toString());
        String category = i.getStringExtra("category".toString());
        String quantity = i.getStringExtra("quantity".toString());
        Integer category1;

        String prodImgbase64 = getIntent().getStringExtra("prodImg");

        if (prodImgbase64 != null && !prodImgbase64.isEmpty()) {
            byte[] userImgBytes = Base64.decode(prodImgbase64, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(userImgBytes, 0, userImgBytes.length);

            prodImg.setImageBitmap(bitmap);
        } else {
            prodImg.setImageResource(R.drawable.noimage);
        }

//        editID.setText(id);
        editName.setText(product);
        editPrice.setText(prodPrice);
        editQuantity.setText(quantity);

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
                toDelete();
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

    public void edit() {
        try {
            String editName1 = editName.getText().toString().trim();
            String editPrice1 = editPrice.getText().toString().trim();
            String editQuantity1 = editQuantity.getText().toString().trim();

            if (editName1.equals("")) {
                Toast.makeText(this, "Product Name is Blank. Please Input a Product Name", Toast.LENGTH_LONG).show();
            } else if (editQuantity1.equals("")) {
                Toast.makeText(this, "Quantity is Blank. Please Input a Quantity", Toast.LENGTH_LONG).show();
            } else if (editPrice1.equals("")) {
                Toast.makeText(this, "Price is Blank. Please Input a Price", Toast.LENGTH_LONG).show();
            } else if (editName1.equals("None") || editPrice1.equals("none")) {
                Toast.makeText(this, "Please Enter Another Product Name", Toast.LENGTH_LONG).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference productsCollection = db.collection("products");

                productsCollection.whereEqualTo("product", editName1)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String existingID = document.getString("id");

                                    if (existingID.equals(id)) {
                                        editData();
                                    } else {
                                        Toast.makeText(this, "Product Name Already Exists", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                editData();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle the case when there is an error fetching data from Firestore
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to check existing products", Toast.LENGTH_LONG).show();
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsCollection = db.collection("products");

        String editName1 = editName.getText().toString().trim();
        String editPrice1 = editPrice.getText().toString().trim();
        String editQuantity1 = editQuantity.getText().toString().trim();
        Spinner spinner = findViewById(R.id.catID);
        String spTxt = spinner.getSelectedItem().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("product", editName1);
        updatedData.put("category", spTxt);
        updatedData.put("prodPrice", editPrice1);
        updatedData.put("quantity", editQuantity1);

        try {
            if (selectedImageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = getBytes(inputStream);
                String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                updatedData.put("prodImg", imageBase64); // Add the image Base64 string
            } else {
                // If no new image is selected, use the current image in prodImg
                Bitmap bitmap = ((BitmapDrawable) prodImg.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                updatedData.put("prodImg", imageBase64);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        productsCollection.document(id)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(prodEdit.this, "Product Updated", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(prodEdit.this, productList.class);
                    startActivity(i);
                })
                .addOnFailureListener(e -> {
                    // Handle the case when there is an error updating data in Firestore
                    e.printStackTrace();
                    Toast.makeText(prodEdit.this, "Failed to update product", Toast.LENGTH_LONG).show();
                });
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

    public void toDelete() {
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference productsCollection = db.collection("products");

                        productsCollection.document(id)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(prodEdit.this, "Product: " + product + " Deleted", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(prodEdit.this, productList.class);
                                    startActivity(i);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the case when there is an error deleting the document in Firestore
                                    e.printStackTrace();
                                    Toast.makeText(prodEdit.this, "Failed to delete product", Toast.LENGTH_LONG).show();
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(prodEdit.this);
        builder.setMessage("Do You Want to Delete the Product: " + product + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    @Override
    public void onBackPressed() {
    }
}