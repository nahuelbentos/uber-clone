package com.nahuelbentos.uberclone.providers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.retrofit.IGoogleApi;
import com.nahuelbentos.uberclone.retrofit.RetrofitClient;

import java.util.Date;

import retrofit2.Call;

public class GoogleAPIProvider {

    private Context context;

    public GoogleAPIProvider(Context context) {
        this.context = context;
    }

    public Call<String> getDirections(LatLng originLatLng, LatLng destinationLatLng) {
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&"
                + "destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&"
                + "departure_time=" + (new Date().getTime() + (60*60*1000)) + "&"
                + "traffic_model=best_guess&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);
        Log.d("GoogleAPI", "getDirections: "+ baseUrl + query);
        // Hago el request el servicio de Google usando la interfaz como referencia.
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl + query);
    }
}
