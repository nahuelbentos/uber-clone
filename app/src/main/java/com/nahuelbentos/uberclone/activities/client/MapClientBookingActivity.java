package com.nahuelbentos.uberclone.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.driver.CalificationClientActivity;
import com.nahuelbentos.uberclone.activities.driver.MapDriverBookingActivity;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientBookingProvider;
import com.nahuelbentos.uberclone.providers.DriverProvider;
import com.nahuelbentos.uberclone.providers.GeofireProvider;
import com.nahuelbentos.uberclone.providers.GoogleAPIProvider;
import com.nahuelbentos.uberclone.providers.TokenProvider;
import com.nahuelbentos.uberclone.utils.DecodePoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeofireProvider mGeofireProvider;

    private FusedLocationProviderClient mFusedLocation;


    private Marker mMarkerDriver;
    private LatLng mCurrentLatLng;

    private boolean mIsFirstTime = true;

    private AutocompleteSupportFragment mAutocomplete;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private TokenProvider mTokenProvider;
    private DriverProvider mDriverProvider;
    private ClientBookingProvider mClientBookingProvider;


    private AutocompleteSupportFragment mAutocompleteDestination;

    private String mDestination;
    private LatLng mDestinationLatLng;
    private LatLng mDriverLatLng;


    private GoogleAPIProvider mGoogleAPIProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private TextView mTextViewDriverBooking;
    private TextView mTextViewEmailDriverBooking;
    private TextView mTextViewOriginDriverBooking;
    private TextView mTextViewDestinationDriverBooking;
    private TextView mTextViewStatusBooking;

    private ValueEventListener mListener;
    private String mIdDriver;
    private ValueEventListener mListenerStatus;

    private ImageView mImageViewClientBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);


        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider("drivers_working");
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);



        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }


        mTokenProvider = new TokenProvider();
        mGoogleAPIProvider = new GoogleAPIProvider(MapClientBookingActivity.this);
        mDriverProvider = new DriverProvider();
        mClientBookingProvider = new ClientBookingProvider();

        mTextViewDriverBooking = findViewById(R.id.textViewDriverBooking);
        mTextViewEmailDriverBooking = findViewById(R.id.textViewEmailDriverBooking);

        mTextViewOriginDriverBooking = findViewById(R.id.textViewOriginDriverBooking);
        mTextViewDestinationDriverBooking = findViewById(R.id.textViewDestinationDriverBooking);
        mTextViewStatusBooking = findViewById(R.id.textViewStatusBooking);
        mImageViewClientBooking = findViewById(R.id.imageViewClientBooking);

        getStatus();
        getClientBooking();



    }

    private void getStatus() {
       mListenerStatus =  mClientBookingProvider.getStatus(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("accepted")){
                        mTextViewStatusBooking.setText("Estado: Viaje aceptado");

                    }

                    if (status.equals("started")){
                        mTextViewStatusBooking.setText("Estado: Viaje iniciado");
                        startBooking();
                    }else if (status.equals("finished")) {
                        mTextViewStatusBooking.setText("Estado: Viaje finalizado");
                        finishBooking();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void finishBooking() {
        Intent intent = new Intent(MapClientBookingActivity.this, CalificationDriverActivity.class);
        startActivity(intent);
        finish();
    }

    private void startBooking() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));
        drawRoute(mDestinationLatLng);

    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String destination = dataSnapshot.child("destination").getValue().toString();
                    String origin = dataSnapshot.child("origin").getValue().toString();
                    double destinationLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());
                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());

                    String idDriver =  dataSnapshot.child("idDriver").getValue().toString();
                    mIdDriver = idDriver;

                    mTextViewOriginDriverBooking.setText("Recoger en: "+ origin);
                    mTextViewDestinationDriverBooking.setText("Destino: "+ destination);
                    mOriginLatLng = new LatLng(originLat, originLng);
                    mDestinationLatLng = new LatLng(destinationLat, destinationLng);
                    mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
                    getDriver(idDriver);
                    getDriverLocation(idDriver);
                   // drawRoute();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriver(String idDriver) {
        mDriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    mTextViewDriverBooking.setText(name);
                    mTextViewEmailDriverBooking.setText(email);
                    String image = "";
                    if(dataSnapshot.hasChild("image")){

                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(MapClientBookingActivity.this ).load(image).into(mImageViewClientBooking);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriverLocation(String idDriver) {
        mListener = mGeofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    double lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                    mDriverLatLng = new LatLng(lat, lng);

                    if(mMarkerDriver != null){
                        mMarkerDriver.remove();
                    }

                    mMarkerDriver = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                            .title("Tu conductor")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                    if(mIsFirstTime){
                        mIsFirstTime = false;

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mOriginLatLng)
                                        .zoom(14f)
                                        .build()
                        ));
                        drawRoute(mOriginLatLng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void drawRoute(LatLng latLng){

        mGoogleAPIProvider.getDirections(mDriverLatLng, latLng).enqueue(new Callback<String>() {
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

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);

                    // Obtengo distancia del response
                    String distance = leg.getJSONObject("distance").getString("text");
                    String duration = leg.getJSONObject("duration").getString("text");


                    // Obtengo una unica propiedad de la propiedad anterior
                    String points = polylines.getString("points");

                    mPolylineList = DecodePoints.decodePoly(points);

                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                } catch (Exception e) {
                    Log.d("onResponse", "onResponse: Error encontrado" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mGeofireProvider.getDriverLocation(mIdDriver).removeEventListener(mListener);
        }

        if(mListenerStatus != null){
            mClientBookingProvider.getStatus(mAuthProvider.getId()).removeEventListener(mListenerStatus);
        }
    }
}