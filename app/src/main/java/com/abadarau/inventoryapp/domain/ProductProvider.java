package com.abadarau.inventoryapp.domain;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProductProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String ERR_BAD_Q_PARAMS = "Invalid query params";
    public static final String ERR_BAD_MATCH = "No match from URI: ";
    public static final String ERR_BAD_INSERT_URI = "Insert Not Supported for URI: ";
    public static final String ERR_REQ_FIELD_EMPTY = " Field is required !";
    public static final String ERR_FIELD_GT = " Filed has to be grater than ";
    public static final String ERR_DB_INSERT = "Failed to insert values";
    public static final String ERR_NO_MASS_ACTION = "You can't do mass actions on the database you hacker you";
    public static final String ERR_NO_ARGS = "Please provide valid args";
    private InventoryDbHelper dbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    static {
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PRODUCTS_PATH, PRODUCTS);
        URI_MATCHER.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PRODUCTS_PATH + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case PRODUCTS:
                cursor = database.query(InventoryContract.ProductEntity.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntity.ID + "= ?";
                cursor = database.query(InventoryContract.ProductEntity.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(ERR_BAD_Q_PARAMS);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PRODUCTS:
                return InventoryContract.ProductEntity.CONTENT_LIST;
            case PRODUCT_ID:
                return InventoryContract.ProductEntity.CONTENT_ITEM;
            default:
                throw new IllegalArgumentException(ERR_BAD_MATCH + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case PRODUCTS:
                validateData(values);
                long id = dbHelper.getWritableDatabase().insert(InventoryContract.ProductEntity.TABLE_NAME, null, values);
                if (id == -1) {
                    throw new IllegalArgumentException(ERR_DB_INSERT);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException(ERR_BAD_INSERT_URI + uri.toString());
        }
    }

    private void validateData(ContentValues values) {
        if(values.size() <= 0){
            throw new IllegalArgumentException(ERR_NO_ARGS);
        }

        String prodName = values.getAsString(InventoryContract.ProductEntity.COLUMN_PRODUCT_NAME);
        if (prodName == null) {
            throw new IllegalArgumentException(InventoryContract.ProductEntity.COLUMN_PRODUCT_NAME + ERR_REQ_FIELD_EMPTY);
        }
        Integer prodQty = values.getAsInteger(InventoryContract.ProductEntity.COLUMN_QUANTITY);
        if (prodQty == null || prodQty < 0) {
            throw new IllegalArgumentException(InventoryContract.ProductEntity.COLUMN_QUANTITY + ERR_FIELD_GT);
        }
        Double prodPrice = values.getAsDouble(InventoryContract.ProductEntity.COLUMN_PRICE);
        if (prodPrice == null || prodPrice < 0) {
            throw new IllegalArgumentException(InventoryContract.ProductEntity.COLUMN_PRICE + ERR_FIELD_GT);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case PRODUCTS:
                throw new IllegalArgumentException(ERR_NO_MASS_ACTION);
            case PRODUCT_ID:
                int deleted = dbHelper.getWritableDatabase().delete(
                        InventoryContract.ProductEntity.TABLE_NAME,
                        InventoryContract.ProductEntity.ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                if (deleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return deleted;
            default:
                throw new IllegalArgumentException(ERR_BAD_MATCH + uri.toString());
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case PRODUCTS:
                throw new IllegalArgumentException(ERR_NO_MASS_ACTION);
            case PRODUCT_ID:
                validateData(values);
                int updated = dbHelper.getWritableDatabase().update(
                        InventoryContract.ProductEntity.TABLE_NAME,
                        values,
                        InventoryContract.ProductEntity.ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}

                );
                if (updated > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return updated;
            default:
                throw new IllegalArgumentException(ERR_BAD_MATCH + uri.toString());
        }
    }

    public static ProductPOJO getFromCursor(Cursor cursor){
        ProductPOJO item = new ProductPOJO();
        item.setId(
                cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntity.ID))
        );
        item.setName(
                cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntity.COLUMN_PRODUCT_NAME))
        );
        item.setQty(
                cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntity.COLUMN_QUANTITY))
        );
        item.setPrice(
                cursor.getDouble(cursor.getColumnIndex(InventoryContract.ProductEntity.COLUMN_PRICE))
        );
        item.setSupplierName(
                cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntity.COLUMN_SUPPLIER_NAME))
        );
        item.setSupplierPhone(
                cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntity.COLUMN_SUPPLIER_PHONE_NUMBER))
        );

        return item;
    }

    public static ContentValues getPojoUpdateValues(ProductPOJO product){
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntity.ID, product.getId());
        values.put(InventoryContract.ProductEntity.COLUMN_PRODUCT_NAME, product.getName());
        values.put(InventoryContract.ProductEntity.COLUMN_QUANTITY, product.getQty());
        values.put(InventoryContract.ProductEntity.COLUMN_PRICE, product.getPrice());
        values.put(InventoryContract.ProductEntity.COLUMN_SUPPLIER_NAME, product.getSupplierName());
        values.put(InventoryContract.ProductEntity.COLUMN_SUPPLIER_PHONE_NUMBER, product.getSupplierPhone());
        return values;
    }
}
