package com.nahuelbentos.uberclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAuth;

    public AuthProvider() {
        this.mAuth  = FirebaseAuth.getInstance();
    }

    // Una tarea sería como un observable o promesa
    public Task<AuthResult> register(String email, String password){
       return  mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password){
        return  mAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout(){
        mAuth.signOut();
    }

    public String getId(){
        return  mAuth.getCurrentUser().getUid();
    }

    public  boolean existsSession(){
        return (mAuth.getCurrentUser() != null);
    }
}
