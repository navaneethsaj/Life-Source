package com.blazingapps.asus.lifesource;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 1500;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    SharedPreferences sharedPreferences;
    private static final String MYPREF = "mypreferences";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String MOBILE = "mobile";
    private static final String FAX = "fax";
    private static final String EMAIL = "email";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String HOSPITAL_ID = "hospitalid";
    private static final String REGISTERED = "registered";
    private static final String ADMIN = "admin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
//        Intent mainIntent = new Intent(SplashScreen.this,MapActivityNav.class);
//        startActivity(mainIntent);
//        finish();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (sharedPreferences.getBoolean(REGISTERED, false))
                {
                    if (sharedPreferences.getString(ADMIN,"").equals("hospital"))
                    {
                        Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                    else if (sharedPreferences.getString(ADMIN,"").equals("donor"))
                    {
                        Intent mainIntent = new Intent(SplashScreen.this,DonorHome.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }else {
                    Intent mainIntent = new Intent(SplashScreen.this,Registration.class);
                    startActivity(mainIntent);
                    finish();
                }
//                Intent mainIntent = new Intent(SplashScreen.this,Registration.class);
//                startActivity(mainIntent);
//                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_PERMISSIONS_REQUEST_SEND_SMS && grantResults.length>0){


        }else {

            Toast.makeText(getApplicationContext(),"Permission Needed",Toast.LENGTH_SHORT).show();
        }
    }
}
