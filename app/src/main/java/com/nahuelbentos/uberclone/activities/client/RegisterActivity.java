package com.nahuelbentos.uberclone.activities.client;

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
import com.nahuelbentos.uberclone.activities.driver.MapDriverActivity;
import com.nahuelbentos.uberclone.activities.driver.RegisterDriverActivity;
import com.nahuelbentos.uberclone.includes.MyToolbar;
import com.nahuelbentos.uberclone.models.Client;
import com.nahuelbentos.uberclone.providers.AuthProvider;
import com.nahuelbentos.uberclone.providers.ClientProvider;

import dmax.dialog.SpotsDialog;


public class RegisterActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;

    // Views
    Button mButtonRegister;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        MyToolbar.show(this, "Registrar usuario", true);
        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();



        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

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

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if (password.length() >= 6){
                mDialog.show();
                register(name, email, password);

            }else{
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }

    }
    public void register(final String name, final String email, String password){
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Client client = new Client(id, name, email);
                    create(client);
                } else {
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }

    void create( Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(RegisterActivity.this, "Se creo exitosamente el cliente", Toast.LENGTH_SHORT).show();

                    // Un intento es deonde estoy y a donde quiero ir (contexto y destino)
                    Intent intent = new Intent(RegisterActivity.this, MapClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
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