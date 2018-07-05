package com.abadarau.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.abadarau.inventoryapp.domain.InventoryDbHelper;

import static com.abadarau.inventoryapp.domain.InventoryContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 1;
    private InventoryDbHelper inventoryDbHelper;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we have a seeder for test @TODO remove
//        inventoryDbHelper = new InventoryDbHelper(this);
//        insertData();
        // @TODO untill here
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView productList = (ListView) findViewById(R.id.product_list_view);
        productAdapter = new ProductAdapter(this, null);
        productList.setAdapter(productAdapter);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void runQueryForData() {
        SQLiteDatabase database = inventoryDbHelper.getReadableDatabase();
        String[] projection = {
                ProductEntity.COLUMN_PRODUCT_NAME,
                ProductEntity.COLUMN_PRICE,
                ProductEntity.COLUMN_QUANTITY,
        };
        Cursor query = database.query(ProductEntity.TABLE_NAME, projection, null, null, null, null, null);
        Log.i("!!!!____Database Entity Count: ", String.valueOf(query.getCount()));
        StringBuilder productsLogText = new StringBuilder();
        while (query.moveToNext()) {
            productsLogText
                    .append("\n New Row :")
                    .append(" |ProductName = ")
                    .append(query.getString(query.getColumnIndex(ProductEntity.COLUMN_PRODUCT_NAME)))
                    .append(" |ProductPrice = ")
                    .append(query.getDouble(query.getColumnIndex(ProductEntity.COLUMN_PRICE)))
                    .append(" |ProductPOJO Quantity = ")
                    .append(query.getInt(query.getColumnIndex(ProductEntity.COLUMN_QUANTITY)))
            ;
        }
        Log.i("!!!!____DB Content:", productsLogText.toString());
        query.close();
    }

    private void insertData() {
        SQLiteDatabase database = inventoryDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductEntity.COLUMN_PRODUCT_NAME, "TestProduct");
        values.put(ProductEntity.COLUMN_PRICE, 22.55);
        values.put(ProductEntity.COLUMN_QUANTITY, 2);
        values.put(ProductEntity.COLUMN_SUPPLIER_NAME, "ProductsRUs");
        values.put(ProductEntity.COLUMN_SUPPLIER_PHONE_NUMBER, "+01455-235-112");

        long newRowId = database.insert(ProductEntity.TABLE_NAME, null, values);

        Log.i("!!!!!____DATABASE INSERT ID: ", String.valueOf(newRowId));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, ProductEntity.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        productAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productAdapter.swapCursor(null);
    }
}
