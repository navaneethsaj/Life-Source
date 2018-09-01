package com.blazingapps.asus.lifesource;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private static final String HOSPITAL_ID = "hospitalid";
    private static final String HOSPITAL_REF = "hospital";
    private static final String PUSH_KEY = "pushkey";



    TextView nameProf,addressProf,mobileProf;
    LinearLayout searchresulttextview, profilelayout;
    Button buttonfinddonor;
    ListView listView;
    SharedPreferences sharedPreferences;
    Spinner spinner;
    SharedPreferences.Editor editor;
    AlertDialog searchingdialog;
    AlertDialog.Builder searchingbuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        nameProf = findViewById(R.id.profname);
        addressProf = findViewById(R.id.profaddress);
        mobileProf = findViewById(R.id.profmobile);
        buttonfinddonor = findViewById(R.id.finddonorbutton);
        listView = findViewById(R.id.listviewdonors);
        spinner = findViewById(R.id.spinner);
        searchresulttextview = findViewById(R.id.resultlayout);
        searchresulttextview.setVisibility(View.GONE);
        profilelayout = findViewById(R.id.profilelayout);

        profilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhoneNo();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        searchingbuilder = new AlertDialog.Builder(this);
        searchingbuilder.setTitle("Searching").setMessage("Please Wait...");
        searchingdialog = searchingbuilder.create();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        nameProf.setText(sharedPreferences.getString(NAME,""));
        addressProf.setText(sharedPreferences.getString(ADDRESS,""));
        mobileProf.setText(sharedPreferences.getString(MOBILE,""));

        buttonfinddonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = "https://us-central1-life-source-277b9.cloudfunctions.net/getdonor?";
                String group = spinner.getSelectedItem().toString();
                group = group.replace("+","%2B");
                String uid = sharedPreferences.getString(HOSPITAL_ID,"unregistered");
                String latitude = String.valueOf(sharedPreferences.getFloat(LATITUDE,0));
                String longitude = String.valueOf(sharedPreferences.getFloat(LONGITUDE,0));
                String url = URL + "uid=" + uid + "&lat=" + latitude + "&long=" + longitude + "&bgroup=" + group ;

                //url = "https://demo1275613.mockable.io/getdonors?uid=id10&lat=12&long=54&bgroup=o%2B";

                DonorAsyncTask asyncTask = new DonorAsyncTask();
                asyncTask.execute(url);
            }
        });
    }
    class DonorAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonfinddonor.setEnabled(false);
            if (!searchingdialog.isShowing()){
                searchingdialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            buttonfinddonor.setEnabled(true);
            if (searchingdialog.isShowing()){
                searchingdialog.dismiss();
            }
            try {
                JSONObject responseObject = new JSONObject(s);
                Log.d("status", String.valueOf(responseObject.getInt("status")));
                if (responseObject.getInt("status") == 200){
                    JSONArray donorArray = responseObject.getJSONArray("donors");
                    ArrayList<RespDonorObj> donorObjs = new ArrayList<>();

                    for (int i =0 ; i < donorArray.length() ; ++i){
                        String name = donorArray.getJSONObject(i).getString("name");
                        String mobile = donorArray.getJSONObject(i).getString("mobile");
                        String distance = donorArray.getJSONObject(i).getString("distance");
                        String bgroup = donorArray.getJSONObject(i).getString("bgroup");

                        donorObjs.add(new RespDonorObj(name,mobile,bgroup,distance));
                    }

                    DonorAdapter donorAdapter = new DonorAdapter(MainActivity.this,R.layout.list_view_items,donorObjs);
                    listView.setAdapter(donorAdapter);
                    searchresulttextview.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                Log.d("exception","jsonexception occoured");
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
    private void updatePhoneNo() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setPadding(20,20,20,20);
        builder.setTitle("Phone Number").setView(editText).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String phoneno = editText.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(HOSPITAL_REF);
                HashMap<String ,Object> hashMap = new HashMap<>();
                hashMap.put("mobile",phoneno);
                myRef.child(sharedPreferences.getString(PUSH_KEY,"")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                        editor.putString(MOBILE,phoneno);
                        editor.commit();
                        mobileProf.setText(phoneno);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel",null);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
