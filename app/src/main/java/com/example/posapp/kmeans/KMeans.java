package com.example.posapp.kmeans;

import static java.lang.Math.round;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.posapp.MainScreen;
import com.example.posapp.R;
import com.example.posapp.transactions.manageTransactions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class KMeans extends AppCompatActivity {
    Button btnBack;
    private TextView textViewResult;
    BarChart barChart;
    EditText txtStartDate, txtEndDate;
    long currentDay, currentDayE;
    ArrayList<Long> days = new ArrayList<Long>();//0 day to Current Day
    long startDate, endDate;
    int ctr = 0;
    BarData barData;
    DatePickerDialog datePickerDialog;

    BarDataSet barDataSet;

    ArrayList barEntriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmeans_ui);

        textViewResult = findViewById(R.id.textViewResult);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnBack = findViewById(R.id.btnBack);
        barChart = findViewById(R.id.barChart);
        barChart.setEnabled(false);
        barChart.setClickable(false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(KMeans.this, MainScreen.class);
                startActivity(i);
            }
        });

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePicker();
                if(!txtEndDate.getText().toString().isEmpty()){
                    runKMeans();
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
                datePickerDialog = new DatePickerDialog(KMeans.this, new DatePickerDialog.OnDateSetListener() {
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

                        endDate = selectedCalendar.getTimeInMillis();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        txtEndDate.setText(dateFormat.format(selectedCalendar.getTime()));
                        runKMeans();
                    }
                }, mYear2, mMonth2, mDay2);
                datePickerDialog.getDatePicker().setMinDate(startDate);

                datePickerDialog.show();
            }
        });

        getDates();

        //Autostart kmeans
        ctr = 12;
        runKMeans();
        getDailyTotal();
    }

    private void runKMeans() {
        int k = 1;
        barEntriesArrayList = new ArrayList<>();
        // Call your k-means clustering method with the provided K value
        List<Transaction> transactions = createDummyData();

        // Check if transactions are available
        if (transactions == null || transactions.isEmpty()) {
            textViewResult.setText("No transactions today for clustering");
            return;
        }

        List<Cluster> clusters = performKMeans(k);
        StringBuilder resultText = new StringBuilder("K-Means Result:\n");
        for (int i = 0; i < k; i++) {
            Cluster cluster = clusters.get(i);
            Context context = this;

            Date date1 = new Date(startDate);
            Date date2 = new Date(endDate);

            SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy, E", Locale.getDefault());
            String formattedDate = df.format(date1);
            String formattedDate2 = df.format(date2);


            // Append information about the cluster to the resultText
//          resultText.append("Cluster ").append(i + 1).append("\n"); FOR CLUSTER!!!
            if (formattedDate.equals(formattedDate2)){
                resultText.append("Date: ").append(formattedDate).append("\n");
            }else if(!txtEndDate.getText().toString().trim().equals("")){
                resultText.append("Date: ").append(formattedDate).append(" to ").append(formattedDate2).append("\n");
            }else{
                resultText.append("Date: ").append(formattedDate).append("\n");
            }
//          resultText.append("Centroid: ").append(Arrays.toString(cluster.getCentroid())).append("\n"); FOR CENTROID!!!
            resultText.append("Total Items Bought: ").append(cluster.getTotalQuantity()).append("\n");
            resultText.append("Total Sales: ").append(cluster.getTotalPrice() + "0").append("\n");
            resultText.append("Compatible Products: ").append(cluster.getMostBoughtProducts(context)).append("\n");
            resultText.append("\n");
        }

        textViewResult.setText(resultText.toString());
    }

    private List<Cluster> performKMeans(int k) {
        KMeans kMeans = new KMeans();
        List<Transaction> transactions = createDummyData();
        List<Cluster> clusters = kMeans.predict(k, transactions);

        // Print the results in the log for reference
        for (int i = 0; i < k; i++) {
            Cluster cluster = clusters.get(i);
            Context context = this;
            float averageQuantity = cluster.getTotalQuantity();
            String mostBoughtProduct = String.valueOf(cluster.getMostBoughtProducts(context));

            System.out.println("Cluster " + i);
            System.out.println("Centroid: " + Arrays.toString(cluster.getCentroid()));
            System.out.println("Total Items Bought: " + averageQuantity);
            System.out.println("Total Sale for Day " + i + " " + cluster.getTotalPrice());
            System.out.println("Compatible Products: " + mostBoughtProduct);
            System.out.println("Assigned Transactions: " + cluster.getTransactions());
            System.out.println("\n");
        }
        textViewResult.setText("K-Means Result:" + clusters.toString());
        return clusters;
    }

    private static final int MAX_ITERATIONS = 30;
    private static final double CONVERGENCE_EPSILON = 0.005;

    private final Random randomState;

    public KMeans() {
        this(new Random());
    }

    public KMeans(Random random) {
        this.randomState = random;
    }

    public List<Cluster> predict(final int k, final List<Transaction> transactions) {
        checkDataSetSanity(transactions);
        int dimension = 3;
        final List<Cluster> clusters = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            Cluster cluster = new Cluster(dimension);
            for (int j = 0; j < dimension; j++) {
                // Initialize centroids with random values
                cluster.getCentroid()[j] = randomState.nextFloat();
            }
            clusters.add(cluster);
        }

        // Iterate until we converge or run out of iterations
        boolean converged = false;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            converged = step(clusters, transactions);
            if (converged) {
                System.out.println("Converged at iteration: " + i);
                break;
            }
        }

        if (!converged) {
            System.out.println("Did not converge");
        }
        return clusters;
    }

    private void checkDataSetSanity(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transaction list is empty or null.");
        }
    }

    private boolean step(final List<Cluster> clusters, final List<Transaction> transactions) {
        // Clean up the previous state
        for (Cluster cluster : clusters) {
            cluster.getTransactions().clear();
        }
        // Assign each transaction to the nearest cluster
        for (Transaction transaction : transactions) {
            final Cluster nearest = nearestCluster(transaction, clusters);
            nearest.getTransactions().add(transaction);
        }

        boolean converged = true;
        // Move each cluster towards the mean of its assigned transactions
        for (Cluster cluster : clusters) {
            List<Transaction> assignedTransactions = cluster.getTransactions();
            if (!assignedTransactions.isEmpty()) {
                float[] oldCentroid = Arrays.copyOf(cluster.getCentroid(), cluster.getCentroid().length);
                // Compute the new cluster centroid
                for (int j = 0; j < cluster.getCentroid().length; j++) {
                    float sum = 0;
                    for (Transaction transaction : assignedTransactions) {
                        if (j == 0) {
                            sum += transaction.getProductName().hashCode(); // Use hash code for simplicity
                        } else {
                            sum += transaction.getQuantity();
                        }
                    }
                    cluster.getCentroid()[j] = sum / assignedTransactions.size();
                }
                // Check for convergence
                float sumSquaredDistances = 0;
                for (int j = 0; j < oldCentroid.length; j++) {
                    sumSquaredDistances += Math.pow(oldCentroid[j] - cluster.getCentroid()[j], 2);
                }

                if (sumSquaredDistances > CONVERGENCE_EPSILON) {
                    converged = false;
                }
            }
        }
        return converged;
    }

    private Cluster nearestCluster(Transaction transaction, List<Cluster> clusters) {
        Cluster nearest = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Cluster next : clusters) {
            float nextDistance = sqDistance(transaction, next);
            if (nextDistance < nearestDistance) {
                nearest = next;
                nearestDistance = nextDistance;
            }
        }

        return nearest;
    }

    private float sqDistance(Transaction transaction, Cluster cluster) {
        float dist = 0;
        float[] centroid = cluster.getCentroid();
        // Hash Map
        dist += Math.pow(transaction.getProductName().hashCode() - centroid[0], 2);
        dist += Math.pow(transaction.getQuantity() - centroid[1], 2);
        dist += Math.pow(transaction.getPrice() - centroid[2], 2);

        return dist;
    }

    public static class Cluster {
        private float[] centroid;
        private List<Transaction> transactions;

        public Cluster(int dimension) {
            this.centroid = new float[dimension];
            this.transactions = new ArrayList<>();
        }

        public float[] getCentroid() {
            return centroid;
        }

        public float getTotalPrice() {
            if (transactions.isEmpty()) {
                return 0.00f;
            }

            double totalPrice = 0;
            for (Transaction transaction : transactions) {
                totalPrice += transaction.getPrice();
            }

            return (float) totalPrice;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        // Inside the Cluster class
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("\n\nCluster{Centroid=").append(Arrays.toString(centroid)).append(", Transactions=[");

            for (Transaction transaction : transactions) {
                builder.append(transaction).append(", ");
            }

            if (!transactions.isEmpty()) {
                builder.setLength(builder.length() - 2);
            }

            builder.append("]}");

            return builder.toString();
        }

        public int getTotalQuantity() {
            if (transactions.isEmpty()) {
                return 0; // Return 0 if there are no transactions in the cluster
            }

            float totalQuantity = 0;
            for (Transaction transaction : transactions) {
                totalQuantity += transaction.getQuantity();
            }

            return Math.round(totalQuantity);
        }

        public List<String> getMostBoughtProducts(Context context) {
            Map<String, Integer> productCounts = new HashMap<>();

            // Count the occurrences of each product in the cluster
            for (Transaction transaction : transactions) {
                String productName = transaction.getProductName();
                productCounts.put(productName, productCounts.getOrDefault(productName, 0) + 1);
            }

            // Find the minimum count (least bought product)
            int minCount = Integer.MAX_VALUE; // Initialize with a large value

            for (int count : productCounts.values()) {
                if (count < minCount) {
                    minCount = count;
                }
            }

            // Find all products with the minimum count (least bought products)
            List<String> leastBoughtProducts = new ArrayList<>();

            int i = 0;
            for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                if (entry.getValue() == minCount) {
                    if (i != 2) {
                        leastBoughtProducts.add(entry.getKey());
                        i++;
                    }
                }
            }

            if (leastBoughtProducts.size() == 1) {
                String existingProduct = leastBoughtProducts.get(0);
                String randomProduct = getRandomProductFromDatabase(context);

                // Ensure that the random product is not the same as the existing product
                while (randomProduct.equals(existingProduct)) {
                    randomProduct = getRandomProductFromDatabase(context);
                }

                leastBoughtProducts.add(randomProduct);
            }

            if (leastBoughtProducts.size() > 2) {
                leastBoughtProducts = leastBoughtProducts.subList(0, 2);
            }

            return leastBoughtProducts;
        }

        @SuppressLint("Range")
        private static String getRandomProductFromDatabase(Context context) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            String randomProduct = null;

            try {
                // Use the provided context to open or create the database
                db = context.openOrCreateDatabase("TIMYC", MODE_PRIVATE, null);
                String query = "SELECT product FROM products ORDER BY RANDOM() LIMIT 1";

                cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("product");
                    if (columnIndex != -1) {
                        randomProduct = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return randomProduct;
        }
    }

    public static class Transaction {
        private String productName;
        private float quantity;
        private double price;


        public Transaction(String productName, float quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public float getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }


        @Override
        public String toString() {
            return "{ Product='" + productName + "', Quantity=" + round(quantity) +
                    ", Price=" + price + " }";
        }
    }

    private List<Transaction> createDummyData() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

        String query = "SELECT * FROM transactions WHERE time BETWEEN " + startDate + " AND " + endDate;
        Cursor cursor = db.rawQuery(query, null);

        int prodName = cursor.getColumnIndex("prodName");
        int quantity = cursor.getColumnIndex("quantity");
        int priceIndex = cursor.getColumnIndex("price");

        while(cursor.moveToNext()) {
            transactions.add(new Transaction(cursor.getString(prodName), cursor.getInt(quantity), cursor.getDouble(priceIndex)));
        }

        db.close();
        return transactions;
    }

    public void getDates(){
        getCurrentDate();

    }
    public void getCurrentDate(){
        // Get the current date
        Date currentDate = new Date();

        // Convert the current date to Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        currentDay = calendar.getTimeInMillis();
        startDate = currentDay;
        System.out.println("Current date and time as int (set to 00:00:00): " + currentDay);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        currentDayE = calendar.getTimeInMillis();
        endDate = currentDayE;
        System.out.println("Current date and time as int (set to 23:59:59): " + currentDayE);

        currentDay = currentDay - 518400000;
        currentDayE = currentDayE - 518400000;//-6 days from current
        for(int i = 0; i < 7; i++){
            long newTimeE = currentDayE + (86400000 * i);
            long newTime = currentDay + (86400000 * i);

            days.add(newTime);
            days.add(newTimeE);
        }

        for (int i = 0; i < days.size(); i += 2) {
            System.out.println("Day " + (i / 2 + 1) + " Start: " + new Date(days.get(i)));
            System.out.println("Day " + (i / 2 + 1) + " End: " + new Date(days.get(i + 1)));
        }
    }

    public void getDailyTotal(){
        Date currentDate = new Date();
        SQLiteDatabase db = openOrCreateDatabase("TIMYC", Context.MODE_PRIVATE, null);

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

        currentDay = currentDay - 518400000;
        currentDayE = currentDayE - 518400000;//-6 days from current
        for (int j = 0; j < 7; j++) {
            double totalPrice = 0;

            // iterate through the date range for each day
            long startDate = days.get(j * 2);
            long endDate = days.get(j * 2 + 1);

            String query = "SELECT * FROM transactions WHERE time BETWEEN " + startDate + " AND " + endDate;
            Cursor cursor = db.rawQuery(query, null);
            int priceIndex = cursor.getColumnIndex("price");

            if (cursor.moveToFirst()) {
                do {
                    totalPrice += cursor.getDouble(priceIndex);
                } while (cursor.moveToNext());
            }

            cursor.close(); // Close the cursor after retrieving data

            barEntriesArrayList.add(new BarEntry(j+1, (float) totalPrice));
            System.out.println("Day " + j + ": " + totalPrice);
        }
        barDataSet = new BarDataSet(barEntriesArrayList, "Daily Income");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barChart.getDescription().setEnabled(false);
    }

    private void showStartDatePicker(){
        final Calendar c = Calendar.getInstance();
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(KMeans.this, new DatePickerDialog.OnDateSetListener() {
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

                startDate = selectedCalendar.getTimeInMillis();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(selectedCalendar.getTime());

                txtStartDate.setText(formattedDate);
                if(!txtEndDate.getText().toString().isEmpty()){
                    if(startDate > endDate){
                        txtEndDate.setText(formattedDate);
                        runKMeans();
                    }else{
                        runKMeans();
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

    @Override
    public void onBackPressed() {
    }

}