package com.blazingapps.asus.lifesource;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String MYPREF = "mypreferences";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String MOBILE = "mobile";
    private static final String FAX = "fax";
    private static final String EMAIL = "email";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    TextView nameProf,addressProf,mobileProf;
    Button buttonfinddonor;
    ListView listView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        nameProf = findViewById(R.id.profname);
        addressProf = findViewById(R.id.profaddress);
        mobileProf = findViewById(R.id.profmobile);
        buttonfinddonor = findViewById(R.id.finddonorbutton);
        listView = findViewById(R.id.listviewdonors);

        nameProf.setText(sharedPreferences.getString(NAME,""));
        addressProf.setText(sharedPreferences.getString(ADDRESS,""));
        mobileProf.setText(sharedPreferences.getString(MOBILE,""));


    }
}
