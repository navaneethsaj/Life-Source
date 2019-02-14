package com.blazingapps.asus.lifesource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


public class MapActivityNavAmbu extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener , LocationListener{

    private static final String MYPREF = "mypreferences";
    private static final String PUSH_KEY = "pushkey";
    private static final String DONOR_REF = "blooddonors";
    private static final String DONOR_NAME ="donorname";
    private static final String DONOR_ADDRESS = "donoraddress";
    private static final String DONOR_LATITUDE ="donorlatitude";
    private static final String DONOR_LONGITUDE = "donorlongitude";
    private static final String DONOR_CONTACT = "donorcontact";

    private static final String ISAVAILABLE = "available";

    SharedPreferences sharedPreferences;
    private static final String AMB_REF = "ambulanceref";

    private MapView mapView;
    MapboxMap mapboxMap;
    String lat;
    String lon;
    private LocationManager mLocationManager;
    Location destlocation;
    Button button;
    private boolean notcounted=true;
    private boolean isAvailable = true;

    SharedPreferences.Editor editor;
    String phone="";
    Button accept,reject;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        lat = uri.getQueryParameter("lat");
        lon = uri.getQueryParameter("lon");
        phone = uri.getQueryParameter("phone");
        Mapbox.getInstance(this, "pk.eyJ1IjoibmF2YW5lZXRoc2FqIiwiYSI6ImNqb3ZnM2hlbTFoa2ozcWxoYXY0bndpNWYifQ.7PMJIi-GMu2yVPdLCFP7lg");
        setContentView(R.layout.activity_map_nav);
        mapView = findViewById(R.id.mapView);
        button = findViewById(R.id.button);
        accept = findViewById(R.id.accept);
        reject=findViewById(R.id.reject);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        destlocation = new Location("");
        destlocation.setLatitude(Float.valueOf(lat));
        destlocation.setLongitude(Float.valueOf(lon));

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                acknowledgeService();
                updateAvailablity(false);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deacknowledgeService();
                updateAvailablity(true);
            }
        });
    }

    private void deacknowledgeService() {
        //String smstext = "An ambulance will arrive shortly"+
//                "contact no : "+sharedPreferences.getString(DONOR_NAME,"")+" , "+sharedPreferences.getString(DONOR_CONTACT,"")
//                //+"\nlocate us : "+"https://maps.google.com/?q="+String.valueOf(sharedPreferences.getFloat(LATITUDE,0))
//                //+","+String.valueOf(sharedPreferences.getFloat(LONGITUDE,0))
//                ;
//        String locateustxt="location : "+"http://lifesource.com/locateaccident?lat="+String.valueOf(sharedPreferences.getFloat(DONOR_LATITUDE,0))
//                +"&lon="+String.valueOf(sharedPreferences.getFloat(DONOR_LONGITUDE,0));
//        //Log.d("TAG",smstext);
                String msg = "Request rejected";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, msg, null, null);
        //smsManager.sendTextMessage(mobile, null, locateustxt, null, null);
        Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_LONG).show();
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
            DatabaseReference myRef = database.getReference(AMB_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("serviced");
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),":(",Toast.LENGTH_LONG).show();

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

    public void acknowledgeService(){
//        String smstext = "An ambulance will arrive shortly"+
//                "contact no : "+sharedPreferences.getString(DONOR_NAME,"")+" , "+sharedPreferences.getString(DONOR_CONTACT,"")
//                //+"\nlocate us : "+"https://maps.google.com/?q="+String.valueOf(sharedPreferences.getFloat(LATITUDE,0))
//                //+","+String.valueOf(sharedPreferences.getFloat(LONGITUDE,0))
//                ;
//        String locateustxt="location : "+"http://lifesource.com/locateaccident?lat="+String.valueOf(sharedPreferences.getFloat(DONOR_LATITUDE,0))
//                +"&lon="+String.valueOf(sharedPreferences.getFloat(DONOR_LONGITUDE,0));
//        //Log.d("TAG",smstext);
        String msg = "An ambulance will arrive shortly";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, msg, null, null);
        //smsManager.sendTextMessage(mobile, null, locateustxt, null, null);
        Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_LONG).show();
    }

    public void updateAvailablity(boolean av){
        isAvailable = av;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(AMB_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("available");
        myRef.setValue(isAvailable).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (isAvailable){
                    editor.putBoolean(ISAVAILABLE,true);
                    editor.commit();
                }else {

                    editor.putBoolean(ISAVAILABLE,false);
                    editor.commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                isAvailable = !isAvailable;
            }
        });
    }
}