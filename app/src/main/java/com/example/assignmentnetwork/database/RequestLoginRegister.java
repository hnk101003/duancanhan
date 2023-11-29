package com.example.assignmentnetwork.database;

import com.example.assignmentnetwork.model.ServerRequest;
import com.example.assignmentnetwork.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestLoginRegister {

    @POST("learn-login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
