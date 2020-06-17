package com.nahuelbentos.uberclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.MapClientActivity;
import com.nahuelbentos.uberclone.activities.driver.MapDriverActivity;

public class MainActivity extends AppCompatActivity {
    Button mButtonIAmClient;
    Button mButtonIAmDriver;

    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPref.edit();
        mButtonIAmClient = findViewById(R.id.btnIAmClient);
        mButtonIAmDriver = findViewById(R.id.btnIAmDriver);
        
        mButtonIAmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "client");
                editor.apply();
                goToSelectAuth();
            }
        });


        mButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("user", "driver");
                editor.apply();
                goToSelectAuth();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String typeUser = mPref.getString("user", "") ;
            if (typeUser.equals("client")){
                Intent intent = new Intent(MainActivity.this, MapClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void goToSelectAuth() {
        Intent intent = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }
}