package com.example.assignmentnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.database.RequestLoginRegister;
import com.example.assignmentnetwork.model.ServerRequest;
import com.example.assignmentnetwork.model.ServerResponse;
import com.example.assignmentnetwork.model.User;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPassActivity extends AppCompatActivity {

    private Button btn_forgot;
    private TextInputLayout ed_email, ed_code, ed_password;
    private TextView tv_timer;
    private boolean isResetInitiated = false;
    private String email;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        tv_timer = findViewById(R.id.timer);
        ed_email = findViewById(R.id.edEmail);
        ed_code = findViewById(R.id.edCode);
        ed_password = findViewById(R.id.edPassword);
        btn_forgot = findViewById(R.id.btn_forgot);

        tv_timer.setVisibility(View.GONE);
        ed_code.setVisibility(View.GONE);
        ed_password.setVisibility(View.GONE);
    }

    public void forgotClick(View view) {
        if (!isResetInitiated) {
            email = ed_email.getEditText().getText().toString();
            if (!email.isEmpty()) {
                initiateForgotPasswordProcess(email);
//                Log.d("email: ", email);
            } else {
                Toast.makeText(this, "Don't empty !", Toast.LENGTH_SHORT).show();
            }
        } else {
            String code = ed_code.getEditText().getText().toString();
            String password = ed_password.getEditText().getText().toString();
            if (!code.isEmpty() && !password.isEmpty()) {
                finishForgotPasswordProcess(email, code, password);
            } else {
                Toast.makeText(this, "Don't empty !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initiateForgotPasswordProcess(String email){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestLoginRegister requestLoginRegister = retrofit.create(RequestLoginRegister.class);
        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_INITIATE);
        request.setUser(user);

        Call<ServerResponse> response = requestLoginRegister.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Toast.makeText(getApplicationContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();

                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Toast.makeText(getApplicationContext(), "Mã code được gửi về email của bạn", Toast.LENGTH_SHORT).show();

                    ed_email.setVisibility(View.GONE);
                    ed_code.setVisibility(View.VISIBLE);
                    ed_password.setVisibility(View.VISIBLE);
                    tv_timer.setVisibility(View.VISIBLE);
                    btn_forgot.setText("Change Password");
                    isResetInitiated = true;
                    startCountdownTimer();

                } else {
                    Toast.makeText(getApplicationContext(), "Email không chính xác", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed");
            }
        });
    }

    private void finishForgotPasswordProcess(String email, String code, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestLoginRegister requestLoginRegister = retrofit.create(RequestLoginRegister.class);
        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestLoginRegister.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Toast.makeText(getApplicationContext(), "Reset ur pass success", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    isResetInitiated = false;
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Reset ur pass fail", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed");
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_timer.setText("Thời gian còn lại : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(ForgotPassActivity.this, "Time out ! Request again to reset ur pass", Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        }.start();
    }
    private void goToLogin() {
        startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
    }
}