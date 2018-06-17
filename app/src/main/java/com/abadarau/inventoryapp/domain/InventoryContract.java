package com.abadarau.inventoryapp.domain;

import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    public static final class ProductEntity implements BaseColumns{
        public static final String TABLE_NAME = "products";
        public static final String ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "prduct_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
