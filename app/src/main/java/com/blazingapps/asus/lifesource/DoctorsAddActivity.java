package com.blazingapps.asus.lifesource;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorsAddActivity extends AppCompatActivity {
    private static final String HOSPITAL_REF = "hospital";
    private static final int REQCODE = 1;
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
    private static final String REGISTERED = "registered";
    private static final String ADMIN = "admin";
    private static final String PUSH_KEY = "pushkey";
    Spinner spinner;
    EditText contactno,name;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_add);

        spinner = findViewById(R.id.speciality);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.specialization_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        sharedPreferences=getSharedPreferences(MYPREF,MODE_PRIVATE);

        name=findViewById(R.id.bname);
        contactno=findViewById(R.id.contactno);


    }

    public void adddoctor(View view) {

        DoctorObject doctorObject = new DoctorObject(name.getText().toString(),spinner.getSelectedItem().toString(),contactno.getText().toString());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(HOSPITAL_REF);

        myRef.child(sharedPreferences.getString(PUSH_KEY,"")).child("doctorlist").push().setValue(doctorObject);
    }
}
