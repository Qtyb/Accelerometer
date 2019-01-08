package com.example.piotrkutyba.accelerometr;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {

    public Double latitude;
    public Double longitude;
    public LatLng latLng;

    public Coordinates(){
        this.latitude = 0d;
        this.longitude  = 0d;
    }
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude  = longitude;
    }
}
