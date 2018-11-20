package com.blazingapps.asus.lifesource;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InboxFragment extends Fragment {
    String url ;
    SharedPreferences sharedPreferences;
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

    ListView listView;
    ProgressBar progressBar;
    RelativeLayout root;
    ArrayList<ChatObject> chatObjectArrayList ;
    private Context mcontext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mcontext=getActivity();
        url = "https://us-central1-life-source-277b9.cloudfunctions.net/inbox?uid="+sharedPreferences.getString(PUSH_KEY,"");
        chatObjectArrayList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_inbox, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listview);
        root=view.findViewById(R.id.rootinbox);
        progressBar = view.findViewById(R.id.progress);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("chat").child(sharedPreferences.getString(PUSH_KEY,""));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        new InboxAysncTask().execute(url);
    }

    class InboxAysncTask extends AsyncTask<String,Void,String >{
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
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (s!=null){
                Log.d("Response is ",s);
                chatObjectArrayList = new ArrayList<>();
                try {
                    JSONObject resp = new JSONObject(s);
                    if (resp.getInt("status") == 200){
                        JSONArray chatlist = resp.getJSONArray("chatlist");
                        for (int i = 0; i<chatlist.length(); ++i ){
                            JSONObject questionObj = chatlist.getJSONObject(i);
                            String question = questionObj.getString("question");
                            ArrayList<AnswerObject> answers = new ArrayList<>();
                            JSONArray answerlist = questionObj.getJSONArray("answer");
                            for (int j=0; j<answerlist.length(); ++j){
                                JSONObject answerObj = answerlist.getJSONObject(j);
                                String ans = answerObj.getString("reply");
                                String docid = answerObj.getString("docId");
                                answers.add(new AnswerObject(ans,docid));
                            }

                            chatObjectArrayList.add(new ChatObject(question,answers));
                        }

                        ChatAdapter chatAdapter = new ChatAdapter(mcontext,R.layout.chatitem,chatObjectArrayList);
                        listView.setAdapter(chatAdapter);
                        listView.setSelection(listView.getCount()-1);

                    }else {
                        Toast.makeText(mcontext,"404",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error","error");
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
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void refresh(){
        new InboxAysncTask().execute(url);
    }
}
