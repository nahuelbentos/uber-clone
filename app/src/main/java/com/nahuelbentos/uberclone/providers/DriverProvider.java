package com.nahuelbentos.uberclone.providers;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nahuelbentos.uberclone.models.Client;
import com.nahuelbentos.uberclone.models.Driver;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {

    DatabaseReference database;

    public DriverProvider() {
        this.database = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver driver){
        return database.child(driver.getId()).setValue(driver);
    }

    public DatabaseReference getDriver(String idDriver){
        return database.child(idDriver);
    }


    public Task<Void> update(Driver driver){
        Log.d("ErrorUpdate", "update: " + driver);

        Map<String, Object> map = new HashMap<>();
        map.put("name", driver.getName());
        map.put("vehicleBrande", driver.getVehicleBrand());
        map.put("vehiclePlate", driver.getVehiclePlate());
        map.put("image", driver.getImage());

        Log.d("ErrorUpdate", "driver.getName(): " + driver.getName());
        Log.d("ErrorUpdate", "driver.getImage(): " + driver.getImage());
        Log.d("ErrorUpdate", "driver.getVehicleBrand(): " + driver.getVehicleBrand());
        Log.d("ErrorUpdate", "driver.getVehiclePlate(): " + driver.getVehiclePlate());
        Log.d("ErrorUpdate", "driver.getId(): " + driver.getId());

        return database.child(driver.getId()).updateChildren(map);
    }

}
