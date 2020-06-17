package com.nahuelbentos.uberclone.providers;

import com.nahuelbentos.uberclone.models.FCMBody;
import com.nahuelbentos.uberclone.models.FCMResponse;
import com.nahuelbentos.uberclone.retrofit.IFCMApi;
import com.nahuelbentos.uberclone.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
