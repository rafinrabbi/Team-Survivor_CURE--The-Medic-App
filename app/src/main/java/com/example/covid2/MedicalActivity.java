package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicalActivity extends AppCompatActivity {
    private Spinner mDiv, mArea;
    private ListView doctors;
    private Button fetch;
    private String phoneNo;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);
        phoneNo = getIntent().getStringExtra("PhoneNo");
        db = FirebaseFirestore.getInstance();
        mDiv = (Spinner) findViewById(R.id.SPINNER_MEDICAL_DIVSION);
        mArea = (Spinner) findViewById(R.id.SPINNER_MEDICAL_AREA);
        doctors = (ListView) findViewById(R.id.LISTVIEW_DOCTORS);
        fetch = (Button) findViewById(R.id.FETCH_DOCS);


        List<String> divisions = new ArrayList<String>();
        divisions.add("Dhaka");
        divisions.add("Rajshahi");
        divisions.add("Sylhet");
        divisions.add("Chittagong");
        divisions.add("Barisal");
        divisions.add("Khulna");
        divisions.add("Rangpur");
        final ArrayAdapter<String> divisionadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, divisions);
        divisionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiv.setAdapter(divisionadapter);

        List<String> area= new ArrayList<String>();
        area.add("Mirpur");
        area.add("Badda");
        area.add("Mugda");
        area.add("Rampura");
        area.add("Gulshan");
        area.add("Mokhakhali");
        area.add("Uttara");
        final ArrayAdapter<String>areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, area);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArea.setAdapter(areaAdapter);
        ArrayList<DonorInfo> arrayOfDonors = new ArrayList<DonorInfo>();

        final DonorAdapter dAdapter = new DonorAdapter(this, arrayOfDonors);
        doctors.setAdapter(dAdapter);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateView(mDiv.getSelectedItem().toString(), mArea.getSelectedItem().toString(), dAdapter);
            }
        });
    }

    private void populateView(String division, String area, final DonorAdapter dAdapter){
        Toast.makeText(MedicalActivity.this, division+ " "+area,Toast.LENGTH_LONG).show();
        db.collection("DOCTORS").whereEqualTo("division", division).whereEqualTo("area", area)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dAdapter.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        DonorInfo di = new DonorInfo(data.get("fullName").toString(), data.get("phoneNo").toString());
                        dAdapter.add(di);
                    }

                } else {
                    Toast.makeText(MedicalActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent options = new Intent(MedicalActivity.this, Options.class);
        options.putExtra("PhoneNo", phoneNo);
        finish();
        startActivity(options);
    }
}