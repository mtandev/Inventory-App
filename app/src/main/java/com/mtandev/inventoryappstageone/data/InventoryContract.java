package com.mtandev.inventoryappstageone.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.mtandev.inventoryappstageone";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "product";

    public InventoryContract () {

    }

    public final static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +  PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "product";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "item_name";
        public final static String COLUMN_ITEM_PRICE = "item_price";
        public final static String COLUMN_ITEM_QUANTITY = "item_quantity";
        public final static String COLUMN_ITEM_SUPPLIER_NAME = "item_supplier";
        public final static String COLUMN_ITEM_SUPPLIER_PHONE_NUMBER = "supplier_number";


        public final static int SUPPLIER_AMAZON = 0;
        public final static int SUPPLIER_COSTCO = 1;
        public final static int SUPPLIER_TARGET = 2;
        public final static int SUPPLIER_TRADERJOES = 3;
        public final static int SUPPLIER_UNKNOWN = 4;

        public static boolean isValidSupplierName(int suppliername) {
            if (suppliername == SUPPLIER_UNKNOWN || suppliername == SUPPLIER_AMAZON || suppliername == SUPPLIER_COSTCO || suppliername == SUPPLIER_TARGET || suppliername == SUPPLIER_TRADERJOES) {
                return true;
            }

            return false;
        }
    }
}
