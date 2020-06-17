package com.nahuelbentos.uberclone.activities.client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.models.Client;
import com.nahuelbentos.uberclone.models.ClientBooking;
import com.nahuelbentos.uberclone.models.FCMBody;
import com.nahuelbentos.uberclone.models.FCMResponse;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientBookingProvider;
import com.nahuelbentos.uberclone.providers.GeofireProvider;
import com.nahuelbentos.uberclone.providers.GoogleAPIProvider;
import com.nahuelbentos.uberclone.providers.NotificationProvider;
import com.nahuelbentos.uberclone.providers.TokenProvider;
import com.nahuelbentos.uberclone.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverActivity extends AppCompatActivity {
    private LottieAnimationView mAnimation;
    private TextView mTextViewLookingFor;
    private Button mButtonCancelRequest;

    private GeofireProvider mGeofireProvider;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;
    private String mExtraOrigin;
    private String mExtraDestination;

    private double mRadius = 0.1;

    private boolean mDriverFound = false;
    private String mIdDriverFound = "";

    private  LatLng mDriverFoundLatLng;

    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvider;
    private AuthProvider mAuthProvider;
    private ClientBookingProvider mClientBookingProvider;
    private GoogleAPIProvider mGoogleAPIProvider;

    private ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        mAnimation = findViewById(R.id.animation);
        mTextViewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);

        mAnimation.playAnimation();

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng",0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat",0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng",0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGeofireProvider = new GeofireProvider("active_drivers");
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mGoogleAPIProvider = new GoogleAPIProvider(RequestDriverActivity.this);

        getClosestDriver();
    }

    private void getClosestDriver(){

        mGeofireProvider.getActiveDrivers(mOriginLatLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!mDriverFound){
                    mDriverFound = true;
                    mIdDriverFound = key;
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookingFor.setText("CONDUCTOR ENCONTRADO \n ESPERANDO RESPUESTA");

                    Log.d("Driver", "onKeyEntered: ID: " + key);
                    createClientBooking();
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                // INGRESA CUANDO TERMINA LA BUSQUEDA DEL CONDUCTOR EN UN RADIO DE 0.1KM
                if(!mDriverFound){
                    mRadius = mRadius + 0.1f;

                    if(mRadius > 5){
                        // No encontro ningún conductor
                        mTextViewLookingFor.setText("No se encontro un conductor");
                        Toast.makeText(RequestDriverActivity.this, "No se encontro ningún conductor.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        getClosestDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    private void createClientBooking(){

        mGoogleAPIProvider.getDirections(mOriginLatLng, mDriverFoundLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // Obtengo todo el body de la respuesta
                    JSONObject jsonObject =  new JSONObject(response.body());
                    // Obtengo una propiedad array dentro de la respuesta
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    // Obtengo el primer elemento del array anterior
                    JSONObject route = jsonArray.getJSONObject(0);

                    // Obtengo un objeto de la propiedad anterior
                    JSONObject polylines = route.getJSONObject("overview_polyline");

                    // Obtengo una unica propiedad de la propiedad anterior
                    String points = polylines.getString("points");

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);

                    // Obtengo distancia del response
                    String distance = leg.getJSONObject("distance").getString("text");
                    String duration = leg.getJSONObject("duration").getString("text");

                    sendNotification(duration, distance);




                } catch (Exception e) {
                    Log.d("onResponse", "onResponse: Error encontrado" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



    }
    private void sendNotification(String time, String km) {
        mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            // DataSnapshot contiene la informacin del nodo que estamos intentando obtener.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "SOLICITUD DE SERVICIO A " + time + " DE TU POSICIÓN" );
                    map.put("body",
                            "Un cliente está solicitando un servicio a una distancia de "+ km + "\n" +
                            "Recoger en: " + mExtraOrigin + "\n" +
                            "Destino: " + mExtraDestination);

                    map.put("idClient", mAuthProvider.getId());
                    FCMBody fcmBody = new FCMBody(token, "high", map );
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if(response.body() != null){
                                // La notificacion se envio correctamente
                                if (response.body().getSuccess() == 1) {
                                    Toast.makeText(RequestDriverActivity.this, "La notificación se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                    ClientBooking clientBooking = new ClientBooking(
                                            mAuthProvider.getId(),
                                            mIdDriverFound,
                                            mExtraOrigin,
                                            mExtraDestination,
                                            time,
                                            km,
                                            "created",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng
                                    );
                                    mClientBookingProvider.create(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkStatusClientBooking();
                                        }
                                    });

                                } else {
                                    Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la  notificación", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la  notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "onFailure: Error:  " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la notificación porque el conductor no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkStatusClientBooking() {

        // El evento addValueEventListener sirve para obtener los cambios de una propiedad en tiempo real
        mListener = mClientBookingProvider.getStatus(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("accepted")){
                        Intent intent = new Intent(RequestDriverActivity.this, MapClientBookingActivity.class );
                        startActivity(intent);
                        finish();
                    } else if (status.equals("cancelled")){
                        Toast.makeText(RequestDriverActivity.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RequestDriverActivity.this, MapClientActivity.class );
                        startActivity(intent);
                        finish();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpio el Listener ya que sino queda ejecutandose permanentemente
        if (mListener != null ) {
            mClientBookingProvider.getStatus(mAuthProvider.getId()).removeEventListener(mListener);
        }

    }
}
