package com.nahuelbentos.uberclone.retrofit;

import com.nahuelbentos.uberclone.models.FCMBody;
import com.nahuelbentos.uberclone.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

// FCM = Firebase Cloud Messagging
public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAAfpO-Mt0:APA91bHHPJ8mU-CBQO2-7P2LDat6RgtE-zIrkyOsCl5g6hPwSpi2HKYH0hCcyRiQlRQEzSg2tKtNTQmsnl80lu0JuyCrLzZry825artpsyRRIoa6eCT4SZbQzEUetvLQB5ThTgzQlIyB"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
