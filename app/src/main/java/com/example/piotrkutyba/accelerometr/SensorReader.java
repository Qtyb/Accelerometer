package com.example.piotrkutyba.accelerometr;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static com.example.piotrkutyba.accelerometr.MainActivity.coordinates;


public class SensorReader extends Activity implements SensorEventListener {
    public  SensorManager mSensorManager;
    public  Sensor mAccelerometer;
    private boolean mInitialized;

    public SensorReader(SensorManager mSM) {
        Log.d("SensorReader: ","Constructor");
        mSensorManager = mSM;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 }
    @Override
    protected void onResume() {
        Log.d("SensorReader: ","onResume");

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        Log.d("SensorReader: ","onPause");
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("SensorReader: ","onAccuracyChanged");
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SensorReader: ","onSensorChanged");

        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];
        Log.d("SensorReader: ","onSensorChanged " + x.toString() +" , "+ y.toString() +" , "+ z.toString());
    }
}