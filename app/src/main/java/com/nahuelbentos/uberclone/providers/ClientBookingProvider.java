package com.nahuelbentos.uberclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nahuelbentos.uberclone.models.ClientBooking;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingProvider {
    private DatabaseReference mDatabase;

    public ClientBookingProvider() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }

    public Task<Void> create(ClientBooking clientBooking){
        return mDatabase.child(clientBooking.getIdClient()).setValue(clientBooking);
    }

    public Task<Void> updateStatus(String idClient, String status){
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return mDatabase.child(idClient).updateChildren(map);
    }

    // Crear referencia a una propiedad

    public DatabaseReference getStatus(String idClientBooking){
        return mDatabase.child(idClientBooking).child("status");
    }
}
