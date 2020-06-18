package com.nahuelbentos.uberclone.providers;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nahuelbentos.uberclone.models.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {

    DatabaseReference database;

    public ClientProvider() {
        this.database = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client){
        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("email", client.getEmail());

        return database.child(client.getId()).setValue(map);
    }
    public Task<Void> update(Client client){
        Log.d("ErrorUpdate", "update: " + client);

        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("image", client.getImage());

        Log.d("ErrorUpdate", "client.getName(): " + client.getName());
        Log.d("ErrorUpdate", "client.getImage(): " + client.getImage());
        Log.d("ErrorUpdate", "client.getId(): " + client.getId());

        return database.child(client.getId()).updateChildren(map);
    }


    public DatabaseReference getClient(String idClient){
        return database.child(idClient);
    }
}
