package com.example.miamisupermarket2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        TextView tvReceipt = findViewById(R.id.tvReceipt);
        Button btnBack = findViewById(R.id.btnBack);

        // Read the data sent from MainActivity
        Intent intent = getIntent();
        String[] names = intent.getStringArrayExtra("names");
        double[] prices = intent.getDoubleArrayExtra("prices");
        int[] quantities = intent.getIntArrayExtra("quantities");
        double[] totals = intent.getDoubleArrayExtra("totals");
        double grandTotal = intent.getDoubleExtra("grandTotal", 0);

        if (names == null || prices == null || quantities == null || totals == null) {
            tvReceipt.setText("No receipt data received.");
        } else {
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date());

            StringBuilder sb = new StringBuilder();
            sb.append("      MIAMI SUPERMARKET\n");
            sb.append("      ").append(date).append("\n");
            sb.append("--------------------------------\n");
            sb.append(String.format(Locale.US, "%-8s %5s %3s %9s\n",
                    "ITEM", "PRICE", "QTY", "TOTAL"));
            sb.append("--------------------------------\n");

            for (int i = 0; i < names.length; i++) {
                sb.append(String.format(Locale.US, "%-8s %5.0f %3d %9.2f\n",
                        names[i], prices[i], quantities[i], totals[i]));
            }

            sb.append("--------------------------------\n");
            sb.append(String.format(Locale.US, "%-18s %13.2f\n", "GRAND TOTAL", grandTotal));
            sb.append("--------------------------------\n");
            sb.append("   Thank you for shopping!\n");

            tvReceipt.setText(sb.toString());
        }

        btnBack.setOnClickListener(v -> finish());
    }
}