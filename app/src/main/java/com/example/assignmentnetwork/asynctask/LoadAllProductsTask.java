package com.example.assignmentnetwork.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.example.assignmentnetwork.JSONparser;
import com.example.assignmentnetwork.adapter.AdapterProduct;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.fragment.HomeFragment;
import com.example.assignmentnetwork.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadAllProductsTask extends AsyncTask<String, String, String> {

    Context context;
    ListView lvProducts;
    ProgressDialog pDialog;
    JSONparser jParser;
    ArrayList<Product> listProducts;
    JSONArray products = null;

    AdapterProduct adapterProduct;

    private AdapterProduct.OnEditItemClickListener itemClickListener;
    private AdapterProduct.OnRemoveItemClickListener onRemoveItemClickListener;
    public LoadAllProductsTask(Context context, ListView lvProducts, AdapterProduct adapterProduct, AdapterProduct.OnEditItemClickListener itemClickListener, AdapterProduct.OnRemoveItemClickListener onRemoveItemClickListener) {
        this.context = context;
        this.lvProducts = lvProducts;
        this.adapterProduct = adapterProduct;
        this.itemClickListener = itemClickListener;
        this.onRemoveItemClickListener = onRemoveItemClickListener;

        jParser = new JSONparser();
        this.listProducts = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        List<HashMap<String, String>> params = new ArrayList<>();
        JSONObject jsonObject = jParser.makeHttpRequest(Constants.url_all_products, "GET", params);
        try {
            int success = jsonObject.getInt("success");
            if (success == 1) {
                Log.d("All Products: ", jsonObject.toString());
                products = jsonObject.getJSONArray(Constants.TAG_PRODUCTS);
                for (int i = 0; i < products.length(); i++) {
                    JSONObject c = products.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(Constants.TAG_PID);
                    String name = c.getString(Constants.TAG_NAME);
                    String description = c.getString(Constants.TAG_DESCRIPTION);
                    String price = c.getString(Constants.TAG_PRICE);

                    // creating new Product
                    Product product = new Product();
                    product.setId(id);
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    listProducts.add(product);
                }
            } else {
                // no products found
                // Launch Add New product Activity
                Intent intent = new Intent(context, HomeFragment.class);
                // Closing all previous activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        adapterProduct = new AdapterProduct(context, listProducts);
        adapterProduct.setOnEditItemClickListener(itemClickListener);
        adapterProduct.setOnRemoveItemClickListener(onRemoveItemClickListener);
        lvProducts.setAdapter(adapterProduct);
        adapterProduct.notifyDataSetChanged();

//
    }

}
