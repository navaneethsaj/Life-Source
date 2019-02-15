package com.blazingapps.asus.lifesource;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchDoctor extends AppCompatActivity {
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

    ListView listView;
    Spinner spinner;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);

        spinner = findViewById(R.id.spinner);
        listView=findViewById(R.id.listview);
        button = findViewById(R.id.button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.specialization_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void search(View view) {
        new DoctorAsyncTask().execute("https://us-central1-life-source-277b9.cloudfunctions.net/getdoctor?lat=10&long=10&uid=11&spec=GYNO");
    }

    class DoctorAsyncTask extends AsyncTask<String,Void,String> {

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
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            if (s==null){
//                Snackbar.make(rootlayout,"Network Slow",Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"Network Slow",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject responseObject = new JSONObject(s);
                Log.d("status", String.valueOf(responseObject.getInt("status")));
                if (responseObject.getInt("status") == 200) {

                    String hspname = responseObject.getString("hospital");
                    JSONArray jsonArray = responseObject.getJSONArray("doctors");
                    ArrayList<DoctorObject> doctorObjects = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();++i){
                        if (jsonArray.getJSONObject(i).getString("speciality").equals(spinner.getSelectedItem().toString())) {
                            doctorObjects.add(new DoctorObject(
                                    jsonArray.getJSONObject(i).getString("name"),
                                    jsonArray.getJSONObject(i).getString("speciality"),
                                    jsonArray.getJSONObject(i).getString("phoneno"),
                                    jsonArray.getJSONObject(i).getString("time")
                            ));
                        }
                    }
                    DoctorAdapter doctorAdapter = new DoctorAdapter(SearchDoctor.this,R.layout.layout_doctors,doctorObjects,hspname);
                    listView.setAdapter(doctorAdapter);
                }else {
                    //error
                }
            } catch (JSONException e) {
                Log.d("exception","jsonexception occoured");
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

}
