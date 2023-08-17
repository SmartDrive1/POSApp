package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class editOrder extends AppCompatActivity {

    EditText editProduct, qty, editPrice;
    Button edit, remove, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        Intent i = getIntent();
        String prodName = i.getStringExtra("prodName".toString());
        String quantity = i.getStringExtra("quantity".toString());
        String price = i.getStringExtra("price".toString());


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(editOrder.this, cart.class);
                startActivity(i);
            }
        });
    }

    public void edit(){
        try{
            String qty1 = qty.getText().toString();

            if (qty1.equals("") || qty1.equals("")){
                Toast.makeText(this,"Quantity is Blank. Please Input a Value", Toast.LENGTH_LONG).show();
            }else{
                SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

                String sql = "update cartlist set quantity = ? where prodName = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1,qty1);
                statement.execute();
                Toast.makeText(this,"Product Updated", Toast.LENGTH_LONG).show();
                Intent i = new Intent(editOrder.this, OrderingSystem.class);
                startActivity(i);
            }
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(){
        try{
            String product1 = editProduct.getText().toString();

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE,null);

            String sql = "delete from inventory where id = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1,product1);
            statement.execute();
            Toast.makeText(this,"Cart Item Deleted", Toast.LENGTH_LONG).show();
            Intent i = new Intent(editOrder.this, cart.class);
            startActivity(i);
        }catch (Exception e)
        {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show();
        }
    }
    }
}