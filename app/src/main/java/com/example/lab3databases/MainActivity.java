package com.example.lab3databases;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView productId;
    EditText productName, productPrice;
    Button addBtn, findBtn, deleteBtn;
    ListView productListView;

    ArrayList<String> productList;
    ArrayAdapter adapter;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = new ArrayList<>();

        // info layout
        productId = findViewById(R.id.productId);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);

        //buttons
        addBtn = findViewById(R.id.addBtn);
        findBtn = findViewById(R.id.findBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // listview
        productListView = findViewById(R.id.productListView);

        // db handler
        dbHandler = new MyDBHandler(this);

        viewProducts();

        // button listeners
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString();
                double price = Double.parseDouble(productPrice.getText().toString());
                Product product = new Product(name, price);
                dbHandler.addProduct(product);

                productName.setText("");
                productPrice.setText("");

//                Toast.makeText(MainActivity.this, "Add product", Toast.LENGTH_SHORT).show();
                viewProducts();
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a product name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product query = new Product();
                query.setProductName(name);

                Product found = dbHandler.findProduct(query);
                if (found != null) {
                    productId.setText(String.valueOf(found.getId()));
                    productName.setText(found.getProductName());
                    productPrice.setText(String.valueOf(found.getProductPrice()));
                    Toast.makeText(MainActivity.this, "Found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a product name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product query = new Product();
                query.setProductName(name);

                boolean deleted = dbHandler.deleteProduct(query);
                if (deleted) {
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    productId.setText("");
                    productName.setText("");
                    productPrice.setText("");
                    viewProducts();
                } else {
                    Toast.makeText(MainActivity.this, "No match to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void viewProducts() {
        productList.clear();
        Cursor cursor = dbHandler.getData();
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "Nothing to show", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                productList.add(cursor.getString(1) + " (" +cursor.getString(2)+")");
            }
        }

        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);
    }
}

