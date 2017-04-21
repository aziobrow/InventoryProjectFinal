package com.example.android.inventoryprojectfinal;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryprojectfinal.data.ProductContract.ProductEntry;

/**
 * This adapter knows how to create list items for each row of product data in the cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
        final Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

        // Find individual views that we want to modify in the list item layout
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);

        // Read the attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        Button listviewSale = (Button) view.findViewById(R.id.listview_track_sale);
        listviewSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String count = quantityTextView.getText().toString();
                int productCount = Integer.parseInt(count);
                ContentValues values = new ContentValues();
                if (productCount > 0) {
                    productCount -= 1;
                    values.put(ProductEntry.COLUMN_QUANTITY, productCount);
                    context.getContentResolver().update(currentProductUri, values, null, null);
                    quantityTextView.setText(String.valueOf(productCount));
                } else {
                    Toast.makeText(context, "It's empty! Order Now!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
