package com.nahuelbentos.uberclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nahuelbentos.uberclone.models.Driver;

public class DriverProvider {

    DatabaseReference database;

    public DriverProvider() {
        this.database = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver driver){
        return database.child(driver.getId()).setValue(driver);
    }
}
