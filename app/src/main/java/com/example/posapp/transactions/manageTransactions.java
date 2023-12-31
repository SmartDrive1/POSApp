package com.example.posapp.transactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.Toast;
import com.example.posapp.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.posapp.mainManageScreen;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class manageTransactions extends AppCompatActivity implements transClickListener {
    Button btnExcel, back, btnReset;
    EditText txtStartDate, txtEndDate;
    transAdapter transAdapter;
    List<transItems> items = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    long startTimeMilli, endTimeMilli;
    long currentDay, currentDayE;
    ImageView upIndicator, downIndicator;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);

        back = findViewById(R.id.btnBack);
        btnExcel = findViewById(R.id.btnExcel);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnReset = findViewById(R.id.btnReset);
        upIndicator = findViewById(R.id.upIndicator);
        downIndicator = findViewById(R.id.downIndicator);

        recyclerView = findViewById(R.id.recycleTrans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        transAdapter = new transAdapter(this, items, this);
        recyclerView.setAdapter(transAdapter);

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();
                int itemsPerPage = 1;

                if (itemCount <= visibleItemCount || itemCount <= itemsPerPage) {
                    downIndicator.setVisibility(View.GONE);
                    upIndicator.setVisibility(View.GONE);
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItemPosition + itemsPerPage >= itemCount) {
                        downIndicator.setVisibility(View.GONE);
                        upIndicator.setVisibility(View.VISIBLE);
                    } else {
                        downIndicator.setVisibility(View.VISIBLE);
                        upIndicator.setVisibility(View.GONE);
                    }
                }
            }
        });
        transferFirestoreToSQLite();
        currentDate();
        refreshList();
    }

    @SuppressLint("Range")
    public void refreshList() {
        items.clear();
        String formattedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            String query = "SELECT transID, time, category, SUM(price) AS totalAmount, SUM(quantity) AS totalQuantity FROM transactions WHERE time BETWEEN " + currentDay + " AND " + currentDayE + " GROUP BY transID";
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
                    items.add(new transItems(cursor.getString(id), "", cursor.getString(totalQuantity), cursor.getString(totalAmount), "", formattedDate));
                }
                cursor.close();
                db.close();

                transAdapter.notifyDataSetChanged();
            }
        }catch (Exception e) {
            Toast.makeText(this, "No Transactions", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClicked(transItems view) {
        Intent i = new Intent(getApplicationContext(), transEdit.class);
        i.putExtra("id",view.getTransID());
        i.putExtra("time",view.gettDate());
        startActivity(i);
    }

    public void exportToExcel(Context context) {
        if (checkPermission(context)) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Sheet1");
            writeDataFromDatabase(context, sheet);
            saveExcelFile(context, workbook);
        } else {
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
            Cursor cursor = db.rawQuery("SELECT transID, prodName, category, quantity, price, time, (quantity * price) AS total_price FROM transactions ORDER BY time ASC", null);

            // Headers
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Transaction ID");

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Product Name");

            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue("Category");

            Cell headerCell4 = headerRow.createCell(3);
            headerCell4.setCellValue("Quantity");

            Cell headerCell5 = headerRow.createCell(4);
            headerCell5.setCellValue("Price");

            Cell headerCell6 = headerRow.createCell(5);
            headerCell6.setCellValue("Total Price");

            Cell headerCell7 = headerRow.createCell(6);
            headerCell7.setCellValue("Time");

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
                dataCell3.setCellValue(cursor.getString(cursor.getColumnIndex("category")));

                Cell dataCell4 = dataRow.createCell(3);
                dataCell4.setCellValue(cursor.getInt(cursor.getColumnIndex("quantity")));

                Cell dataCell5 = dataRow.createCell(4);
                dataCell5.setCellValue(cursor.getDouble(cursor.getColumnIndex("price")));

                Cell dataCell6 = dataRow.createCell(5);
                dataCell6.setCellValue(cursor.getDouble(cursor.getColumnIndex("total_price")));

                Cell dataCell7 = dataRow.createCell(6);
                dataCell7.setCellValue(formattedDate);
            }
            cursor.close();
            db.close();
        }else{
            Cursor cursor = db.rawQuery("SELECT transID, prodName, category, quantity, price, time, (quantity * price) AS total_price FROM transactions WHERE time BETWEEN " + startTimeMilli + " AND " + endTimeMilli + " ORDER BY time ASC",null);
            // Headers
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Transaction ID");

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Product Name");

            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue("Category");

            Cell headerCell4 = headerRow.createCell(3);
            headerCell4.setCellValue("Quantity");

            Cell headerCell5 = headerRow.createCell(4);
            headerCell5.setCellValue("Price");

            Cell headerCell6 = headerRow.createCell(5);
            headerCell6.setCellValue("Total Price");

            Cell headerCell7 = headerRow.createCell(6);
            headerCell7.setCellValue("Time");

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
                dataCell3.setCellValue(cursor.getString(cursor.getColumnIndex("category")));

                Cell dataCell4 = dataRow.createCell(3);
                dataCell4.setCellValue(cursor.getInt(cursor.getColumnIndex("quantity")));

                Cell dataCell5 = dataRow.createCell(4);
                dataCell5.setCellValue(cursor.getDouble(cursor.getColumnIndex("price")));

                Cell dataCell6 = dataRow.createCell(5);
                dataCell6.setCellValue(cursor.getDouble(cursor.getColumnIndex("total_price")));

                Cell dataCell7 = dataRow.createCell(6);
                dataCell7.setCellValue(formattedDate);
            }
            cursor.close();
            db.close();
        }
    }

    private void saveExcelFile(Context context, XSSFWorkbook workbook) {
        try {
            String exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(exportDir, "transactions.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            Log.d("ExcelExporter", "Excel file saved to: " + file.getAbsolutePath());
            Toast.makeText(this, "Excel file saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rangeDate(){
        items.clear();

        String formattedDate;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

            SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER)");
            String query = "SELECT transID, time, category, SUM(price) AS totalAmount, SUM(quantity) AS totalQuantity FROM transactions WHERE time BETWEEN " + startTimeMilli + " AND " + endTimeMilli + " GROUP BY transID";
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
                    items.add(new transItems(cursor.getString(id), "", cursor.getString(totalQuantity), cursor.getString(totalAmount), "", formattedDate));
                }
                cursor.close();
                db.close();

                transAdapter.notifyDataSetChanged();
            }
        }catch (Exception e) {
            Toast.makeText(this, "No Transactions", Toast.LENGTH_LONG).show();
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

    public void currentDate(){
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        currentDay = calendar.getTimeInMillis();
        System.out.println("Current date and time as int (set to 00:00:00): " + currentDay);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        currentDayE = calendar.getTimeInMillis();
        System.out.println("Current date and time as int (set to 23:59:59): " + currentDayE);
    }

    public void transferFirestoreToSQLite() {
        // Create or open SQLite database
        SQLiteDatabase sqliteDb = openOrCreateDatabase("TIMYC", MODE_PRIVATE, null);

        // Create transactions table if not exists
        String createTableQuery = "CREATE TABLE IF NOT EXISTS transactions " +
                "(transID INTEGER, prodName VARCHAR, quantity INTEGER, price DOUBLE, category VARCHAR, time INTEGER)";
        sqliteDb.execSQL(createTableQuery);

        // Retrieve data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionsCollection = db.collection("transactions");

        transactionsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear existing data in SQLite table
                sqliteDb.execSQL("DELETE FROM transactions");

                // Insert data into SQLite table
                for (QueryDocumentSnapshot document : task.getResult()) {
                    long transID = document.getLong("transID");
                    String prodName = document.getString("prodName");
                    String quantityStr = document.getString("quantity");
                    String priceStr = document.getString("price");
                    String category = document.getString("category");
                    long time = document.getLong("time");

                    // Parse quantity and price from string to appropriate types
                    int quantity = Integer.parseInt(quantityStr);
                    double price = Double.parseDouble(priceStr);

                    // Insert data into SQLite table
                    String insertDataQuery = "INSERT INTO transactions (transID, prodName, quantity, price, category, time) " +
                            "VALUES (" + transID + ", '" + prodName + "', " + quantity + ", " + price + ", '" + category + "', " + time + ")";
                    sqliteDb.execSQL(insertDataQuery);
                }

                // Close SQLite database
                sqliteDb.close();
            } else {
                // Handle failure, e.g., show an error message
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Call the method to fetch and display transactions again
        transferFirestoreToSQLite();
        refreshList();
    }

    @Override
    public void onBackPressed() {
    }
}