package com.blazingapps.asus.lifesource;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    String action;
    LinearLayout asklayout,inboxlayout;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bottomNavigationView = findViewById(R.id.bottomnavigator);
        Bundle b = getIntent().getExtras();
        action = b.getString("action");
        asklayout = findViewById(R.id.asklayout);
        inboxlayout = findViewById(R.id.inboxlayout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_ask:
                        asklayout.setVisibility(View.VISIBLE);
                        inboxlayout.setVisibility(View.GONE);
                        break;
                    case R.id.action_inbox:
                        asklayout.setVisibility(View.INVISIBLE);
                        inboxlayout.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        if (action.equals("ask")){
            bottomNavigationView.setSelectedItemId(R.id.action_ask);
        }else if (action.equals("inbox")){
            bottomNavigationView.setSelectedItemId(R.id.action_inbox);
        }else {
            Toast.makeText(this,"Intent Error",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
