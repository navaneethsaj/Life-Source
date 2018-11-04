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

import java.util.ArrayList;

public class DonorAdapter extends ArrayAdapter<RespDonorObj> {

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
    private static final String HOSPITAL_REF = "hospital";
    private static final String PUSH_KEY = "pushkey";


    ArrayList<RespDonorObj> donorObjArrayList;
    Context context;
    SharedPreferences sharedPreferences;

    public DonorAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        donorObjArrayList = objects;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MYPREF,Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return donorObjArrayList.size();
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
        TextView textViewbloodgroup = v.findViewById(R.id.bloodgroup);
        ImageView callicon = v.findViewById(R.id.callicon);
        ImageView smsicon = v.findViewById(R.id.sendsmsicon);

        final String name,mobile,distance,bloodgroup;
        name = donorObjArrayList.get(position).getName();
        mobile = donorObjArrayList.get(position).getMobile();
        distance = donorObjArrayList.get(position).getDistance();
        bloodgroup = donorObjArrayList.get(position).getBloodgroup();

//        name = "sasi";
//        mobile="42424";
//        distance="23";
//        bloodgroup = "sf";

        textViewname.setText(name);
        textViewmobile.setText(mobile);
        textViewdistance.setText(distance);
        textViewbloodgroup.setText(bloodgroup);
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
                String smstext = "Dear "+name+"\nYou are requested to donate blood at "+
                        sharedPreferences.getString(NAME,"")+",\n"+
                        sharedPreferences.getString(ADDRESS,"")+"\n"+
                        "contact no : "+sharedPreferences.getString(PHONE,"")+" , "+sharedPreferences.getString(MOBILE,"");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobile, null, smstext, null, null);
            }
        });

        return v;
    }
}
