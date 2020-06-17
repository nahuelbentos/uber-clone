package com.nahuelbentos.uberclone.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nahuelbentos.uberclone.models.Token;

public class TokenProvider {

    DatabaseReference mDatabase;

    public TokenProvider() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }

    public void create(String idUser){
        if(idUser == null){
            return;
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken());
                mDatabase.child(idUser).setValue(token);
            }
        });
    }

    public DatabaseReference getToken(String idUser){
        return mDatabase.child(idUser);
    }
}
