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
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AmbuActivity extends AppCompatActivity {
    private static final String PUSH_KEY = "pushkey";
    private static final String AMB_REF = "ambulanceref";


    private static final String AMBNAME = "ambulancename";
    private static final String AMBPHONE = "ambulancephone";
    private static final String AMBVEHICLE = "ambulancevehicleno";

    private static final String LATITUDE = "ambulancelatitude";
    private static final String LONGITUDE = "ambulanclongi";
    private static final String MYPREF = "mypreferences";
    private static final String REGISTERED = "registered";

    private static final String ADMIN = "admin";
    private static final int REQCODE = 99;
    private static final String ISAVAILABLE = "available";

    private FirebaseAuth mAuth;
    TextView name,phone,vehicle;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button availablityButton;
    private boolean isAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambu);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        availablityButton = findViewById(R.id.availablityambulancebutton);
        name = findViewById(R.id.apname);
        phone=findViewById(R.id.apphone);
        vehicle=findViewById(R.id.appvehicleno);
        name.setText(sharedPreferences.getString(AMBNAME,""));
        phone.setText(sharedPreferences.getString(AMBPHONE,""));
        vehicle.setText(sharedPreferences.getString(AMBVEHICLE,""));
        requestLocationUpdate();
        isAvailable = sharedPreferences.getBoolean(ISAVAILABLE,true);
        if (isAvailable){
            availablityButton.setText("Available");
        }else {
            availablityButton.setText("Not Available");
        }
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhoneNo();
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
            mLocationRequest.setInterval(10000);
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
                            Toast.makeText(getApplicationContext(),String.valueOf(location.getLongitude()),Toast.LENGTH_LONG).show();
                            editor.putFloat(LATITUDE, (float) location.getLatitude());
                            editor.putFloat(LONGITUDE, (float) location.getLongitude());
                            editor.commit();
                            updateFirebaseDataBase();
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

    public void updateFirebaseDataBase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(AMB_REF);
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("latitude",sharedPreferences.getFloat(LATITUDE,0));
        hashMap.put("longitude",sharedPreferences.getFloat(LONGITUDE,0));
        myRef.child(sharedPreferences.getString(PUSH_KEY,"")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("grandreslength", String.valueOf(grantResults.length));
        switch (requestCode)
        {
            case REQCODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    requestLocationUpdate();
                }else {
                    Toast.makeText(getApplicationContext(),"Insufficient Permission",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this,"Insufficent Permission",Toast.LENGTH_LONG).show();
                }
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

    public void updateAvailablity(View view){
        isAvailable = !isAvailable;
        if (isAvailable){
            availablityButton.setText("Available");
        }else {
            availablityButton.setText("NOt available");
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(AMB_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("available");
        myRef.setValue(isAvailable).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (isAvailable){
                    availablityButton.setText("Available");
                    editor.putBoolean(ISAVAILABLE,true);
                    editor.commit();
                }else {
                    availablityButton.setText("Not Available");
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

    public void reject(View view) {
    }

    public void accept(View view) {
    }

    private void updatePhoneNo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setPadding(20,20,20,20);
        builder.setTitle("Phone Number").setView(editText).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String phoneno = editText.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(AMB_REF);
                HashMap<String ,Object> hashMap = new HashMap<>();
                hashMap.put("contactno",phoneno);
                myRef.child(sharedPreferences.getString(PUSH_KEY,"")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Snackbar.make(rootlayout,"Updated",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                        editor.putString(AMBPHONE,phoneno);
                        editor.commit();
                        phone.setText(phoneno);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Snackbar.make(rootlayout,"Failed",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
