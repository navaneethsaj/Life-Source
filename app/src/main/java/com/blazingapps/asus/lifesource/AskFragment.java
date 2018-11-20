package com.blazingapps.asus.lifesource;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AskFragment extends Fragment {
    EditText inputtext;
    Button sendbutton;
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
    String URL;
    SharedPreferences sharedPreferences;
    View view;
    private Context mcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mcontext=getActivity();
        URL = "https://us-central1-life-source-277b9.cloudfunctions.net/ask?id=" + sharedPreferences.getString(PUSH_KEY,"");
        return inflater.inflate(R.layout.fragment_ask, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendbutton = view.findViewById(R.id.sendbutton);
        inputtext = view.findViewById(R.id.inputtext);
        this.view=view;
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputtext.getText().toString().length()<2){
                    Snackbar.make(view,"Enter Valid Question",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String url = URL + "&question=" + inputtext.getText().toString();
                inputtext.setText("");
                inputtext.setHint("Sending");
                Log.d("url",url);
                new AskAsyncTask().execute(url);
            }
        });
    }

    class AskAsyncTask extends AsyncTask<String,Void,String >{

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
        protected void onPostExecute(String s) {
            sendbutton.setEnabled(true);
            super.onPostExecute(s);
            if (s!=null){
                Log.d("Response is ",s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mcontext,"Try Later",Toast.LENGTH_SHORT).show();
                }
                try {
                    if (jsonObject.getInt("status") == 200){
                        Snackbar.make(view,"Question Sent",Snackbar.LENGTH_SHORT).show();
                        inputtext.setHint("Ask");
                        /*FragmentManager fm = getFragmentManager();
                        InboxFragment fragm = (InboxFragment) fm.findFragmentById(R.id.inboxfragment);
                        fragm.refresh();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mcontext,"Try Later",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(mcontext,"Try Later",Toast.LENGTH_SHORT).show();
                Log.d("Response is ","null");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendbutton.setEnabled(false);
        }
    }

}
