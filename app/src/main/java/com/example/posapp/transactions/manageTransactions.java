package com.example.posapp.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.example.posapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    Button btnExcel, back, btnFilter, btnReset;
    EditText txtStartDate, txtEndDate;
    transAdapter transAdapter;
    List<transItems> items = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    long startTimeMilli, endTimeMilli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        back = findViewById(R.id.btnBack);
        btnExcel = findViewById(R.id.btnExcel);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnReset = findViewById(R.id.btnReset);

        btnExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcel(manageTransactions.this);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
                txtStartDate.setText("");
                txtEndDate.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent i = new Intent(manageTransactions.this, mainManageScreen.class);
              startActivity(i);
            }
        });

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePicker();
                if(!txtEndDate.getText().toString().isEmpty()){
                    rangeDate();
                }
            }
        });

        final Calendar c1 = Calendar.getInstance();
        int mYear2 = c1.get(Calendar.YEAR);
        int mMonth2 = c1.get(Calendar.MONTH);
        int mDay2 = c1.get(Calendar.DAY_OF_MONTH);

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(manageTransactions.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Set the time to 23:59:59
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        selectedCalendar.set(Calendar.MINUTE, 59);
                        selectedCalendar.set(Calendar.SECOND, 59);

                        endTimeMilli = selectedCalendar.getTimeInMillis();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        txtEndDate.setText(dateFormat.format(selectedCalendar.getTime()));
                        rangeDate();
                    }
                }, mYear2, mMonth2, mDay2);
                datePickerDialog.getDatePicker().setMinDate(startTimeMilli);

                datePickerDialog.show();
            }
        });
        refreshList();
    }

    @SuppressLint("Range")
    public void refreshList() {
        items.clear();
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

    public void exportToExcel(Context context) {
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
    private void writeDataFromDatabase(Context context, XSSFSheet sheet) {
        SQLiteDatabase db = context.openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
        if(txtStartDate.getText().toString().trim().equals("")){
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
        }else{
            Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE time BETWEEN " + startTimeMilli + " AND " + endTimeMilli,null);
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

    private long convertDateToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            if (date != null) {
                return (date.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void rangeDate(){
        items.clear();

        String formattedDate;
        RecyclerView recyclerView = findViewById(R.id.recycleTrans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, time INTEGER)");
            String query = "SELECT transID, time, SUM(price) AS totalAmount, SUM(quantity) AS totalQuantity FROM transactions WHERE time BETWEEN " + startTimeMilli + " AND " + endTimeMilli + " GROUP BY transID";
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
                }
                cursor.close();
                db.close();

                transAdapter = new transAdapter(this, items, this);
                recyclerView.setAdapter(transAdapter);
            }
        }catch (Exception e) {
            Toast.makeText(this, "Database Error", Toast.LENGTH_LONG).show();
        }
    }

    private void showStartDatePicker(){
        final Calendar c = Calendar.getInstance();
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(manageTransactions.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, monthOfYear);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Set the time components to start of the day
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);

                startTimeMilli = selectedCalendar.getTimeInMillis();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(selectedCalendar.getTime());

                txtStartDate.setText(formattedDate);
                if(!txtEndDate.getText().toString().isEmpty()){
                    if(startTimeMilli > endTimeMilli){
                        txtEndDate.setText(formattedDate);
                        rangeDate();
                    }else{
                        rangeDate();
                    }
                }
            }
        }, mYear1, mMonth1, mDay1);

        datePickerDialog.show();
        enableEndDateEditText(true);
    }

    private void enableEndDateEditText(boolean enable) {
        txtEndDate.setEnabled(enable);
    }
}