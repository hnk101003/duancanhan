package com.example.assignmentnetwork.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;

import com.example.assignmentnetwork.JSONparser;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetProductDetailsTask extends AsyncTask<String, String, String> {

    Context context;
    ProgressDialog pDialog;
    JSONparser jsonParser;
    EditText edpro_Name, edpro_Des, edpro_Price;
    Product product;

    public GetProductDetailsTask(Context context, EditText edpro_Name, EditText edpro_Des, EditText edpro_Price) {
        this.context = context;
        this.edpro_Name = edpro_Name;
        this.edpro_Des = edpro_Des;
        this.edpro_Price = edpro_Price;
        jsonParser = new JSONparser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        List<HashMap<String, String>> params = new ArrayList<>();
        HashMap<String, String> hsPid = new HashMap<>();
        hsPid.put(Constants.TAG_PID, strings[0]);
        params.add(hsPid);

        try {
            JSONObject json = jsonParser.makeHttpRequest(Constants.url_product_detail, "GET", params);
            int success = json.getInt(Constants.TAG_SUCCESS);

            if (success == 1) {
                JSONArray productObj = json.getJSONArray(Constants.TAG_PRODUCT);
                // get first product object from JSON Array
                JSONObject obj = productObj.getJSONObject(0);
                // product with this pid found
                product = new Product();
                product.setName(obj.getString(Constants.TAG_NAME));
                product.setDescription(obj.getString(Constants.TAG_DESCRIPTION));
                product.setPrice(obj.getString(Constants.TAG_PRICE));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
        edpro_Name.setText(product.getName());
        edpro_Des.setText(product.getDescription());
        edpro_Price.setText(product.getPrice());
    }
}
