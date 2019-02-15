package com.blazingapps.asus.lifesource;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.geometry.LatLng;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MapActivityNav extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener , LocationListener{

    private static final String DONOR_REF = "blooddonors";

    private static final String DONOR_NAME ="donorname";
    private static final String DONOR_ADDRESS = "donoraddress";
    private static final String DONOR_LATITUDE ="donorlatitude";
    private static final String DONOR_LONGITUDE = "donorlongitude";
    private static final String DONOR_CONTACT = "donorcontact";
    private static final String DONOR_GROUP ="donorgroup";
    private static final String MYPREF = "mypreferences";
    private static final String REGISTERED = "registered";
    private static final String PUSH_KEY = "pushkey";
    private static final String ADMIN = "admin";
    private static final String STREAMING = "streaming";
    private static final String AVAILABLE = "availablity";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private MapView mapView;
    MapboxMap mapboxMap;
    String lat;
    String lon;
    private LocationManager mLocationManager;
    Location destlocation;
    Button button;
    String donorPhone="";
    private boolean notcounted=true;
    Button accept,reject;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        lat = uri.getQueryParameter("lat");
        lon = uri.getQueryParameter("lon");
        donorPhone = uri.getQueryParameter("phone");

        Mapbox.getInstance(this, "pk.eyJ1IjoibmF2YW5lZXRoc2FqIiwiYSI6ImNqb3ZnM2hlbTFoa2ozcWxoYXY0bndpNWYifQ.7PMJIi-GMu2yVPdLCFP7lg");
        setContentView(R.layout.activity_map_nav);
        mapView = findViewById(R.id.mapView);
        button = findViewById(R.id.button);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        sharedPreferences = getSharedPreferences(MYPREF,MODE_PRIVATE);
        editor=sharedPreferences.edit();

        destlocation = new Location("");
        destlocation.setLatitude(Float.valueOf(lat));
        destlocation.setLongitude(Float.valueOf(lon));

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,

                0, this);
        accept = findViewById(R.id.accept);
        reject=findViewById(R.id.reject);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                acknowledgeService();
//                updateAvailablity(false);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                deacknowledgeService();
//                updateAvailablity(true);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(Float.valueOf(lat),Float.valueOf(lon)))
                .title("Destination")
                .snippet("Reach Here"));
        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .zoom(9)
                .target(new LatLng(Float.valueOf(lat),Float.valueOf(lon)))
                .build());
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(this);
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAGZ", String.valueOf(location.distanceTo(destlocation)));
        button.setText(String.valueOf(location.distanceTo(destlocation)));
        if (location.distanceTo(destlocation) < 100 && notcounted){

            button.setText("You Have Reached The Destination");
            Log.d("TAGZ","Target Reached");
            mLocationManager.removeUpdates(this);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("serviced");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = 0;
                    if (dataSnapshot.getValue() != null){
                        count = (long) dataSnapshot.getValue();
                    }
                    count++;
                    myRef.setValue(count).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            notcounted=false;
                            Toast.makeText(getApplicationContext(),"You have reached destination",Toast.LENGTH_LONG).show();
                            updateAvailablity(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void updateAvailablity(boolean av){
        boolean isAvailable = av;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("available");
        myRef.setValue(isAvailable).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    editor.putBoolean(AVAILABLE,isAvailable);
                    editor.commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Snackbar.make(rootlayout,"Failed",Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


}