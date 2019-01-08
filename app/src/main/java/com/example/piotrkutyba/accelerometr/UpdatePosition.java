package com.example.piotrkutyba.accelerometr;

import android.util.Log;
import java.util.TimerTask;
import static com.example.piotrkutyba.accelerometr.MainActivity.coordinates;

public class UpdatePosition extends TimerTask {

    public void run() {
        //MainActivity.getCurrentLocation();
        Log.d("UpdatePosition: ", "Latidude: " + coordinates.latitude.toString() + "Longitude: " + coordinates.longitude.toString());
    }
}
