package com.nahuelbentos.uberclone.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.JsonObject;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.includes.MyToolbar;
import com.nahuelbentos.uberclone.providers.GoogleAPIProvider;
import com.nahuelbentos.uberclone.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;

    private String mExtraOrigin;
    private String mExtraDestination;


    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

    private GoogleAPIProvider mGoogleAPIProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private TextView mTextViewOrigin;
    private TextView mTextViewDestination;
    private TextView mTextViewTime;
    private TextView mTextViewDistance;

    private Button mButtonRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);

        MyToolbar.show(this,"Confirmar viaje", true);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Obtengo los parametros enviados desde la actividad predecesora
        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");


        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGoogleAPIProvider = new GoogleAPIProvider(DetailRequestActivity.this);

        mTextViewOrigin = findViewById(R.id.textViewOrigin);
        mTextViewDestination = findViewById(R.id.textViewDestination);
        mTextViewTime = findViewById(R.id.textViewTime);
        mTextViewDistance = findViewById(R.id.textViewDistance);
        mButtonRequest = findViewById(R.id.btnRequestNow);



        mTextViewOrigin.setText(mExtraOrigin);
        mTextViewDestination.setText(mExtraDestination);


        mButtonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequestDriver();
            }
        });



    }

    private void goToRequestDriver() {
        Intent intent = new Intent(DetailRequestActivity.this, RequestDriverActivity.class);
        intent.putExtra("origin", mExtraOrigin);
        intent.putExtra("destination", mExtraDestination);

        intent.putExtra("origin_lat", mOriginLatLng.latitude);
        intent.putExtra("origin_lng", mOriginLatLng.longitude);
        intent.putExtra("destination_lat", mDestinationLatLng.latitude);
        intent.putExtra("destination_lng", mDestinationLatLng.longitude);

//        intent.putExtra("distance", mTextViewDistance.getText().toString());
//        intent.putExtra("time", mTextViewTime.getText().toString());




        startActivity(intent);
        finish();
    }

    private void drawRoute(){
        mGoogleAPIProvider.getDirections(mOriginLatLng, mDestinationLatLng).enqueue(new Callback<String>() {
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

                    mTextViewDistance.setText(distance);
                    mTextViewTime.setText(duration);

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


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                    .target(mOriginLatLng)
                    .zoom(14f)
                    .build()
        ));

        drawRoute();
    }
}