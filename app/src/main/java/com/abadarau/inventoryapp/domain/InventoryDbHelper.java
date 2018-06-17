package com.abadarau.inventoryapp.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE_SQL = "CREATE TABLE " + InventoryContract.ProductEntity.TABLE_NAME + " ( " +
                InventoryContract.ProductEntity.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + InventoryContract.ProductEntity.COLUMN_PRODUCT_NAME + " TEXT NOT NULL" +
                ", " + InventoryContract.ProductEntity.COLUMN_PRICE + " REAL NOT NULL" +
                ", " + InventoryContract.ProductEntity.COLUMN_QUANTITY + " INTEGER NOT NULL" +
                ", " + InventoryContract.ProductEntity.COLUMN_SUPPLIER_NAME + " TEXT" +
                ", " + InventoryContract.ProductEntity.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT" +
                ");";

        db.execSQL(CREATE_PRODUCTS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
