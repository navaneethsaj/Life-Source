package com.blazingapps.asus.lifesource;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorAdapter extends ArrayAdapter<DoctorObject> {
    ArrayList<DoctorObject> doctorObjects;
    Context context;
    SharedPreferences sharedPreferences;
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

    public DoctorAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context,resource,objects);
        doctorObjects = objects;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MYPREF,Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return doctorObjects.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layout_doctors, null);
        return v;
    }
}
