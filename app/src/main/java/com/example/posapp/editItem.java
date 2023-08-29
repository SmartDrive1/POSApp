package com.example.posapp;

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

public class editItem extends AppCompatActivity {

    EditText editID, txtEditItemName, txtEditItemStock;

    Button btnEdit, btnDelete, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);


        editID = findViewById(R.id.txtEditID);
        txtEditItemName = findViewById(R.id.txtEditItemName);
        txtEditItemStock = findViewById(R.id.txtEditItemStock);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        String id = i.getStringExtra("id".toString());
        String itemName = i.getStringExtra("itemName".toString());
        String itemStock = i.getStringExtra("itemStock".toString());

        editID.setText(id);
        txtEditItemName.setText(itemName);
        txtEditItemStock.setText(itemStock);


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
                Intent i = new Intent(editItem.this, inventory.class);
                startActivity(i);
            }
        });
    }

    public void edit(){
        try{
            String upID = editID.getText().toString();
            String editItemName1 = txtEditItemName.getText().toString();
            String editItemStock1 = txtEditItemStock.getText().toString();

            if (editItemStock1.equals("") || editItemName1.equals("")){
                Toast.makeText(this,"Item Name or Item Stock is Blank. Please Input a Value", Toast.LENGTH_LONG).show();
            }else{
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "update inventory set itemName = ?, stock = ? where id = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,editItemName1);
                statement.bindString(2,editItemStock1);
                statement.bindString(3,upID);
                statement.execute();
                Toast.makeText(this,"Product Updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(editItem.this, inventory.class);
                startActivity(i);
            }
            }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(){
        try{
            String editID1 = editID.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "delete from inventory where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,editID1);
            statement.execute();
            Toast.makeText(this,"Product Deleted", Toast.LENGTH_LONG).show();
            Intent i = new Intent(editItem.this, inventory.class);
            startActivity(i);
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }
}