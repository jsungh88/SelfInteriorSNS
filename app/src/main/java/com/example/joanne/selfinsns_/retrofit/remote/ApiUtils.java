package com.example.joanne.selfinsns_.retrofit.remote;

public class ApiUtils {

    public static final String BASE_URL = "http://13.209.108.67/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}