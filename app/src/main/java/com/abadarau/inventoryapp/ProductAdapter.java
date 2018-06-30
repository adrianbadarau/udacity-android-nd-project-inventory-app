package com.abadarau.inventoryapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.abadarau.inventoryapp.domain.ProductPOJO;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<ProductPOJO> {

    public ProductAdapter(@NonNull Context context, @NonNull List<ProductPOJO> objects) {
        super(context, R.layout.product_list_item, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent);
        }

        final ProductPOJO product = getItem(position);
        TextView productNameTV = convertView.findViewById(R.id.product_name_tv);
        productNameTV.setText(product.getName());
        ((TextView) convertView.findViewById(R.id.product_currency_tv)).setText(String.valueOf(product.getPrice()));
        ((TextView) convertView.findViewById(R.id.product_qty_tv)).setText(product.getQty());
        final View finalConvertView = convertView;
        ((Button) convertView.findViewById(R.id.buy_product_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newQty = product.getPrice() - 1;
                if(newQty >= 0){
                    product.setPrice(newQty);
                    ((TextView) finalConvertView.findViewById(R.id.product_qty_tv)).setText(product.getQty());
                }else{
                    Snackbar.make(finalConvertView, R.string.qty_neg_err, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        return finalConvertView;
    }
}
