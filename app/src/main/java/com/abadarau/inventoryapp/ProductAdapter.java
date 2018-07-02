package com.abadarau.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.abadarau.inventoryapp.domain.InventoryContract;
import com.abadarau.inventoryapp.domain.ProductPOJO;
import com.abadarau.inventoryapp.domain.ProductProvider;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends CursorAdapter {
    public ProductAdapter(Context context, Cursor c) {
        super(context, c, R.layout.product_list_item);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        final ProductPOJO product = ProductProvider.getFromCursor(cursor);
        TextView productNameTv = (TextView) view.findViewById(R.id.product_name_tv);
        productNameTv.setText(product.getName());
        ((TextView) view.findViewById(R.id.product_qty_tv)).setText(product.getQty());
        ((TextView) view.findViewById(R.id.product_price_tv)).setText(String.format(Locale.GERMAN, "%.2f", product.getPrice()));

        ((Button) view.findViewById(R.id.buy_product_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri updateUri = ContentUris.withAppendedId(InventoryContract.ProductEntity.CONTENT_URI, product.getId());
                Integer qty = ((product.getQty() - 1) > 0) ? product.getQty() - 1 : 0;
                ContentValues values = new ContentValues();
                values.put(InventoryContract.ProductEntity.COLUMN_QUANTITY, qty);
                int updated = context.getContentResolver().update(updateUri, values, null, null);
                if (updated > 0) {
                    Snackbar.make(view, R.string.prod_sell_ok, Snackbar.LENGTH_LONG).show();
                    ((TextView) view.findViewById(R.id.product_price_tv)).setText(String.format(Locale.GERMAN, "%.2f", product.getPrice()));
                } else {
                    Snackbar.make(view, R.string.qty_neg_err, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


}
