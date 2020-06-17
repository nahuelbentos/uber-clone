package com.nahuelbentos.uberclone.activities;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.activities.client.MapClientActivity;
import com.nahuelbentos.uberclone.activities.client.RegisterActivity;
import com.nahuelbentos.uberclone.activities.driver.MapDriverActivity;
import com.nahuelbentos.uberclone.includes.MyToolbar;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    AlertDialog mDialog;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyToolbar.show(this, "Login de usuario", true);


        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
    }

    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()){
            if (password.length() >= 6){
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "El login se realizo exitosamente", Toast.LENGTH_LONG).show();

                            String typeUser = mPref.getString("user", "") ;
                            if (typeUser.equals("client")){
                                Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_LONG).show();
                        }
                        mDialog.dismiss();
                    }
                });

            }else {

                Toast.makeText(LoginActivity.this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(LoginActivity.this, "El email y la contraseña son requeridos", Toast.LENGTH_LONG).show();

        }

    }
}