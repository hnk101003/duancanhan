package com.example.assignmentnetwork.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.assignmentnetwork.JSONparser;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.fragment.HomeFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.Callback;

public class CreateNewProductTask extends AsyncTask<String, String, String> {

    Context context;
    ProgressDialog pDialog;
    JSONparser jsonParser;


    public CreateNewProductTask(Context context) {
        this.context = context;
        jsonParser = new JSONparser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("vui lòng chờ...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        List<HashMap<String, String>> params = new ArrayList<>();

        HashMap<String, String> hsName = new HashMap<>();
        hsName.put("name",strings[0]);
        Log.d("TAG", "doInBackground: " + hsName);
        params.add(hsName);

        HashMap<String, String> hsDes = new HashMap<>();
        hsDes.put("description",strings[1]);
        Log.d("TAG", "doInBackground: " + hsDes);
        params.add(hsDes);

        HashMap<String, String> hsPrice = new HashMap<>();
        hsPrice.put("price",strings[2]);
        Log.d("TAG", "doInBackground: " + hsPrice);
        params.add(hsPrice);

        JSONObject jsonObject = jsonParser.makeHttpRequest(Constants.url_create_products,"POST",params);
        Log.d("Create response",jsonObject.toString());

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
