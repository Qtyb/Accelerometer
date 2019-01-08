package com.example.piotrkutyba.accelerometr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class  MainActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 18f;
    public static Coordinates coordinates;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int mSensorCounter;
    public  static float sensorXAcc;  // prawo lewo
    public  static float sensorYAcc; //góra dół, nieptrzebna dla nas
    public  static float sensorZAcc; //Przód tył
    private Boolean mStarted;
    private NewLocation mNewLocation;
    private Marker[] mMarkerArray;
    public TextView measurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("SensorReader: ","Constructor");
        mStarted = false;
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mNewLocation = new NewLocation();
        mMarkerArray = new Marker[5];
        measurement = new TextView(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("MainActivity: ", "Map is ready");
        setDynamicLayout();
        mMap = googleMap;

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }else{
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity: ", "onMapReady: No permission");
            try {
                TimeUnit.SECONDS.wait(1000);
            }catch (Exception e){
                Log.d("Excepttion","MainActivity.onMapReady Exception:" + e.toString());
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        coordinates = new Coordinates();

        getCurrentLocation();

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void getCurrentLocation() {
        Log.d("MainActivity: ", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity: ", "onMapReady: No permission");
            try {
                TimeUnit.SECONDS.wait(1000);
            }catch (Exception e){
            }
           // android.os.Process.killProcess(android.os.Process.myPid());
           // System.exit(1);
            return;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Log.d("MainActivity: ","onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();

                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Starting Position"));
                    coordinates.longitude = currentLocation.getLongitude();
                    coordinates.latitude = currentLocation.getLatitude();
                }else{
                    Log.d("MainActivity: ", "onComplete: current location is null");
                    Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void moveCamera(LatLng latLng, float zoom){
        Log.d("MainActivity: ", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void setDynamicLayout(){
        final Button startButton;
        final TextView dimmerTV;

        measurement = (TextView)findViewById(R.id.tv_measurment_id);
        startButton = (Button)findViewById(R.id.bt_start_id);
        dimmerTV = (TextView)findViewById(R.id.tv_start_id);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this, "Witam", Toast.LENGTH_SHORT).show();
                mStarted = true;
                startButton.setVisibility(View.GONE);
                dimmerTV.setVisibility(View.GONE);
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        try{
                            mSensorCounter++;
                            if(mSensorCounter%59==0)
                                mMap.clear();
                            mNewLocation.getNewLocation(sensorXAcc,sensorZAcc,sensorYAcc);
                            if(mSensorCounter%2==0) {
                                moveCamera(new LatLng(coordinates.latitude, coordinates.longitude),DEFAULT_ZOOM);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(coordinates.latitude, coordinates.longitude)));

                            }
                        }
                        catch (Exception e) {
                            // TODO: handle exception
                            Log.d("Interval EXCEPTION: ",e.getMessage());
                        }
                        finally{
                            //also call the same runnable to call it at regular interval
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(runnable);
            }
        });
    }
    @Override
    protected void onResume() {
        Log.d("SensorReader: ","onResume");

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorXAcc = event.values[0];  // prawo lewo
        sensorYAcc = event.values[1]; //góra dół, nieptrzebna dla nas
        sensorZAcc = event.values[2];
        measurement.setText("Oś x: " + sensorXAcc + "  Oś y: " + sensorYAcc + "  Oś z:" + sensorZAcc);
        Log.d("SensorReader: ","onSensorChanged " + sensorXAcc +" , "+ sensorYAcc +" , "+ sensorZAcc);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("SensorReader: ","onAccuracyChanged");
    }
}
