package com.blazingapps.asus.lifesource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    ArrayList<RespDonorObj> donorObjArrayList;
    Context context;

    public DonorAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        donorObjArrayList = objects;
        this.context = context;
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

        return v;
    }
}
