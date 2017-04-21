package com.example.android.inventoryprojectfinal;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryprojectfinal.data.ProductContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_IMAGE,
                ProductEntry.COLUMN_SUPPLIER_EMAIL};

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }


    @Override
    public void onLoadFinished (Loader < Cursor > loader, final Cursor cursor){
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        Button sale = (Button) findViewById(R.id.track_sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cursor.moveToFirst()) {
                    int productCount = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));
                    if (productCount > 0) {
                        ContentValues values = new ContentValues();
                        productCount -= 1;
                        values.put(ProductEntry.COLUMN_QUANTITY, productCount);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(Integer.toString(productCount));
                    } else {
                        Toast.makeText(DetailsActivity.this, "We're out of stock! Order Now!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button receive = (Button) findViewById(R.id.receive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cursor.moveToFirst()) {
                    int productCount = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));
                    if (productCount > 0) {
                        ContentValues values = new ContentValues();
                        productCount += 1;
                        values.put(ProductEntry.COLUMN_QUANTITY, productCount);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(Integer.toString(productCount));
                    } else {
                        Toast.makeText(DetailsActivity.this, "It's empty! Order Now!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button orderNow = (Button) findViewById(R.id.order_product);
        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "";
                if (cursor.moveToFirst()) {
                    productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_TEXT, "In need of some " + productName);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        Button deleteAll = (Button) findViewById(R.id.delete_product);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            mNameTextView = (TextView) findViewById(R.id.name_text_view);
            mPriceTextView = (TextView) findViewById(R.id.price_text_view);
            mQuantityTextView = (TextView) findViewById(R.id.quantity_text_view);
            mSupplierEmailTextView = (TextView) findViewById(R.id.email_text_view);
            mImageView = (ImageView) findViewById(R.id.image);

            // Find the columns of attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            byte[] image = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE));
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            //Set textviews
            mNameTextView.setText(name);
            mPriceTextView.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierEmailTextView.setText(supplierEmail);

            // Convert byte array to bitmap and display the image
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierEmailTextView.setText("");
        mImageView.setImageBitmap(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete?");

        builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteProduct() {
        getContentResolver().delete(mCurrentProductUri, null, null);
        Intent home = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(home);
        Toast.makeText(DetailsActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
    }
}