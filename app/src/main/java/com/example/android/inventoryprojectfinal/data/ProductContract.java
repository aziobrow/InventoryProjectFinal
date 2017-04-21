package com.example.android.inventoryprojectfinal.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory App
 */

public class ProductContract {

    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the inventory database table
     * Each entry in the table will represent a single product
     */
    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Information contained within the database table:
         */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID for each product
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of each product
         * Type: TEXT
         */
        public final static String COLUMN_NAME = "name";

        /**
         * Quantity in stock of each product
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Price for one unit of each product
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Photo representing each product
         * Type: TEXT
         */
        public final static String COLUMN_IMAGE = "image";

        /**
         * Email to contact supplier
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_EMAIL = "email";
    }
}