package com.example.posapp.kmeans;

import static java.lang.Math.round;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.posapp.MainScreen;
import com.example.posapp.R;

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

    private EditText textViewResult;
    private Button btnBack, btnDay0, btnDay1, btnDay2, btnDay3, btnDay4, btnDay5, btnDay6, btnPast;
    long currentDay, currentDayE;
    ArrayList<Long> days = new ArrayList<Long>();//0 day to 7th day
    long startDate, endDate;
    int ctr = 0;
    boolean past7Days = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmeans_ui);

        // Initialize UI components
        textViewResult = findViewById(R.id.textViewResult);
        btnBack = findViewById(R.id.btnBack);
        btnDay0 = findViewById(R.id.btnDay0);
        btnDay1 = findViewById(R.id.btnDay1);
        btnDay2 = findViewById(R.id.btnDay2);
        btnDay3 = findViewById(R.id.btnDay3);
        btnDay4 = findViewById(R.id.btnDay4);
        btnDay5 = findViewById(R.id.btnDay5);
        btnDay6 = findViewById(R.id.btnDay6);
        btnPast = findViewById(R.id.btnPast);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(KMeans.this, MainScreen.class);
                startActivity(i);
            }
        });

        btnDay0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 0;
                runKMeans();
            }
        });

        btnDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 2;
                runKMeans();
            }
        });

        btnDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 4;
                runKMeans();
            }
        });

        btnDay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 6;
                runKMeans();
            }
        });

        btnDay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 8;
                runKMeans();
            }
        });

        btnDay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 10;
                runKMeans();
            }
        });

        btnDay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr = 12;
                runKMeans();
            }
        });

        btnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                past7Days = true;
                runKMeans();
            }
        });

        getDates();

        //Autostart kmeans
        ctr = 12;
        runKMeans();
    }

    private void runKMeans() {
        int k = 1;
        if(past7Days == true){
            k = 7;
        }
        // Call your k-means clustering method with the provided K value
        List<Transaction> transactions = createDummyData();

        // Check if transactions are available
        if (transactions == null || transactions.isEmpty()) {
            // Handle the case where transactions are null or empty
            textViewResult.setText("Error: No transactions available for clustering.");
            return;
        }

        List<Cluster> clusters = performKMeans(k);
        StringBuilder resultText = new StringBuilder("K-Means Result:\n");
        if(past7Days == false){
            for (int i = 0; i < k; i++) {
                Cluster cluster = clusters.get(i);

                SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
                String formattedDate = df.format(days.get(ctr));

                // Append information about the cluster to the resultText
//                resultText.append("Cluster ").append(i + 1).append("\n");
                resultText.append("Date: ").append(formattedDate).append("\n");
                resultText.append("Centroid: ").append(Arrays.toString(cluster.getCentroid())).append("\n");
                resultText.append("Average Quantity: ").append(cluster.getAverageQuantity()).append("\n");
                resultText.append("Average Price: ").append(cluster.getAveragePrice()).append("\n");
                resultText.append("Most Bought Product(s): ").append(cluster.getMostBoughtProducts()).append("\n");
                resultText.append("\n");
            }
        }else{
            for (int i = 0; i < k; i++) {
                Cluster cluster = clusters.get(i);

                SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
                String formattedDate = df.format(days.get(i*2));

                // Append information about the cluster to the resultText
                resultText.append("Cluster ").append(i + 1).append("\n");
                resultText.append("Date: ").append(formattedDate).append("\n");
                resultText.append("Centroid: ").append(Arrays.toString(cluster.getCentroid())).append("\n");
                resultText.append("Average Quantity: ").append(cluster.getAverageQuantity()).append("\n");
                resultText.append("Average Price: ").append(cluster.getAveragePrice()).append("\n");
                resultText.append("Most Bought Product(s): ").append(cluster.getMostBoughtProducts()).append("\n");
                resultText.append("\n");
            }
        }

        textViewResult.setText(resultText.toString());
        past7Days = false;
    }

    private List<Cluster> performKMeans(int k) {
        KMeans kMeans = new KMeans();
        List<Transaction> transactions = createDummyData();
        List<Cluster> clusters = kMeans.predict(k, transactions);

        // Print the results
        for (int i = 0; i < k; i++) {
            Cluster cluster = clusters.get(i);
            float averageQuantity = cluster.getAverageQuantity();
            String mostBoughtProduct = String.valueOf(cluster.getMostBoughtProducts());

            System.out.println("Cluster " + i);
            System.out.println("Centroid: " + Arrays.toString(cluster.getCentroid()));
            System.out.println("Average Quantity: " + averageQuantity);
            System.out.println("Most Bought Product: " + mostBoughtProduct);
            System.out.println("Assigned Transactions: " + cluster.getTransactions());
            System.out.println();
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
        int dimension = 4; // Assuming 2 dimensions: product name and quantity
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
        // Use hash code for product name for simplicity
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

        public float getAveragePrice() {
            if (transactions.isEmpty()) {
                return 0.0f; // Return 0 if there are no transactions in the cluster
            }

            double totalPrice = 0;
            for (Transaction transaction : transactions) {
                totalPrice += transaction.getPrice();
            }

            return (float) (totalPrice / transactions.size());
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

            // Remove the trailing comma and space if there are transactions
            if (!transactions.isEmpty()) {
                builder.setLength(builder.length() - 2);
            }

            builder.append("]}");

            return builder.toString();
        }

        public float getAverageQuantity() {
            if (transactions.isEmpty()) {
                return 0.0f; // Return 0 if there are no transactions in the cluster
            }
            float totalQuantity = 0;
            for (Transaction transaction : transactions) {
                totalQuantity += transaction.getQuantity();
            }
            return totalQuantity / transactions.size();
        }
        @SuppressLint("NewApi")
        public List<String> getMostBoughtProducts() {
            Map<String, Integer> productCounts = new HashMap<>();

            // Count the occurrences of each product in the cluster
            for (Transaction transaction : transactions) {
                String productName = transaction.getProductName();
                productCounts.put(productName, productCounts.getOrDefault(productName, 0) + 1);
            }

            // Find the maximum count
            int maxCount = 0;

            for (int count : productCounts.values()) {
                if (count > maxCount) {
                    maxCount = count;
                }
            }

            // Find all products with the maximum count
            List<String> mostBoughtProducts = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                if (entry.getValue() == maxCount) {
                    mostBoughtProducts.add(entry.getKey());
                }
            }
            return mostBoughtProducts;
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

        if(past7Days == false){
            startDate = days.get(ctr);
            endDate = days.get(ctr+1);
            String query = "SELECT * FROM transactions WHERE time BETWEEN " + startDate + " AND " + endDate;
            Cursor cursor = db.rawQuery(query, null);

            int prodName = cursor.getColumnIndex("prodName");
            int quantity = cursor.getColumnIndex("quantity");
            int priceIndex = cursor.getColumnIndex("price");

            while(cursor.moveToNext()) {
                transactions.add(new Transaction(cursor.getString(prodName), cursor.getInt(quantity), cursor.getDouble(priceIndex)));
            }
        }else{
            String query = "SELECT * FROM transactions WHERE time BETWEEN " + days.get(0) + " AND " + days.get(13);
            Cursor cursor = db.rawQuery(query, null);

            int prodName = cursor.getColumnIndex("prodName");
            int quantity = cursor.getColumnIndex("quantity");
            int priceIndex = cursor.getColumnIndex("price");

            while(cursor.moveToNext()) {
                transactions.add(new Transaction(cursor.getString(prodName), cursor.getInt(quantity), cursor.getDouble(priceIndex)));
            }
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
        System.out.println("Current date and time as int (set to 00:00:00): " + currentDay);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        currentDayE = calendar.getTimeInMillis();
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
}