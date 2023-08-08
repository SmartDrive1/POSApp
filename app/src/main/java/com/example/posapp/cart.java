package com.example.posapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class cart extends AppCompatActivity {

    //ListView lstCart1;
    TextView txt1;
    String tmpName, tmpQty, tmpPrice;
    Integer arrayLength, ctr = 0;
    ArrayList<String> titles = new ArrayList <String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txt1 = findViewById(R.id.txtView);

        cCurrentTransac a = new cCurrentTransac();
        tmpName = a.Product.get(1);
        txt1.setText(tmpName);
    }
}