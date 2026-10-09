package com.example.miamisupermarket2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int ROWS = 5;

    // Product names
    // Index 0 is the default placeholder shown before the user picks anything
    String[] items = {"-- Select item --", "Milk", "Bread", "Rice", "Sugar", "Juice"};

    // Product prices (placeholder price is 0)
    double[] prices = {0, 100, 80, 200, 150, 120};

    // Dropdown labels
    String[] priceLabels;
    String[] quantities = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    // Views (one entry per row)
    Spinner[] spinnerItems = new Spinner[ROWS];
    Spinner[] spinnerPrices = new Spinner[ROWS];
    Spinner[] spinnerQuantities = new Spinner[ROWS];
    TextView[] tvTotals = new TextView[ROWS];

    TextView tvGrandTotal;
    Button btnReceipt;

    // View IDs
    int[] itemIds = {R.id.spinnerItem1, R.id.spinnerItem2, R.id.spinnerItem3,
            R.id.spinnerItem4, R.id.spinnerItem5};
    int[] priceIds = {R.id.spinnerPrice1, R.id.spinnerPrice2, R.id.spinnerPrice3,
            R.id.spinnerPrice4, R.id.spinnerPrice5};
    int[] quantityIds = {R.id.spinnerQuantity1, R.id.spinnerQuantity2, R.id.spinnerQuantity3,
            R.id.spinnerQuantity4, R.id.spinnerQuantity5};
    int[] totalIds = {R.id.tvTotal1, R.id.tvTotal2, R.id.tvTotal3,
            R.id.tvTotal4, R.id.tvTotal5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build price labels from the prices array
        priceLabels = new String[prices.length];
        for (int i = 0; i < prices.length; i++) {
            priceLabels[i] = String.valueOf((int) prices[i]);
        }

        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        btnReceipt = findViewById(R.id.btnReceipt);

        for (int i = 0; i < ROWS; i++) {
            final int row = i;

            spinnerItems[i] = findViewById(itemIds[i]);
            spinnerPrices[i] = findViewById(priceIds[i]);
            spinnerQuantities[i] = findViewById(quantityIds[i]);
            tvTotals[i] = findViewById(totalIds[i]);

            setupSpinner(spinnerItems[i], items);
            setupSpinner(spinnerPrices[i], priceLabels);
            setupSpinner(spinnerQuantities[i], quantities);

            // Every row starts on the placeholder (position 0)
            spinnerItems[i].setSelection(0);
            spinnerPrices[i].setSelection(0);

            // When the product changes, move the price dropdown to match
            spinnerItems[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerPrices[row].setSelection(position);
                    calculateTotals();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spinnerPrices[i].setOnItemSelectedListener(totalsListener);
            spinnerQuantities[i].setOnItemSelectedListener(totalsListener);
        }

        btnReceipt.setOnClickListener(v -> openReceipt());

        calculateTotals();
    }

    // Shared adapter setup for every dropdown
    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                data
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Recalculates totals whenever a price or quantity changes
    private final AdapterView.OnItemSelectedListener totalsListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    calculateTotals();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    private double getPrice(int row) {
        return prices[spinnerPrices[row].getSelectedItemPosition()];
    }

    private int getQuantity(int row) {
        return Integer.parseInt(spinnerQuantities[row].getSelectedItem().toString());
    }

    private void calculateTotals() {
        // Guard: listeners can fire before every view is set up
        for (int i = 0; i < ROWS; i++) {
            if (spinnerPrices[i] == null || spinnerQuantities[i] == null
                    || spinnerPrices[i].getSelectedItem() == null
                    || spinnerQuantities[i].getSelectedItem() == null) {
                return;
            }
        }

        double grandTotal = 0;

        for (int i = 0; i < ROWS; i++) {
            double total = getPrice(i) * getQuantity(i);
            tvTotals[i].setText(String.format("%.2f", total));
            grandTotal += total;
        }

        tvGrandTotal.setText("GRAND TOTAL: " + String.format("%.2f", grandTotal));
    }

    private void openReceipt() {
        // Count rows where a real item was chosen (position 0 is the placeholder)
        int count = 0;
        for (int i = 0; i < ROWS; i++) {
            if (spinnerItems[i].getSelectedItemPosition() > 0) {
                count++;
            }
        }

        if (count == 0) {
            Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[count];
        double[] unitPrices = new double[count];
        int[] qtys = new int[count];
        double[] totals = new double[count];
        double grandTotal = 0;

        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            if (spinnerItems[i].getSelectedItemPosition() > 0) {
                names[index] = spinnerItems[i].getSelectedItem().toString();
                unitPrices[index] = getPrice(i);
                qtys[index] = getQuantity(i);
                totals[index] = unitPrices[index] * qtys[index];
                grandTotal += totals[index];
                index++;
            }
        }

        Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
        intent.putExtra("names", names);
        intent.putExtra("prices", unitPrices);
        intent.putExtra("quantities", qtys);
        intent.putExtra("totals", totals);
        intent.putExtra("grandTotal", grandTotal);
        startActivity(intent);
    }
}