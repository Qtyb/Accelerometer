package com.example.piotrkutyba.accelerometr;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class RaceStatus {
    public Boolean checkRaceStatus(LatLng AcheckPosition, LatLng AfinishPosition, double Aprecision){
        boolean status = false;

        if(AcheckPosition.latitude - AfinishPosition.latitude < Aprecision){
            if(AcheckPosition.longitude - AfinishPosition.longitude < Aprecision){
                status = true;
            }
        }
        return status;
    }
}
