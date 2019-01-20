package com.example.piotrkutyba.accelerometr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.sin;

public class  MainActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 18f;
    public static Coordinates coordinates;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravityAccelerometer;
    private int mSensorCounter;
    public  static float sensorXAcc;  // prawo lewo
    public  static float sensorYAcc; //góra dół, nieptrzebna dla nas
    public  static float sensorZAcc; //Przód tył
    private float tmpSensorXAcc;  // prawo lewo
    private float tmpSensorYAcc; //góra dół, nieptrzebna dla nas
    private float tmpSensorZAcc; //Przód tył
    private NewLocation mNewLocation;
    public TextView measurement;
    private Marker mRaceMarker;
    private Polyline mRacePolyline;
    private Boolean mRaceStarted;
    private Stopwatch mStopwatch;
    private Marker[] mMarkerArray;
    private TextView stopWatchTV;
    private RaceStatus raceChecker;


    //magnietic
    private ImageView imageView;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
    float orientation[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mRaceStarted = false;
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravityAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        measurement = new TextView(this);
        mStopwatch = new Stopwatch();
        mNewLocation = new NewLocation();
        raceChecker = new RaceStatus();

        //TODO Tablica z markerami
        mMarkerArray = new Marker[5];


        //magnetic
        imageView = (ImageView)findViewById(R.id.transparent_compass2);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("INFO: ", "MainActivity.onMapReady: Map is ready");
        setDynamicLayout();
        mMap = googleMap;

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity: ", "onMapReady: No permission");
            try {
                TimeUnit.SECONDS.wait(1000);
            }catch (Exception e){
                Log.d("EXCEPTION: ","MainActivity.onMapReady: " + e.toString());
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng position) {
                if(mRaceMarker==null)
                    mRaceMarker = mMap.addMarker(new MarkerOptions().position(position).title("Finish").icon(BitmapDescriptorFactory.fromResource(R.drawable.finish)));
            }
        });
        coordinates = new Coordinates();
        getCurrentLocation();
    }
    public void getCurrentLocation() {
        Log.d("INFO: ", "MainActivity.getCurrentLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("INFO: ", "MainActivity.getCurrentLocation: No permission");
            try {
                TimeUnit.SECONDS.wait(5000);
            }catch (Exception e){}
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Log.d("INFO: ","MainActivity.getCurrentLocation: found location!");
                    Location currentLocation = (Location) task.getResult();
                    try{
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Starting Position"));
                        coordinates.longitude = currentLocation.getLongitude();
                        coordinates.latitude = currentLocation.getLatitude();
                    }
                    catch (Exception e){
                        Log.d("EXCEPTION: ","MainActivity.getCurrentLocation: " + e.getMessage());
                    }
                }else{
                    Log.d("INFO: ", "MainActivity.getCurrentLocation: current location is null");
                    Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void moveCamera(LatLng latLng, float zoom){
        Log.d("INFO: ", "moveCamera: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void updatePosition(){
        try{
            mSensorCounter++;
            if(mSensorCounter%59==0) {
                mMap.clear();
                mRaceMarker = mMap.addMarker(new MarkerOptions().position(mRaceMarker.getPosition()).title("Finish").icon(BitmapDescriptorFactory.fromResource(R.drawable.finish)));
                mRacePolyline = mRacePolyline = mMap.addPolyline(new PolylineOptions()
                        .add(mRacePolyline.getPoints().get(0),mRacePolyline.getPoints().get(1))
                        .width(5)
                        .color(Color.RED));
            }
            mNewLocation.getNewLocation(sensorXAcc, sensorZAcc,sensorXAcc);
            if(mSensorCounter%2==0) {
                moveCamera(new LatLng(coordinates.latitude, coordinates.longitude),DEFAULT_ZOOM);
                if(mRaceStarted) {
                    if (raceChecker.checkRaceStatus(new LatLng(coordinates.latitude, coordinates.longitude), mRaceMarker.getPosition(), 0.00007)) {
                        mRaceMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.trophy));
                    }
                }mMap.addMarker(new MarkerOptions().position(new LatLng(coordinates.latitude, coordinates.longitude)));
            }
        }
        catch (Exception e) {
            Log.d("EXCEPTION: ", String.format("updatePosition: %s", e.getMessage()));
        }
    }
    private void reset(){
        try{
            mRaceStarted = false;
            mRaceMarker = null;
            mRacePolyline = null;
            stopWatchTV.setVisibility(View.INVISIBLE);
            mStopwatch.resetStopwatch();
            mMap.clear();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "reset EXCEPTION: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("EXCEPTION: ", String.format("reset: %s", e.getMessage()));
        }
    }
    private void setDynamicLayout(){
        final Button startButton;
        final Button renewButton;
        final Button backButton;
        final Button startRaceButton;
        final TextView dimmerTV;

        measurement = (TextView)findViewById(R.id.tv_measurment_id);
        startButton = (Button)findViewById(R.id.bt_start_id);
        dimmerTV = (TextView)findViewById(R.id.tv_start_id);
        renewButton = (Button)findViewById(R.id.bt_renew_id);
        startRaceButton = (Button)findViewById(R.id.bt_race_id);
        stopWatchTV = (TextView)findViewById(R.id.tv_stopWatch_id);
        backButton = (Button)findViewById(R.id.bt_back_id);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRaceButton.setVisibility(View.GONE);
                renewButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                dimmerTV.setVisibility(View.VISIBLE);
                reset();
            }
        });
        startRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mRacePolyline = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(coordinates.latitude, coordinates.longitude), new LatLng(mRaceMarker.getPosition().latitude,mRaceMarker.getPosition().longitude))
                            .width(5)
                            .color(Color.RED));
                    mStopwatch.turnOnStopwatch();
                    mRaceStarted = true;
                    startRaceButton.setEnabled(false);
                    startRaceButton.setBackgroundColor(getResources().getColor(R.color.Disabled));
                    backButton.setEnabled(false);
                    backButton.setBackgroundColor(getResources().getColor(R.color.Disabled));
                    stopWatchTV.setVisibility(View.VISIBLE);

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "startRace EXCEPTION: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("EXCEPTION: ", String.format("startRace: %s", e.getMessage()));
                }

            }
        });

        renewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    reset();
                    startRaceButton.setEnabled(true);
                    startRaceButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    backButton.setEnabled(true);
                    backButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    getCurrentLocation();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "renew EXCEPTION: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("EXCEPTION: ", String.format("renew: %s", e.getMessage()));
                }
            }
        });
        startButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startRaceButton.setVisibility(View.VISIBLE);
                renewButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                dimmerTV.setVisibility(View.GONE);
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        updatePosition();
                        if(mRaceStarted)
                            stopWatchTV.setText(mStopwatch.printForStopWatch());
                        handler.postDelayed(this, 1000);
                        }
                };
                handler.post(runnable);
            }
        });
    }
    @Override
    protected void onResume() {
        Log.d("INFO: ","SensorsReader: onResume");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravityAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();

        //magnetic
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

        }
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            sensorXAcc = tmpSensorXAcc - Math.abs(event.values[0]);  // prawo lewo
            sensorYAcc = tmpSensorYAcc - Math.abs(event.values[1]); //góra dół, nieptrzebna dla nas
            sensorZAcc = tmpSensorZAcc - Math.abs(event.values[2]);
            measurement.setText("Oś x: " + sensorXAcc + "  Oś y: " + sensorYAcc + "  Oś z:" + sensorZAcc);
        //    Log.d("DEBUG: ","Gravity:  " + event.values[0] +" , "+ event.values[1] +" , "+ event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            tmpSensorXAcc = event.values[0];  // prawo lewo
            tmpSensorYAcc = event.values[1]; //góra dół, nieptrzebna dla nas
            tmpSensorZAcc = event.values[2];
         //   Log.d("DEBUG: ","ACCELEROMETER:  x: " + event.values[0] +" , y: "+ event.values[1] +" , z: "+ event.values[2]);
        }

        //magnetic
        final float alpha = 0.97f;
        synchronized (this) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha * mGravity[0] + (1-alpha) * event.values[0];  //przyspieszenie
                mGravity[1] = alpha + mGravity[1] + (1-alpha) * event.values[1];   //ziemskie w 3
                mGravity[2] = alpha + mGravity[2] + (1-alpha) * event.values[2];    //kierunkach
            }

            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1-alpha) * event.values[0]; //pole
                mGeomagnetic[1] = alpha + mGeomagnetic[1] + (1-alpha) * event.values[1];//magnetyczne
                mGeomagnetic[2] = alpha + mGeomagnetic[2] + (1-alpha) * event.values[2];// w 3 kierunkach


                //    Log.d("magnetic", "MAGNETIC Y: " + event.values[0] + " MAGNETIC X: "
                //          + event.values[1] + " MAGNETIC Z: " + event.values[2]);
            }

            float R[] = new float[9]; // macierz rotacji, gdy układ współrzędnych urządzenia
            // jest taki jak układ współrzędnych ziemi

            float I[] = new float[9]; // macierz rotacji dopasowana do układu współrzędnych urządzenia


            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
            // nieprawda np. gdy urządzenie jest w swobodnym spadku

            if(success){

                SensorManager.getOrientation(R,orientation);
                azimuth = (float)Math.toDegrees(orientation[0]); // kat wokol osi z (raczej mnie nie obchodzi)
                azimuth = (azimuth+360) % 360;

              //   Log.i("Orientation:", "orientation Y: " + Math.toDegrees(orientation[0]) +
                //       " orientation X: " + Math.toDegrees(orientation[1]) + " orientation Z: " + Math.toDegrees(orientation[2]));


                //animacja kompasu
                Animation anim = new RotateAnimation(-currentAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);

                //public RotateAnimation (float fromDegrees,
                //                float toDegrees,
                //                int pivotXType,
                //                float pivotXValue,
                //                int pivotYType,
                //                float pivotYValue)


                currentAzimuth = azimuth;

                anim.setDuration(500); //czas trwania animacji
                anim.setRepeatCount(0); // ile razy animacja powinna zostac powtorzona
                anim.setFillAfter(true); // prawda sprawia, że animacja rozciaga sie w czasie

                imageView.startAnimation(anim);
            }


        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("INFO: ","SensorsReader: onAccuracyChanged");
    }

    //magnetic
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
