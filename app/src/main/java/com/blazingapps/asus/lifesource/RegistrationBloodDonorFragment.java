package com.blazingapps.asus.lifesource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationBloodDonorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationBloodDonorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationBloodDonorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQCODE = 1;
    private static final String DONOR_REF = "blooddonors";
    private static final String DONOR_NAME ="donorname";
    private static final String DONOR_ADDRESS = "donoraddress";
    private static final String DONOR_LATITUDE ="donorlatitude";
    private static final String DONOR_LONGITUDE = "donorlongitude";
    private static final String DONOR_CONTACT = "donorcontact";
    private static final String DONOR_GROUP ="donorgroup";
    private static final String MYPREF = "mypreferences";
    private static final String MON = "monday";
    private static final String TUE = "tues";
    private static final String WED = "wed";
    private static final String THU = "thu";
    private static final String FRI = "fri";
    private static final String SAT = "sat";
    private static final String SUN = "sun";
    private static final String FROM_HOUR = "fromhour";
    private static final String TO_HOUR = "tohour";
    private static final String FROM_MIN = "frommin";
    private static final String TO_MIN = "tomin";
    private static final String REGISTERED = "registered";
    private static final String ADMIN = "admin";
    private static final String PUSH_KEY = "pushkey";

    boolean isphoneVerified = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    AlertDialog gpsdialog , regdialog;
    AlertDialog.Builder gpsbuilder , regbuilder;

    String addressglobal = "";


    LinearLayout locationlayout;
    Button nextbutton, locatebutton;
    TextView addresstextview, statetextview, citytextview, countrytextview, postalcodetextview, knownnametextview, lattextview, longtextview;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    Location currentlocation;

    LocationCallback locationCallback;

    private OnFragmentInteractionListener mListener;

    public RegistrationBloodDonorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationBloodDonorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationBloodDonorFragment newInstance(String param1, String param2) {
        RegistrationBloodDonorFragment fragment = new RegistrationBloodDonorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getActivity().getSharedPreferences(MYPREF,Context.MODE_PRIVATE);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_blood_donor, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nextbutton = view.findViewById(R.id.nextbutton);

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

        final EditText ename = view.findViewById(R.id.bname);
        final EditText econtact = view.findViewById(R.id.contactno);
        final Spinner spinner = view.findViewById(R.id.bgroup);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        locatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationUpdate();
            }
        });
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ename.getText().toString().length() < 2 || econtact.getText().toString().length()<8 ){
                    Toast.makeText(getContext(),"Enter details",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinner.getSelectedItemPosition() == 0 ){
                    Toast.makeText(getContext(),"Select Blood Group",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isphoneVerified) {
                    phoneNoVerification(econtact.getText().toString());
                    return;
                }
                final AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Registering Donor");
                builder.setMessage("Please wait ...");
                alertDialog = builder.create();

                if (currentlocation != null) {

                    if (alertDialog.isShowing() == false){
                        alertDialog.show();
                    }

                    final String name, contact, group ,address;

                    name = ename.getText().toString();
                    contact = econtact.getText().toString();
                    group = spinner.getSelectedItem().toString();

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
                            DatabaseReference myRef = database.getReference(DONOR_REF);

                            DonorObject donorObject = new DonorObject(name,group,contact,addressglobal,latitude,longitude);

                            String key = myRef.push().getKey();
                            editor.putString(PUSH_KEY,key);
                            editor.commit();
                            myRef.child(key).setValue(donorObject)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (alertDialog.isShowing()){
                                                alertDialog.dismiss();
                                            }

                                            editor.putString(DONOR_NAME,name);
                                            editor.putString(DONOR_ADDRESS,addressglobal);
                                            editor.putString(DONOR_GROUP,group);
                                            editor.putString(DONOR_CONTACT,contact);
                                            editor.putFloat(DONOR_LATITUDE,latitude);
                                            editor.putFloat(DONOR_LONGITUDE,longitude);
                                            editor.putBoolean(REGISTERED,true);
                                            editor.putString(ADMIN,"donor");
                                            editor.putBoolean(MON,true);
                                            editor.putBoolean(TUE,true);
                                            editor.putBoolean(WED,true);
                                            editor.putBoolean(THU,true);
                                            editor.putBoolean(FRI,true);
                                            editor.putBoolean(SAT,true);
                                            editor.putBoolean(SUN,true);
                                            editor.putInt(FROM_HOUR,8);
                                            editor.putInt(TO_HOUR,20);
                                            editor.putInt(FROM_MIN,00);
                                            editor.putInt(TO_MIN,00);
                                            editor.commit();

                                            Toast.makeText(getActivity(),"Registration Successful",Toast.LENGTH_SHORT).show();

                                            LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);

                                            Intent intent = new Intent(getActivity(),DonorHome.class);
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
