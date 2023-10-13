package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UITest extends AppCompatActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitest);

        back = findViewById(R.id.btnBack);
        RecyclerView recyclerView = findViewById(R.id.recycleProds);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UITest.this, MainScreen.class);
                startActivity(i);
            }
        });

        List<UITestItems> items = new ArrayList<UITestItems>();
        items.add(new UITestItems("a","cs","ld","ds"));
        items.add(new UITestItems("a","cs","ld","ds"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UITestAdapter(getApplicationContext(), items));
    }
}