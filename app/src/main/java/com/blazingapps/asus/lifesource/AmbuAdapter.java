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

public class AmbuAdapter extends ArrayAdapter<RespDonorObj> {

    private static final String PUSH_KEY = "pushkey";
    private static final String AMB_REF = "ambulanceref";
    private static final String AMBNAME = "ambulancename";
    private static final String AMBPHONE = "ambulancephone";
    private static final String AMBVEHICLE = "ambulancevehicleno";

    private static final String MYPREF = "mypreferences";
    private static final String REGISTERED = "registered";

    private static final String DONOR_REF = "blooddonors";
    private static final String DONOR_NAME ="donorname";
    private static final String DONOR_ADDRESS = "donoraddress";
    private static final String DONOR_LATITUDE ="donorlatitude";
    private static final String DONOR_LONGITUDE = "donorlongitude";
    private static final String DONOR_CONTACT = "donorcontact";
    private static final String DONOR_GROUP ="donorgroup";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String MOBILE = "mobile";
    private static final String FAX = "fax";
    private static final String EMAIL = "email";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";


    ArrayList<AmbRespObject> ambuList;
    Context context;
    SharedPreferences sharedPreferences;

    public AmbuAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        ambuList = objects;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MYPREF,Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return ambuList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_items, null);

        TextView textViewname = v.findViewById(R.id.name);
        TextView textViewmobile = v.findViewById(R.id.mobile);
        TextView textViewdistance = v.findViewById(R.id.distance);
        TextView reliablity = v.findViewById(R.id.reliablity);
        ImageView callicon = v.findViewById(R.id.callicon);
        ImageView smsicon = v.findViewById(R.id.sendsmsicon);

        final String name;
        final String mobile;
        final String distance;
        final String uid;
        String percent;
        name = ambuList.get(position).getName();
        mobile = ambuList.get(position).getContactno();
        distance = ambuList.get(position).getDistance();
        uid = ambuList.get(position).getUid();

        try {
            if (ambuList.get(position).getServiced()!=0 && ambuList.get(position).getReqcount()!=0) {
                double fraction = ambuList.get(position).getServiced() / ambuList.get(position).getReqcount();
                fraction *= 100;
                percent = String.valueOf((int)fraction) + "% Reliability";
            }else {
                percent="New Ambu";
            }
        }catch (Exception e){
            percent="New Ambu";
        }
//        name = "sasi";
//        mobile="42424";
//        distance="23";
//        bloodgroup = "sf";

        textViewname.setText(name);
        textViewmobile.setText(mobile);
        textViewdistance.setText(distance);
        reliablity.setText(percent);
        callicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("start","ok");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mobile));
                context.startActivity(intent);
                Log.d("finish","ok");
//                Toast.makeText(context,"Hi",Toast.LENGTH_SHORT).show();
            }
        });
        smsicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid=sharedPreferences.getString(PUSH_KEY,"");
                String phone=sharedPreferences.getString(DONOR_CONTACT,"");
                String smstext = "Dear "+name+"\nYou are requested to Arrive at "+
                        "contact no : "+sharedPreferences.getString(DONOR_NAME,"")+" , "+sharedPreferences.getString(DONOR_CONTACT,"")
                        //+"\nlocate us : "+"https://maps.google.com/?q="+String.valueOf(sharedPreferences.getFloat(LATITUDE,0))
                        //+","+String.valueOf(sharedPreferences.getFloat(LONGITUDE,0))
                        ;
                String locateustxt="location : "+"http://lifesource.com/locateaccident?lat="+String.valueOf(sharedPreferences.getFloat(DONOR_LATITUDE,0))
                        +"&lon="+String.valueOf(sharedPreferences.getFloat(DONOR_LONGITUDE,0)
                +"&phone="+phone);
                //Log.d("TAG",smstext);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobile, null, smstext, null, null);
                smsManager.sendTextMessage(mobile, null, locateustxt, null, null);
                Toast.makeText(getContext(),"Request Sent",Toast.LENGTH_LONG).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(DONOR_REF).child(uid).child("reqcount");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long count = 0;
                        if (dataSnapshot.getValue() != null) {
                            count = (long) dataSnapshot.getValue();
                        }
                        count++;
                        myRef.setValue(count);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }
}

