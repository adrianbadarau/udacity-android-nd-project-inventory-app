package com.abadarau.inventoryapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abadarau.inventoryapp.domain.InventoryContract;
import com.abadarau.inventoryapp.domain.ProductPOJO;
import com.abadarau.inventoryapp.domain.ProductProvider;

import java.util.Locale;

public class ManageProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 1;
    private Uri productUri;
    private TextView productNameTV;
    private TextView productQtyTV;
    private TextView productPriceTV;
    private TextView productSupplierNameTV;
    private TextView productSupplierPhoneTV;
    private Boolean dirty = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            dirty = true;
            return false;
        }
    };
    private ProductPOJO product = new ProductPOJO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        Intent intent = getIntent();
        productUri = intent.getData();
        setTitle(R.string.prod_create_title);
        if (productUri != null) {
            setTitle(R.string.prod_edit_title);
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }


        productNameTV = ((TextView) findViewById(R.id.product_name_tv));
        productNameTV.setOnTouchListener(touchListener);
        productQtyTV = ((TextView) findViewById(R.id.product_qty_tv));
        productQtyTV.setOnTouchListener(touchListener);
        productPriceTV = ((TextView) findViewById(R.id.product_price_tv));
        productPriceTV.setOnTouchListener(touchListener);
        productSupplierNameTV = ((TextView) findViewById(R.id.product_supplier_name_tv));
        productSupplierNameTV.setOnTouchListener(touchListener);
        productSupplierPhoneTV = ((TextView) findViewById(R.id.product_supplier_phone_tv));
        productSupplierPhoneTV.setOnTouchListener(touchListener);

        Button saveBTN = (Button) findViewById(R.id.prod_save_btn);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    product.setName(
                            productNameTV.getText().toString()
                    );
                    product.setQty(
                            Integer.valueOf(productQtyTV.getText().toString())
                    );
                    product.setPrice(
                            Double.parseDouble(productPriceTV.getText().toString().replace(",", "."))
                    );
                    product.setSupplierName(
                            productSupplierNameTV.getText().toString()
                    );
                    product.setSupplierPhone(
                            productSupplierPhoneTV.getText().toString()
                    );
                    boolean saved = false;
                    if (productUri == null) {
                        Uri inserted = getContentResolver().insert(InventoryContract.ProductEntity.CONTENT_URI, ProductProvider.getPojoUpdateValues(product));
                        if (inserted != null) {
                            saved = true;
                        }
                    } else {
                        int updated = getContentResolver().update(productUri, ProductProvider.getPojoUpdateValues(product), null, null);
                        if (updated > 0) {
                            saved = true;
                        }
                    }

                    if (saved) {
                        Toast.makeText(getBaseContext(), R.string.prod_save_succ, Toast.LENGTH_LONG).show();
                        dirty = false;
                    } else {
                        Toast.makeText(getBaseContext(), R.string.err_save, Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        ((Button) findViewById(R.id.product_add_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer qty = Integer.valueOf(productQtyTV.getText().toString());
                product.setQty(++qty);
                productQtyTV.setText(qty.toString());
            }
        });
        ((Button) findViewById(R.id.product_remove_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer qty = Integer.valueOf(productQtyTV.getText().toString());
                if (qty <= 0) {
                    qty = 0;
                } else {
                    --qty;
                }
                product.setQty(qty);
                productQtyTV.setText(qty.toString());
            }
        });
    }

    private boolean validateInput() {
        boolean valid = true;
        if (productNameTV.getText().toString().isEmpty() || productPriceTV.getText().toString().isEmpty() || productQtyTV.getText().toString().isEmpty()) {
            valid = false;
            Toast.makeText(this, R.string.err_empty_req_fields, Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, productUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            product = ProductProvider.getFromCursor(data);
            productNameTV.setText(product.getName());
            productPriceTV.setText(String.format(Locale.GERMAN, "%.2f", product.getPrice()));
            productQtyTV.setText(String.format(Locale.GERMAN, "%d", product.getQty()));
            productSupplierNameTV.setText(product.getSupplierName());
            productSupplierPhoneTV.setText(product.getSupplierPhone());

            Button deleteProdBTN = (Button) findViewById(R.id.delete_prod_btn);
            deleteProdBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            Button callSupplierBTN = (Button) findViewById(R.id.call_supplier_btn);
            callSupplierBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + product.getSupplierPhone()));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        if (ActivityCompat.checkSelfPermission(ManageProductActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getBaseContext(), R.string.err_no_call_perm, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (productUri != null) {
                    int deleted = getContentResolver().delete(productUri, null, null);
                    if (deleted > 0) {
                        Toast.makeText(getBaseContext(), R.string.prod_del_succ, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), R.string.prod_del_err, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productNameTV.setText("");
        productPriceTV.setText("");
        productQtyTV.setText("");
        productSupplierNameTV.setText("");
        productSupplierPhoneTV.setText("");
    }

    @Override
    public void onBackPressed() {
        if (!dirty) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.back_confirm_msg);
        builder.setPositiveButton(R.string.back_yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.back_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
