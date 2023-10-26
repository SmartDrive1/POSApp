package com.example.posapp.products;

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

import com.example.posapp.R;

public class prodEdit extends AppCompatActivity {

    EditText editID, editName, editPrice;

    Button btnEdit, btnDelete, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_edit);

        String[] s1 = new String[] {
                "Drinks", "Food", "Add-Ons","Others"
        };
        Spinner s = (Spinner) findViewById(R.id.catID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        editID = findViewById(R.id.txtEditID);
        editName = findViewById(R.id.txtEditName);
        editPrice = findViewById(R.id.txtEditPrice);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        String id = i.getStringExtra("id".toString());
        String product = i.getStringExtra("product".toString());
        String prodPrice = i.getStringExtra("prodPrice".toString());
        String category = i.getStringExtra("category".toString());
        Integer category1;

        editID.setText(id);
        editName.setText(product);
        editPrice.setText(prodPrice);

        switch (category) {
            case "Drinks":
                category1 = 0;
                break;
            case "Food":
                category1 = 1;
                break;
            case "AddOns":
                category1 = 2;
                break;
            case "Others":
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
    }

    public void edit(){
        try{
            String editID1 = editID.getText().toString();
            String editName1 = editName.getText().toString();
            String editPrice1 = editPrice.getText().toString();
            Spinner spinner = (Spinner)findViewById(R.id.catID);
            String spTxt = spinner.getSelectedItem().toString();
            if(spTxt.equals("Add-Ons")){
                spTxt = "AddOns";
            }
            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
                String sql = "update products set product = ?, category = ?, prodPrice = ? where id = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, editName1);
                statement.bindString(2, spTxt);
                statement.bindString(3, editPrice1);
                statement.bindString(4, editID1);
                statement.execute();
                Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
                db.close();
                Intent i = new Intent(prodEdit.this, productList.class);
                startActivity(i);
        }catch (Exception e) {
            Toast.makeText(this,"Please Input a Valid Amount", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(){
        try{
            String editID1 = editID.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "delete from products where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,editID1);
            statement.execute();
            Toast.makeText(this,"Product Deleted", Toast.LENGTH_LONG).show();
            db.close();
            Intent i = new Intent(prodEdit.this, productList.class);
            startActivity(i);
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }
}