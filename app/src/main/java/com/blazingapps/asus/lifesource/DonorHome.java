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
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.nex3z.togglebuttongroup.button.OnCheckedChangeListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import one.xcorp.widget.swipepicker.SwipePicker;

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

    Boolean liveStream = false;
    Boolean isAvailable = true;
    int z = 0;

    AlertDialog dialog;
    AlertDialog.Builder builder;

    Button timepicker;
    SwipePicker fromhour,tohour,frommin,tomin;
    MultiSelectToggleGroup groupweekdays;
    CircularToggle sun,mon,tue,wed,thu,fri,sat;
    TextView pname,paddress,platitude,plongitude,pcontact,pgroup,availabletextview,streamingtextview;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView bloodImageView;//askbutton,inboxbutton;
    Button updatelocationButton;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentlocation;
    String addressglobal;
    LinearLayout availablitylayout, profilelayout,locationlayout;
    RelativeLayout developerlayout,tipslayout;
    LinearLayout chatlayout;
    LinearLayout availablitybuttonslayout;
    Button livestreambutton;
    BottomNavigationView bottomNavigationView;
    RelativeLayout rootlayout,timelayout;
    MultiSelectToggleGroup weekdays;
    ImageView animcloud, tickimg,crossimg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Updating").setMessage("Please wait...").setCancelable(false);
        dialog = builder.create();

        rootlayout=findViewById(R.id.rootlayoutdonor);
        weekdays=findViewById(R.id.group_weekdays);
        timelayout=findViewById(R.id.timelayoutroot);
        groupweekdays = findViewById(R.id.group_weekdays);
        sun = findViewById(R.id.sun);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);

        fromhour = findViewById(R.id.fromhour);
        frommin = findViewById(R.id.frommin);
        tohour = findViewById(R.id.tohour);
        tomin = findViewById(R.id.tomin);

        pname = findViewById(R.id.bprofilename);
        paddress = findViewById(R.id.bprofileaddress);
        platitude = findViewById(R.id.blatitude);
        plongitude = findViewById(R.id.blongitude);
        pcontact = findViewById(R.id.bprofilecontact);
        pgroup = findViewById(R.id.bprofilegroup);
        updatelocationButton = findViewById(R.id.updatelocation);
        livestreambutton = findViewById(R.id.buttonlivestream);
        availablitylayout = findViewById(R.id.availablitylayout);
        availabletextview = findViewById(R.id.availabletextview);
        streamingtextview = findViewById(R.id.streamingtextview);
        developerlayout = findViewById(R.id.developerlayout);
        animcloud = findViewById(R.id.location_cloud);
        chatlayout = findViewById(R.id.chatlayout);
        locationlayout = findViewById(R.id.locationlayout);
        tickimg = findViewById(R.id.tickimg);
        availablitybuttonslayout = findViewById(R.id.availableswitches);
        profilelayout = findViewById(R.id.profilelayout);
        crossimg = findViewById(R.id.crossimg);
        bloodImageView = findViewById(R.id.bloodgroupImageview);
        bottomNavigationView = findViewById(R.id.bottomnavigator);
        tipslayout = findViewById(R.id.tipslayout);

        sun.setChecked(sharedPreferences.getBoolean(SUN,true));
        mon.setChecked(sharedPreferences.getBoolean(MON,true));
        tue.setChecked(sharedPreferences.getBoolean(TUE,true));
        wed.setChecked(sharedPreferences.getBoolean(WED,true));
        thu.setChecked(sharedPreferences.getBoolean(THU,true));
        fri.setChecked(sharedPreferences.getBoolean(FRI,true));
        sat.setChecked(sharedPreferences.getBoolean(SAT,true));

        sun.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("sun");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean(SUN,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        mon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("mon");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean(MON,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        tue.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("tue");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean(TUE,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        wed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("wed");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean(WED,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        thu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("thu");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor.putBoolean(THU,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        fri.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("fri");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putBoolean(FRI,isChecked);
                        editor.commit();
                    }
                });
            }
        });
        sat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public <T extends View & Checkable> void onCheckedChanged(T view, final boolean isChecked) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("sat");
                myRef.setValue(isChecked).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putBoolean(SAT,isChecked);
                        editor.commit();
                    }
                });
            }
        });

        fromhour.setValue(sharedPreferences.getInt(FROM_HOUR,8));
        fromhour.setMaxValue(sharedPreferences.getInt(TO_HOUR,20)-1);
        frommin.setValue(sharedPreferences.getInt(FROM_MIN,30));
        tohour.setValue(sharedPreferences.getInt(TO_HOUR,20));
        tohour.setMinValue(sharedPreferences.getInt(FROM_HOUR,8)+1);
        tomin.setValue(sharedPreferences.getInt(TO_MIN,30));


        fromhour.setOnValueChangeListener(new SwipePicker.OnValueChangeListener() {
            @Override
            public void onValueChanged(@NotNull SwipePicker swipePicker, float v, float v1) {
                final float z1 = v1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("fromhour");
                myRef.setValue((int)v1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putInt(FROM_HOUR, (int) z1);
                        editor.commit();
                        tohour.setMinValue(sharedPreferences.getInt(FROM_HOUR,8)+1);
                    }
                });
            }
        });
        frommin.setOnValueChangeListener(new SwipePicker.OnValueChangeListener() {
            @Override
            public void onValueChanged(@NotNull SwipePicker swipePicker, float v, float v1) {
                final float z1 = v1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("frommin");
                myRef.setValue((int)v1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putInt(FROM_MIN, (int) z1);
                        editor.commit();
                    }
                });
            }
        });
        tohour.setOnValueChangeListener(new SwipePicker.OnValueChangeListener() {
            @Override
            public void onValueChanged(@NotNull SwipePicker swipePicker, float v, float v1) {
                final float z1 = v1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("tohour");
                myRef.setValue((int)v1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putInt(TO_HOUR, (int) z1);
                        editor.commit();
                        fromhour.setMaxValue(sharedPreferences.getInt(TO_HOUR,20)-1);
                    }
                });
            }
        });
        tomin.setOnValueChangeListener(new SwipePicker.OnValueChangeListener() {
            @Override
            public void onValueChanged(@NotNull SwipePicker swipePicker, float v, float v1) {
                final float z1 = v1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(sharedPreferences.getString(PUSH_KEY,null)).child("tomin");
                myRef.setValue((int)v1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        editor.putInt(TO_MIN, (int) z1);
                        editor.commit();
                    }
                });
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_profile:
                        profilelayout.setVisibility(View.VISIBLE);
                        availablitylayout.setVisibility(View.VISIBLE);
                        locationlayout.setVisibility(View.GONE);
                        developerlayout.setVisibility(View.GONE);
                        chatlayout.setVisibility(View.GONE);
                        tipslayout.setVisibility(View.GONE);
                        break;
                    case R.id.action_location:
                        locationlayout.setVisibility(View.VISIBLE);
                        profilelayout.setVisibility(View.GONE);
                        developerlayout.setVisibility(View.GONE);
                        availablitylayout.setVisibility(View.GONE);
                        chatlayout.setVisibility(View.GONE);
                        tipslayout.setVisibility(View.GONE);
                        break;
                    case R.id.action_developers:
                        locationlayout.setVisibility(View.GONE);
                        profilelayout.setVisibility(View.GONE);
                        developerlayout.setVisibility(View.VISIBLE);
                        chatlayout.setVisibility(View.GONE);
                        tipslayout.setVisibility(View.GONE);
                        availablitylayout.setVisibility(View.GONE);
                        break;
                    case R.id.action_tips:
                        locationlayout.setVisibility(View.GONE);
                        chatlayout.setVisibility(View.GONE);
                        profilelayout.setVisibility(View.GONE);
                        developerlayout.setVisibility(View.GONE);
                        tipslayout.setVisibility(View.VISIBLE);
                        availablitylayout.setVisibility(View.GONE);
                        break;
                    case R.id.action_chat:
                        locationlayout.setVisibility(View.GONE);
                        profilelayout.setVisibility(View.GONE);
                        developerlayout.setVisibility(View.GONE);
                        tipslayout.setVisibility(View.GONE);
                        availablitylayout.setVisibility(View.GONE);
                        chatlayout.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);

        pname.setText(sharedPreferences.getString(DONOR_NAME,""));
        paddress.setText(sharedPreferences.getString(DONOR_ADDRESS,""));
        platitude.setText(String.valueOf(sharedPreferences.getFloat(DONOR_LATITUDE,0)));
        plongitude.setText(String.valueOf(sharedPreferences.getFloat(DONOR_LONGITUDE,0)));
        pcontact.setText(sharedPreferences.getString(DONOR_CONTACT,""));
        pgroup.setText(sharedPreferences.getString(DONOR_GROUP,""));

        bloodImageView.setImageResource(getBloodGroupImage(sharedPreferences.getString(DONOR_GROUP,"")));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updatelocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirebaseDataBase();
            }
        });

        requestLocationUpdate();

        if (mAuth == null){
            Snackbar.make(rootlayout,"User not authenticated",Snackbar.LENGTH_SHORT).show();
//            Toast.makeText(this,"User not authenticated",Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this,"User Authenticated",Toast.LENGTH_SHORT).show();
        }

        liveStream = sharedPreferences.getBoolean(STREAMING,false);
        if (liveStream){
            livestreambutton.setText("Disable");
            livestreambutton.setBackgroundResource(R.drawable.gradient_buttonpositive);
            editor.putBoolean(STREAMING,true);
            editor.commit();
            streamingtextview.setText("(Streaming)");
            animcloud.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.blink) );

        }else {
            livestreambutton.setText("Enable");
            editor.putBoolean(STREAMING,false);
            livestreambutton.setBackgroundResource(R.drawable.gradient_buttonnegative);
            editor.commit();
            streamingtextview.setText("(Not streaming)");
        }

        isAvailable = sharedPreferences.getBoolean(AVAILABLE,true);
        if (isAvailable){
            availabletextview.setText("You Are Available");
            tickimg.setBackground(getDrawable(R.drawable.gradient_selected));
            crossimg.setBackground(null);
        }else {
            availabletextview.setText("You Aren't Available");
            crossimg.setBackground(getDrawable(R.drawable.gradient_selected));
            tickimg.setBackground(null);
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
                    animcloud.startAnimation(
                            AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink) );

                }else {
                    livestreambutton.setText("Enable");
                    editor.putBoolean(STREAMING,false);
                    editor.commit();
                    livestreambutton.setBackgroundResource(R.drawable.gradient_buttonnegative);
                    streamingtextview.setText("(Not streaming)");
                    animcloud.clearAnimation();
                }
            }
        });
        availablitybuttonslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAvailablity();
            }
        });

        profilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhoneNo();
            }
        });

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
                DatabaseReference myRef = database.getReference(DONOR_REF);
                HashMap<String ,Object> hashMap = new HashMap<>();
                hashMap.put("contactno",phoneno);
                myRef.child(sharedPreferences.getString(PUSH_KEY,"")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(rootlayout,"Updated",Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                        editor.putString(DONOR_CONTACT,phoneno);
                        editor.commit();
                        pcontact.setText(phoneno);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootlayout,"Failed",Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                                //Toast.makeText(getApplicationContext(),"Turn On GPS",Toast.LENGTH_SHORT).show();
                                Snackbar.make(rootlayout,"Turn On GPS",Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(rootlayout,"Insufficient Permission",Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(this,"Insufficent Permission",Toast.LENGTH_LONG).show();
                }
        }
    }

    public void updateFirebaseDataBase(){
        updatelocationButton.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.upload) );
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
                //Toast.makeText(getApplicationContext(),"Synchronized",Toast.LENGTH_SHORT).show();
                updatelocationButton.setEnabled(true);
                updatelocationButton.clearAnimation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(rootlayout,"Failed",Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
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
                    availabletextview.setText("You Are Available");
                    editor.putBoolean(AVAILABLE,true);
                    tickimg.setBackground(getDrawable(R.drawable.gradient_selected));
                    crossimg.setBackground(null);
                    editor.commit();
                }else {
                    availabletextview.setText("You Aren't Available");
                    tickimg.setBackground(null);
                    crossimg.setBackground(getDrawable(R.drawable.gradient_selected));
                    editor.putBoolean(AVAILABLE,false);
                    editor.commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(rootlayout,"Failed",Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                isAvailable = !isAvailable;
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
    }

    private int getBloodGroupImage(String group){
        switch (group)
        {
            case "A+":
                return R.drawable.aplus;
            case "A-":
                return R.drawable.aminus;
            case "B+":
                return R.drawable.bplus;
            case "B-":
                return R.drawable.bminus;
            case "O+":
                return R.drawable.oplus;
            case "O-":
                return R.drawable.ominus;
            case "AB+":
                return R.drawable.abplus;
            case "AB-":
                return R.drawable.abminus;
                default:
                    return 0;
        }
    }

}
