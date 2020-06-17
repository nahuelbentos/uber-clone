package com.nahuelbentos.uberclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.RegisterActivity;
import com.nahuelbentos.uberclone.activities.driver.RegisterDriverActivity;
import com.nahuelbentos.uberclone.includes.MyToolbar;

public class SelectOptionAuthActivity extends AppCompatActivity {

    SharedPreferences mPref;

    Button mbuttonGoToLogin;
    Button mbuttonGoToRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);


        MyToolbar.show(this, "Seleccionar opción", true);


        mbuttonGoToLogin = findViewById(R.id.btnGoToLogin);
        mbuttonGoToRegister = findViewById(R.id.btnGoToRegister);
        mbuttonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        mbuttonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

    }

    public void goToLogin(){
        Intent intent = new Intent(SelectOptionAuthActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void goToRegister(){
        String typeUser = mPref.getString("user", "") ;
        if (typeUser.equals("client")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }


    }
}