package com.nahuelbentos.uberclone.models;

import java.util.ArrayList;

public class FCMResponse {

    private double multicast_id;
    private int success;
    private int failure;
    private int canonical_ids;
    ArrayList<Object> results = new ArrayList<Object>();

    public FCMResponse(double multicast_id, int success, int failure, int canonical_ids, ArrayList<Object> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    // Getter Methods

    public double getMulticast_id() {
        return multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    // Setter Methods

    public void setMulticast_id( double multicast_id ) {
        this.multicast_id = multicast_id;
    }

    public void setSuccess( int success ) {
        this.success = success;
    }

    public void setFailure( int failure ) {
        this.failure = failure;
    }

    public void setCanonical_ids( int canonical_ids ) {
        this.canonical_ids = canonical_ids;
    }
}