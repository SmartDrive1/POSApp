package com.example.posapp.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.posapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.posapp.login;
import com.example.posapp.mainManageScreen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class manageTransactions extends AppCompatActivity implements transClickListener {
    Button btnExcel, back;
    transAdapter transAdapter;
    List<transItems> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        back = findViewById(R.id.btnBack);
        btnExcel = findViewById(R.id.btnExcel);

        btnExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcel(manageTransactions.this);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(manageTransactions.this, mainManageScreen.class);
                startActivity(i);
            }
        });
        refreshList();
    }

    @SuppressLint("Range")
    public void refreshList() {
        String formattedDate;
        RecyclerView recyclerView = findViewById(R.id.recycleTrans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, time INTEGER)");
            String query = "SELECT transID, time, SUM(price) AS totalAmount, SUM(quantity) AS totalQuantity FROM transactions GROUP BY transID";
            Cursor cursor = db.rawQuery(query, null);

            int id = cursor.getColumnIndex("transID");
            int time = cursor.getColumnIndex("time");
            int totalAmount = cursor.getColumnIndex("totalAmount");
            int totalQuantity = cursor.getColumnIndex("totalQuantity");

            if (cursor.getCount() == 0){
                Toast.makeText(this, "No Transactions Found", Toast.LENGTH_LONG).show();
            }else{
                while(cursor.moveToNext()){
                    formattedDate = dateFormat.format(new Date(cursor.getLong(time)));
                    items.add(new transItems(cursor.getString(id), "", cursor.getString(totalQuantity), cursor.getString(totalAmount), formattedDate));
                    transAdapter = new transAdapter(this, items, this);
                    recyclerView.setAdapter(transAdapter);
                }

                cursor.close();
                db.close();
            }
            }catch (Exception e) {
            Toast.makeText(this, "Database Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClicked(transItems view) {
        Intent i = new Intent(getApplicationContext(), transEdit.class);
        i.putExtra("id",view.getTransID());
        startActivity(i);
    }

    public static void exportToExcel(Context context) {
        if (checkPermission(context)) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Sheet1");
            writeDataFromDatabase(context, sheet);
            saveExcelFile(context, workbook);
        } else {
            // Request permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressLint("Range")
    private static void writeDataFromDatabase(Context context, XSSFSheet sheet) {
        SQLiteDatabase db = context.openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM transactions", null);

        // Headers
        Row headerRow = sheet.createRow(0);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Transaction ID");

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Product Name");

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Quantity");

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("Price");

        Cell headerCell5 = headerRow.createCell(4);
        headerCell5.setCellValue("Time");

        int rowNum = 1;
        while (cursor.moveToNext()) {
            Row dataRow = sheet.createRow(rowNum++);

            String formattedDate;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
            int time = cursor.getColumnIndex("time");
            formattedDate = dateFormat.format(new Date(cursor.getLong(time)));

            Cell dataCell1 = dataRow.createCell(0);
            dataCell1.setCellValue(cursor.getString(cursor.getColumnIndex("transID")));

            Cell dataCell2 = dataRow.createCell(1);
            dataCell2.setCellValue(cursor.getString(cursor.getColumnIndex("prodName")));

            Cell dataCell3 = dataRow.createCell(2);
            dataCell3.setCellValue(cursor.getString(cursor.getColumnIndex("quantity")));

            Cell dataCell4 = dataRow.createCell(3);
            dataCell4.setCellValue(cursor.getString(cursor.getColumnIndex("price")));

            Cell dataCell5 = dataRow.createCell(4);
            dataCell5.setCellValue(formattedDate);
        }
        cursor.close();
        db.close();
    }

    private static void saveExcelFile(Context context, XSSFWorkbook workbook) {
        try {
            String exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(exportDir, "transactions.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            Log.d("ExcelExporter", "Excel file saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}