package com.example.piotrkutyba.accelerometr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        createDynamicLayout();
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity: ", "Map is ready");
        mMap = googleMap;
        Button btn= new Button(this);
        btn.setText("Submit");
        btn.bringToFront();
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this, "dupa", Toast.LENGTH_SHORT).show();
            }
        });
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);

            //mMap.addMarker(new MarkerOptions().getPosition())
        }else{
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity: ", "onMapReady: No permission");
            try {
                TimeUnit.SECONDS.wait(1000);
            }catch (Exception e){
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        Toast.makeText(MainActivity.this, (CharSequence) mMap.getMyLocation(), Toast.LENGTH_SHORT).show();
        getCurrentLocation();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void getCurrentLocation() {
        Log.d("MainActivity: ", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity: ", "onMapReady: No permission");
            try {
                TimeUnit.SECONDS.wait(1000);
            }catch (Exception e){
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Log.d("MainActivity: ","onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();

                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            DEFAULT_ZOOM);

                }else{
                    Log.d("MainActivity: ", "onComplete: current location is null");
                    Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d("MainActivity: ", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void createDynamicLayout() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final TextView txtView = new TextView(this);
        txtView.setBackgroundColor(00574);
        txtView.setWidth(size.x);
        txtView.setHeight(size.y);

        final Button btn = new Button(this);
        btn.setText("Start");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btn.setVisibility(btn.GONE);
            }
        });
    }
}
