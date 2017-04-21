package com.example.android.inventoryprojectfinal;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryprojectfinal.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;

/**
 * Add Product Activity
 */
public class EditorActivity extends AppCompatActivity {

    private int REQUEST_IMAGE_CAPTURE = 1;
    byte[] image;

    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEmailEditText;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView testImage = (ImageView) findViewById(R.id.testImage);
            testImage.setImageBitmap(imageBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            image = stream.toByteArray();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.product_name);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mQuantityEditText = (EditText) findViewById(R.id.quantity);
        mSupplierEmailEditText = (EditText) findViewById(R.id.email);

        Button button = (Button) findViewById(R.id.save_product);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        // Capture Image
        Button addImage = (Button) findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        if (nameString.matches("")) {
            Toast.makeText(EditorActivity.this, "Please enter a product name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();
        if (supplierEmailString.matches("")) {
            Toast.makeText(EditorActivity.this, "Please enter a supplier email.", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceString = mPriceEditText.getText().toString().trim();
        if (priceString.matches("")) {
            Toast.makeText(EditorActivity.this, "Please enter a price per unit.", Toast.LENGTH_SHORT).show();
            return;
        }
        int priceInt = Integer.parseInt(priceString);

        String quantityString = mQuantityEditText.getText().toString().trim();
        if (quantityString.matches("")) {
            Toast.makeText(EditorActivity.this, "Please enter quantity in stock.", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantityInt = Integer.parseInt(quantityString);

        if (image == null) {
            Toast.makeText(EditorActivity.this, "Please set an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRICE, priceInt);
        values.put(ProductEntry.COLUMN_QUANTITY, quantityInt);
        values.put(ProductEntry.COLUMN_IMAGE, image);
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Error Saving Product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Product Saved Successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}