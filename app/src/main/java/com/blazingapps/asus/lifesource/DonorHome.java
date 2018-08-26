package com.blazingapps.asus.lifesource;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DonorHome extends AppCompatActivity {
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
    private static final int REQCODE = 1;


    Boolean liveStream = false;
    Boolean isAvailable = true;
    int z = 0;

    AlertDialog dialog;
    AlertDialog.Builder builder;

    TextView pname,paddress,platitude,plongitude,pcontact,pgroup,availabletextview,streamingtextview;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button updatelocationButton;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentlocation;
    String addressglobal;
    ImageView location_pin;
    LinearLayout availablitylayout;
    Button livestreambutton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Updating").setMessage("Please wait...");
        dialog = builder.create();

        pname = findViewById(R.id.bprofilename);
        paddress = findViewById(R.id.bprofileaddress);
        platitude = findViewById(R.id.blatitude);
        plongitude = findViewById(R.id.blongitude);
        pcontact = findViewById(R.id.bprofilecontact);
        pgroup = findViewById(R.id.bprofilegroup);
        updatelocationButton = findViewById(R.id.updatelocation);
        location_pin = findViewById(R.id.location_pin);
        livestreambutton = findViewById(R.id.buttonlivestream);
        availablitylayout = findViewById(R.id.availablitylayout);
        availabletextview = findViewById(R.id.availabletextview);
        streamingtextview = findViewById(R.id.streamingtextview);

        Animation bouncingeAnimation = AnimationUtils.loadAnimation(this, R.anim.bouncing);
        location_pin.setAnimation(bouncingeAnimation);

        pname.setText(sharedPreferences.getString(DONOR_NAME,""));
        paddress.setText(sharedPreferences.getString(DONOR_ADDRESS,""));
        platitude.setText(String.valueOf(sharedPreferences.getFloat(DONOR_LATITUDE,0)));
        plongitude.setText(String.valueOf(sharedPreferences.getFloat(DONOR_LONGITUDE,0)));
        pcontact.setText(sharedPreferences.getString(DONOR_CONTACT,""));
        pgroup.setText(sharedPreferences.getString(DONOR_GROUP,""));


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updatelocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirebaseDataBase();
            }
        });

        requestLocationUpdate();

        if (mAuth == null){
            Toast.makeText(this,"User not authenticated",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"User Authenticated",Toast.LENGTH_SHORT).show();
        }

        liveStream = sharedPreferences.getBoolean(STREAMING,false);
        if (liveStream){
            livestreambutton.setText("Disable");
            livestreambutton.setBackgroundResource(R.drawable.gradient_buttonpositive);
            editor.putBoolean(STREAMING,true);
            editor.commit();
            streamingtextview.setText("(Streaming)");

        }else {
            livestreambutton.setText("Enable");
            editor.putBoolean(STREAMING,false);
            livestreambutton.setBackgroundResource(R.drawable.gradient_buttonnegative);
            editor.commit();
            streamingtextview.setText("(Not streaming)");
        }

        isAvailable = sharedPreferences.getBoolean(AVAILABLE,true);
        if (isAvailable){
            availabletextview.setText("You are available");
        }else {
            availabletextview.setText("You are not available");
        }

        livestreambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveStream = !liveStream;
                if (liveStream){
                    livestreambutton.setText("Disable");
                    editor.putBoolean(STREAMING,true);
                    editor.commit();
                    livestreambutton.setBackgroundResource(R.drawable.gradient_buttonpositive);
                    streamingtextview.setText("(Streaming)");

                }else {
                    livestreambutton.setText("Enable");
                    editor.putBoolean(STREAMING,false);
                    editor.commit();
                    livestreambutton.setBackgroundResource(R.drawable.gradient_buttonnegative);
                    streamingtextview.setText("(Not streaming)");
                }
            }
        });
        availablitylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAvailablity();
            }
        });

    }
    public void requestLocationUpdate(){
        //z++;
        //Log.d("countzzzzz", String.valueOf(z));
        final LocationManager manager = (LocationManager)getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //Log.d("request","called");
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d("koundzzzz","called");
                    if (locationResult == null) {
                        Log.d("koundzzz","null");
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            //TODO: UI updates.
                            locateUser();
                        }
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this,"REq",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},REQCODE);

            }else {
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        }
    }

    public void locateUser(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},REQCODE);
        }else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                String loc = String.valueOf(location.getLatitude()) + "\n" +  String.valueOf(location.getLongitude());
                                currentlocation = location;

                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                String address = "",city= "",state= "",country="",postalcode="",knownname="";

                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    address = addresses.get(0).getAddressLine(0)+""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                                    city = addresses.get(0).getLocality();
//                                    state = addresses.get(0).getAdminArea();
//                                    country = addresses.get(0).getCountryName();
//                                    postalcode = addresses.get(0).getPostalCode();
//                                    knownname = addresses.get(0).getFeatureName();

                                    addressglobal = address;
                                    paddress.setText(address);
                                    editor.putString(DONOR_ADDRESS,address);
                                    editor.putFloat(DONOR_LATITUDE, (float) location.getLatitude());
                                    editor.putFloat(DONOR_LONGITUDE, (float) location.getLongitude());
                                    editor.commit();
                                    platitude.setText(String.valueOf(location.getLatitude()));
                                    plongitude.setText(String.valueOf(location.getLongitude()));

                                    if (liveStream){
                                        updateFirebaseDataBase();
                                    }

                                    //Toast.makeText(getActivity(),address,Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //Toast.makeText(getActivity(),loc,Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(),"Update Successful",Toast.LENGTH_SHORT).show();

                                Log.d("locationz",loc);


                            }else {
                                Log.d("locationz","No location");
                                Toast.makeText(getApplicationContext(),"Turn On GPS",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("grandreslength", String.valueOf(grantResults.length));
        switch (requestCode)
        {
            case REQCODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    requestLocationUpdate();
                }else {
                    Toast.makeText(this,"Insufficent Permission",Toast.LENGTH_LONG).show();
                }
        }
    }

    public void updateFirebaseDataBase(){
        updatelocationButton.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.rotation) );
        updatelocationButton.setEnabled(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DONOR_REF);
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("address",sharedPreferences.getString(DONOR_ADDRESS,""));
        hashMap.put("latitude",sharedPreferences.getFloat(DONOR_LATITUDE,0));
        hashMap.put("longitude",sharedPreferences.getFloat(DONOR_LONGITUDE,0));
        myRef.child(sharedPreferences.getString(PUSH_KEY,"")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Synchronized",Toast.LENGTH_SHORT).show();
                updatelocationButton.setEnabled(true);
                updatelocationButton.clearAnimation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                updatelocationButton.setEnabled(true);
                updatelocationButton.clearAnimation();
            }
        });
    }
    public void updateAvailablity(){
        if (!dialog.isShowing()){
            dialog.show();
        }
        isAvailable = !isAvailable;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("available");
        myRef.setValue(isAvailable).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                if (isAvailable){
                    availabletextview.setText("You are available");
                    editor.putBoolean(AVAILABLE,true);
                    editor.commit();
                }else {
                    availabletextview.setText("You are not available");
                    editor.putBoolean(AVAILABLE,false);
                    editor.commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                isAvailable = !isAvailable;
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
    }
}
