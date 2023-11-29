package com.example.assignmentnetwork.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.assignmentnetwork.LoginActivity;
import com.example.assignmentnetwork.R;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.database.RequestLoginRegister;
import com.example.assignmentnetwork.model.ServerRequest;
import com.example.assignmentnetwork.model.ServerResponse;
import com.example.assignmentnetwork.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountFragment extends Fragment {

    private TextView tv_name, tv_email, tv_message;
    private EditText ed_old_password, ed_new_password;
    private SharedPreferences pref;
    private AlertDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        tv_name = view.findViewById(R.id.tvName);
        tv_email = view.findViewById(R.id.tvEmail);

        pref = view.getContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        tv_name.setText("" + pref.getString(Constants.NAME, ""));
        tv_email.setText("" + pref.getString(Constants.EMAIL, ""));
        Button btn_chgpassword = view.findViewById(R.id.btn_chgpassword);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btn_chgpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_password, null);
        ed_old_password = view.findViewById(R.id.et_old_password);
        ed_new_password = view.findViewById(R.id.et_new_password);
        tv_message = view.findViewById(R.id.tv_message);

        builder.setView(view);
        builder.setTitle("Đổi mật khẩu");
        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = ed_old_password.getText().toString();
                String new_password = ed_new_password.getText().toString();
                if (!old_password.isEmpty() && !new_password.isEmpty()) {
                    changePasswordProcess(tv_email.getText().toString(), old_password, new_password);
                } else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");
                }
            }
        });
    }

    private void changePasswordProcess(String email, String old_password, String new_password) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestLoginRegister requestInterface = retrofit.create(RequestLoginRegister.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.SUCCESS)) {
                    tv_message.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed " + t.getMessage());
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getMessage());
            }
        });
    }

    public void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN, false);
        editor.putString(Constants.NAME, "");
        editor.putString(Constants.EMAIL, "");
        editor.putString(Constants.UNIQUE_ID, "");
        editor.apply();
        gotoLogin();
    }

    private void gotoLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }
}