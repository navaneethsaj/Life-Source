package com.blazingapps.asus.lifesource;

import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class AmbulanceRegisterFragment extends Fragment {

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
    AlertDialog gpsdialog , regdialog;
    AlertDialog.Builder gpsbuilder , regbuilder;

    String addressglobal = "";

    boolean isphoneVerified = false;

    LinearLayout locationlayout;
    Button nextbutton, locatebutton;
    TextView addresstextview, statetextview, citytextview, countrytextview, postalcodetextview, knownnametextview, lattextview, longtextview;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    Location currentlocation;

    LocationCallback locationCallback;

    public AmbulanceRegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AmbulanceRegisterFragment newInstance(String param1, String param2) {
        AmbulanceRegisterFragment fragment = new AmbulanceRegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        gpsbuilder = new AlertDialog.Builder(getActivity());
        gpsbuilder.setTitle("Locating");
        gpsbuilder.setMessage("Please wait ...");
        gpsbuilder.setCancelable(false);
        gpsdialog = gpsbuilder.create();

        regbuilder = new AlertDialog.Builder(getActivity());
        regbuilder.setTitle("Registering");
        regbuilder.setCancelable(false);
        regbuilder.setMessage("Please wait ...");
        regdialog = regbuilder.create();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_ambulance_register, container, false);nextbutton = view.findViewById(R.id.nextbutton);

        addresstextview = view.findViewById(R.id.addressview);
        citytextview = view.findViewById(R.id.cityview);
        statetextview = view.findViewById(R.id.stateview);
        countrytextview = view.findViewById(R.id.countryview);
        postalcodetextview = view.findViewById(R.id.postalcodeview);
        knownnametextview = view.findViewById(R.id.knownameview);
        lattextview = view.findViewById(R.id.latitudeview);
        longtextview = view.findViewById(R.id.longitudeview);

        locationlayout = view.findViewById(R.id.locationlayout);
        locatebutton = view.findViewById(R.id.locate);

        final EditText ename = view.findViewById(R.id.aname);
        final EditText econtact = view.findViewById(R.id.acontactno);
        final EditText vehicleid = view.findViewById(R.id.avehicleid);
        locatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationUpdate();
            }
        });
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ename.getText().toString().length() < 2 || econtact.getText().toString().length()<8 || vehicleid.getText().toString().length() <2){
                    Toast.makeText(getContext(),"Enter details",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isphoneVerified) {
                    phoneNoVerification(econtact.getText().toString());
                    return;
                }
                final AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Registering");
                builder.setMessage("Please wait ...");
                alertDialog = builder.create();

                if (currentlocation != null) {

                    if (alertDialog.isShowing() == false){
                        alertDialog.show();
                    }

                    final String name, contact, vehicleno ,address;

                    name = ename.getText().toString();
                    contact = econtact.getText().toString();
                    vehicleno = vehicleid.getText().toString();

                    final float latitude = (float) currentlocation.getLatitude();
                    final float longitude = (float) currentlocation.getLongitude();

                    mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            final String userauthtoken = user.getUid();
                            Log.d("usertoken",userauthtoken);
                            Log.d("auth :","success");

                            Toast.makeText(getActivity(),"Authentication Successful",Toast.LENGTH_LONG).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(AMB_REF);

                            AmbObject ambObject = new AmbObject(name,vehicleno,contact,latitude,longitude);
                            String key = myRef.push().getKey();
                            editor.putString(PUSH_KEY,key);
                            editor.commit();
                            myRef.child(key).setValue(ambObject)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (alertDialog.isShowing()){
                                                alertDialog.dismiss();
                                            }

                                            editor.putString(AMBNAME,name);
                                            editor.putString(AMBPHONE,contact);
                                            editor.putString(AMBVEHICLE,vehicleno);
                                            editor.putFloat(LATITUDE,latitude);
                                            editor.putFloat(LONGITUDE,longitude);
                                            editor.putBoolean(REGISTERED,true);
                                            editor.putString(ADMIN,"ambulance");
                                            editor.commit();

                                            Toast.makeText(getActivity(),"Registration Successful",Toast.LENGTH_SHORT).show();

                                            LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);

                                            Intent intent = new Intent(getActivity(),AmbuActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (alertDialog.isShowing()){
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (alertDialog.isShowing()){
                                alertDialog.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getActivity(),"Specify Location",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
    public void requestLocationUpdate(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{
            if (!gpsdialog.isShowing()){
                gpsdialog.show();
            }
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {

                        if (gpsdialog.isShowing()){
                            gpsdialog.dismiss();
                        }
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            //TODO: UI updates.

                            locateUser();

                        }
                    }
                }
            };

            locationCallback = mLocationCallback;
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(getActivity(),"REq",Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},REQCODE);

            }else {
                LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void locateUser(){
        locationlayout.setEnabled(false);
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},REQCODE);
//        }else {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            String loc = String.valueOf(location.getLatitude()) + "\n" +  String.valueOf(location.getLongitude());
                            currentlocation = location;

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());

                            String address = "",city= "",state= "",country="",postalcode="",knownname="";

                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                address = addresses.get(0).getAddressLine(0) + ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality() + "";
                                state = addresses.get(0).getAdminArea() + "";
                                country = addresses.get(0).getCountryName() + "";
                                postalcode = addresses.get(0).getPostalCode() + "";
                                knownname = addresses.get(0).getFeatureName() + "";

                                addressglobal = address;
                                //Toast.makeText(getActivity(),address,Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addresstextview.setText(address);
                            citytextview.setText(city);
                            statetextview.setText(state);
                            countrytextview.setText(country);
                            postalcodetextview.setText(postalcode);
                            knownnametextview.setText(knownname);
                            lattextview.setText(String.valueOf(location.getLatitude()));
                            longtextview.setText(String.valueOf(location.getLongitude()));
                            //Toast.makeText(getActivity(),loc,Toast.LENGTH_SHORT).show();
                            Log.d("locationz",loc);
                        }else {
                            Log.d("locationz","No location");
                            Toast.makeText(getActivity(),"Turn On GPS",Toast.LENGTH_SHORT).show();
                        }

                        if (gpsdialog.isShowing()){
                            gpsdialog.dismiss();
                        }
                    }
                });

        locationlayout.setEnabled(true);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("grandreslength", String.valueOf(grantResults.length));
        if (gpsdialog.isShowing()){
            gpsdialog.dismiss();
        }
        switch (requestCode)
        {
            case REQCODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    requestLocationUpdate();
                }else {
                    Toast.makeText(getActivity(),"Insufficent Permission",Toast.LENGTH_LONG).show();
                }
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    private void phoneNoVerification(String s){
        String phoneNumber = "+91" + s;
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setCancelable(false).setTitle("Verifying Phone No").setMessage("Please Wait...").create();
        if (!dialog.isShowing()){
            dialog.show();
        }
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getActivity(),"Phone Verified",Toast.LENGTH_SHORT).show();
                isphoneVerified = true;
                nextbutton.callOnClick();
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("phone", String.valueOf(e));
                Toast.makeText(getActivity(),"Phone Verification Failed",Toast.LENGTH_SHORT).show();
                isphoneVerified = false;
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.d("firebase phone",s);
                Toast.makeText(getActivity(),"Phone Verification Timed Out",Toast.LENGTH_SHORT).show();
                isphoneVerified = false;
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);
    }
}
