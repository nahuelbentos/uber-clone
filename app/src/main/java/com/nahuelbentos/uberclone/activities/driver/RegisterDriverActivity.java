package com.nahuelbentos.uberclone.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.RegisterActivity;
import com.nahuelbentos.uberclone.includes.MyToolbar;
import com.nahuelbentos.uberclone.models.Client;
import com.nahuelbentos.uberclone.models.Driver;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientProvider;
import com.nahuelbentos.uberclone.providers.DriverProvider;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    // Views
    Button mButtonRegister;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;


    TextInputEditText mTextInputVehicleBrand;
    TextInputEditText mTextInputVehiclePlate;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);


        MyToolbar.show(this, "Registrar conductor", true);
        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();



        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Espere un momento").build();

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputVehicleBrand = findViewById(R.id.textInputVehicleBrand);
        mTextInputVehiclePlate = findViewById(R.id.textInputVehiclePlate);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });
    }

    public void clickRegister(){
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String vehicleBrande = mTextInputVehicleBrand.getText().toString();
        String vehiclePlate = mTextInputVehiclePlate.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !vehicleBrande.isEmpty() && !vehiclePlate.isEmpty()){
            if (password.length() >= 6){
                mDialog.show();
                register(name, email, password, vehicleBrande, vehiclePlate );

            }else{
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }

    }
    public void register(final String name, final String email, String password, final String vehicleBrand, final String vehiclePlate){
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id, name, email, vehicleBrand, vehiclePlate);
                    create(driver);
                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }

    void create( Driver driver){
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Toast.makeText(RegisterDriverActivity.this, "Se creo exitosamente el cliente", Toast.LENGTH_SHORT).show();

                    // Un intento es deonde estoy y a donde quiero ir (contexto y destino)
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    /*
    void saveUser(String id, String name, String email){

        String selectedUser = mPref.getString("user", "");
        User user =  new User();
        user.setName(name);
        user.setEmail(email);
        if (selectedUser.equals("driver")){

            mDatabase.child("Users").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Se creó el registro exitosamente", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Ocurrio un error con registro ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (selectedUser.equals("client")){
            mDatabase.child("Users").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Se creó el registro exitosamente", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Ocurrio un error con registro ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
     */
}