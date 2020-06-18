package com.nahuelbentos.uberclone.activities.driver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.RequestDriverActivity;
import com.nahuelbentos.uberclone.models.ClientBooking;
import com.nahuelbentos.uberclone.models.FCMBody;
import com.nahuelbentos.uberclone.models.FCMResponse;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientBookingProvider;
import com.nahuelbentos.uberclone.providers.ClientProvider;
import com.nahuelbentos.uberclone.providers.GeofireProvider;
import com.nahuelbentos.uberclone.providers.GoogleAPIProvider;
import com.nahuelbentos.uberclone.providers.NotificationProvider;
import com.nahuelbentos.uberclone.providers.TokenProvider;
import com.nahuelbentos.uberclone.utils.DecodePoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeofireProvider mGeofireProvider;
    private ClientProvider mClientProvider;
    private ClientBookingProvider mClientBookingProvider;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;

    private LatLng mCurrentLatLng;


    private TokenProvider mTokenProvider;
    private NotificationProvider mNotificationProvider;


    private TextView mTextViewClientBooking;
    private TextView mTextViewEmailClientBooking;
    private TextView mTextViewOriginClientBooking;
    private TextView mTextViewDestinationClientBooking;

    private String mExtraIdClient;


    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

    private GoogleAPIProvider mGoogleAPIProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private boolean mIsFirstTime = false;
    private boolean mIsCloseToClient = false;
    private Button mButtonStartBooking;
    private Button mButtonFinishBooking;

    private ImageView mImageViewClientBooking;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Log.d("ErrorNull", "ErrorNull mLocationCallback 0: ");
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    Log.d("ErrorNull", "ErrorNull mLocationCallback 1: ");

                    Log.d("ErrorNull", "mLocationCallback 1  mCurrentLatLng:  "+ mCurrentLatLng);
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.d("ErrorNull", "mLocationCallback 2  mCurrentLatLng:  "+ mCurrentLatLng);
                    Log.d("ErrorNull", "ErrorNull mLocationCallback 2: ");
                    if (mMarker != null){
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Tu posición")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                    // Obtener la localizacion del usuario en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    updateLocation();

                    if (mIsFirstTime) {
                        mIsFirstTime = false;
                        getClientBooking();

                    }
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);


        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mGeofireProvider = new GeofireProvider("drivers_working");

        mNotificationProvider = new NotificationProvider();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mTokenProvider = new TokenProvider();

        mGoogleAPIProvider = new GoogleAPIProvider(MapDriverBookingActivity.this);

        mTextViewClientBooking = findViewById(R.id.textViewClientBooking);
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailClientBooking);
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginClientBooking);
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationClientBooking);
        mButtonStartBooking = findViewById(R.id.btnStartBooking);
        mButtonFinishBooking = findViewById(R.id.btnFinishBooking);
        mImageViewClientBooking = findViewById(R.id.imageViewClientBooking);
        mExtraIdClient = getIntent().getStringExtra("idClient");

//        mButtonStartBooking.setEnabled(false);


        mButtonStartBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsCloseToClient){

                startBooking();
                } else {
                    Toast.makeText(MapDriverBookingActivity.this, "Debes estar cerca del cliente para iniciar el viaje.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonFinishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBooking();
            }
        });

        getClient();
        getClientBooking();


    }

    private void finishBooking() {
        mClientBookingProvider.updateStatus(mExtraIdClient, "finished");
        mClientBookingProvider.updateHistoryBooking(mExtraIdClient);
        sendNotification("Viaje Finalizado");
        if (mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
        mGeofireProvider.removeLocation(mAuthProvider.getId());
        Intent intent = new Intent(this, CalificationClientActivity.class);
        intent.putExtra("idClient", mExtraIdClient);
        startActivity(intent);
        finish();
    }

    private void startBooking() {
        mClientBookingProvider.updateStatus(mExtraIdClient, "started");
        sendNotification("Viaje Iniciado");
        mButtonStartBooking.setVisibility(View.GONE);
        mButtonFinishBooking.setVisibility(View.VISIBLE);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));
        drawRoute(mDestinationLatLng);
    }

    private  double getDistanceBetween(LatLng clientLatLng, LatLng driverLatLng){
        double distance = 0;

        Location clientLocation = new Location("");
        Location driverLocation = new Location("");

        clientLocation.setLatitude(clientLatLng.latitude);
        clientLocation.setLongitude(clientLatLng.longitude);
        driverLocation.setLatitude(driverLatLng.latitude);
        driverLocation.setLongitude(driverLatLng.longitude);

        // Obtengo la distancia entre la posicion del cliente y la del conductor.
        distance = clientLocation.distanceTo(driverLocation);

        return distance;
    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mExtraIdClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String destination = dataSnapshot.child("destination").getValue().toString();
                    String origin = dataSnapshot.child("origin").getValue().toString();
                    double destinationLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());
                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());

                    mTextViewOriginClientBooking.setText("Recoger en: "+ origin);
                    mTextViewDestinationClientBooking.setText("Destino: "+ destination);
                    mOriginLatLng = new LatLng(originLat, originLng);
                    mDestinationLatLng = new LatLng(destinationLat, destinationLng);
                    mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
                    drawRoute(mOriginLatLng);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void drawRoute(LatLng latLng){
        Log.d("ErrorNull", "drawRoute ErrorNull: 1 ");
        Log.d("ErrorNull", "drawRoute mCurrentLatLng:  "+ mCurrentLatLng);
        Log.d("ErrorNull", "drawRoute mOriginLatLng:  "+mOriginLatLng);
        if(mCurrentLatLng != null){

            mGoogleAPIProvider.getDirections(mCurrentLatLng, latLng).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                         Log.d("ErrorNull", "drawRoute ErrorNull 2: ");
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
                        Log.d("ErrorNull", "drawRoute ErrorNull 3: ");
                        Log.d("onResponse", "onResponse: Error encontrado" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

        }
    }

    private void getClient() {
        mClientProvider.getClient(mExtraIdClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String email = dataSnapshot.child("email").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = "";
                    if(dataSnapshot.hasChild("image")){

                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(MapDriverBookingActivity.this ).load(image).into(mImageViewClientBooking);
                    }

                    mTextViewEmailClientBooking.setText(email);
                    mTextViewClientBooking.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateLocation(){
        Log.d("ErrorNull", "updateLocation ErrorNull : ");
        Log.d("ErrorNull", "updateLocation 1  mCurrentLatLng:  "+ mCurrentLatLng);
        if (mAuthProvider.existsSession() && mCurrentLatLng != null){
            Log.d("ErrorNull", "updateLocation 2  mCurrentLatLng:  "+ mCurrentLatLng);
            Log.d("ErrorNull", "updateLocation ErrorNull 2 : ");
            mGeofireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
            Log.d("ErrorNull", "updateLocation  ErrorNull 3 : ");
            Log.d("ErrorNull", "updateLocation 3  mCurrentLatLng:  "+ mCurrentLatLng);
            if (!mIsCloseToClient){
                if(mOriginLatLng != null & mCurrentLatLng != null){

                    double distance = getDistanceBetween(mOriginLatLng, mCurrentLatLng); // Retornada en metros
                    if (distance <= 200){
//                        mButtonStartBooking.setEnabled(true);
                        mIsCloseToClient = true;
                        Toast.makeText(this, "Estas cerca de la ubicacion del cliente", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()){
                        Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult: 1");
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                        Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult 2: ");
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
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
            Log.d("ErrorNull", "Error Null onRequestPermissionsResult: 1");
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            Log.d("ErrorNull", "Error Null onRequestPermissionsResult: 2");
        } else {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa el GPS para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                })
                .create()
                .show();
    }

    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Si tiene el gps activado
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
        return isActive;
    }



    private void disconnect(){
        if(mFusedLocation != null){
            Log.d("ErrorNull", "ErrorNull disconnect: 1");
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            Log.d("ErrorNull", "ErrorNull disconnect: 2");
            if(mAuthProvider.existsSession()){
                mGeofireProvider.removeLocation(mAuthProvider.getId());
            }
        } else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }

    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (gpsActived()){
                    Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult: 1");
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult: 2");
                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()){
                Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult: 3");
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                Log.d("ErrorNull", "ErrorNull onRequestPermissionsResult: 4");
            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicación requiere de los permisos de ubicación para poder utilizarse")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Habilita los permisos para obtener la ubicación del celular
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }

    }

    private void sendNotification(String status) {
        mTokenProvider.getToken(mExtraIdClient).addListenerForSingleValueEvent(new ValueEventListener() {
            // DataSnapshot contiene la informacin del nodo que estamos intentando obtener.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "ESTADO DE TU VIAJE  " + status );
                    map.put("body",
                            "Tu estado del viaje es cliente es: "+ status);

                    map.put("idClient", mAuthProvider.getId());
                    FCMBody fcmBody = new FCMBody(token, "high","4500s", map );
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if(response.body() != null){
                                // La notificacion se envio correctamente
                                if (response.body().getSuccess() != 1) {

                                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la  notificación", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la  notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "onFailure: Error:  " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificación porque el conductor no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}