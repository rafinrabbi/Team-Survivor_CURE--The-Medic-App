package com.example.covid2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Options extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button medical, emergency, plasma, settings, medical_facility,  medicine_corner;
    private String phoneNo;
    private TextView newCases, newDeaths, newRecovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_options);
        db = FirebaseFirestore.getInstance();
        phoneNo = getIntent().getStringExtra("PhoneNo");
        medical = (Button) findViewById(R.id.BUTTON_MEDICAL_FACILITY);
        plasma = (Button) findViewById(R.id.BUTTON_PLASMA);
        settings  = (Button) findViewById(R.id.BUTTON_ACCOUNT_SETTINGS);
        newCases = (TextView) findViewById(R.id.NEW_CASES);
        newDeaths = (TextView) findViewById(R.id.NEW_DEATHS);
        medicine_corner = (Button) findViewById(R.id.BUTTON_MEDICINE_CORNER);
        emergency = (Button) findViewById(R.id.BUTTON_EMERGENCY);

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(Options.this, EmergencyActivity.class);
                i.putExtra("PhoneNo", phoneNo);
                finish();
                startActivity(i);
            }
        });


        medicine_corner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicine = new Intent(Options.this, MedicineActivity.class);
                medicine.putExtra("PhoneNo", phoneNo);
                finish();
                startActivity(medicine);
            }
        });

        newRecovered = (TextView) findViewById(R.id.NEW_RECOVERED);
        medical_facility = (Button) findViewById(R.id.BUTTON_MEDICAL_FACILITY);

        medical_facility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicalActivity = new Intent(Options.this, MedicalActivity.class);
                medicalActivity.putExtra("PhoneNo", phoneNo);
                finish();
                startActivity(medicalActivity);
            }
        });

        plasma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plasma = new Intent(Options.this, PlasmaActivity.class);
                plasma.putExtra("PhoneNo", phoneNo);
                finish();
                startActivity(plasma);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plasma = new Intent(Options.this, SettingsActivity.class);
                plasma.putExtra("PhoneNo", phoneNo);
                finish();
                startActivity(plasma);
            }
        });
        loadCases();
    }

    private void loadCases(){
        db.collection("TodaysUpdate").document("NewCases").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                newCases.setText(data.get("new_infected").toString());
                newDeaths.setText(data.get("new_deaths").toString());
                newRecovered.setText(data.get("new_recovered").toString());
            }
        });
    }
}