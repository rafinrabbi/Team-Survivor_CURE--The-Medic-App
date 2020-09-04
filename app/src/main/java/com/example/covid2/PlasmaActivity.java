package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlasmaActivity extends AppCompatActivity {
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phoneNo;
    private FirebaseFirestore db;
    private Spinner blood_group, division;
    private ListView plasmaList;
    private Button fetch;
    private Switch donateSwitch;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasma);
        phoneNo = getIntent().getStringExtra("PhoneNo");
        db = FirebaseFirestore.getInstance();
        blood_group = (Spinner) findViewById(R.id.SPINNER_PLASMA_BG);
        division = (Spinner) findViewById(R.id.SPINNER_PLASMA_DIVISION);
        plasmaList = (ListView) findViewById(R.id.LISTVIEW_PLASMA_DONORS);
        fetch = (Button) findViewById(R.id.BUTTON_QUERY_DONOR);
        donateSwitch = (Switch) findViewById(R.id.SWITCH_IS_DONATING);
        donateSwitch.setEnabled(false);

        List<String> bg = new ArrayList<String>();
        bg.add("A+");
        bg.add("A-");
        bg.add("B+");
        bg.add("B-");
        bg.add("O+");
        bg.add("O-");
        bg.add("AB+");
        bg.add("AB-");
        ArrayAdapter<String> bgadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bg);
        bgadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blood_group.setAdapter(bgadapter);

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
        division.setAdapter(divisionadapter);

        ArrayList<DonorInfo> arrayOfDonors = new ArrayList<DonorInfo>();
        //arrayOfDonors.add(new DonorInfo("tta", "fafa"));
        final DonorAdapter dAdapter = new DonorAdapter(this, arrayOfDonors);
        plasmaList.setAdapter(dAdapter);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDonor(blood_group.getSelectedItem().toString(), division.getSelectedItem().toString(), dAdapter);
            }
        });
        loadSwitch(donateSwitch);
        donateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                donateSwitch.setEnabled(false);
                changeSwitch(donateSwitch);
            }
        });
        if( !checkPermission(Manifest.permission.CALL_PHONE)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        }

    }
    private void loadDonor(String blood_group, String division, final DonorAdapter dAdapter){
        db.collection("USERS").whereEqualTo("blood_group", blood_group).whereEqualTo("isRecovered", true).whereEqualTo("division", division)
                .whereEqualTo("isDonating", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    Toast.makeText(PlasmaActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadSwitch(final Switch sw){
        db.collection("USERS").whereEqualTo("phoneNo", phoneNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> qs = null;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        qs = doc.getData();
                    }
                    boolean isOn = (boolean)qs.get("isDonating");
                    if(isOn){
                        sw.setChecked(true);
                    }
                    sw.setEnabled(true);
                } else {
                    Toast.makeText(PlasmaActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void changeSwitch(final Switch sw){
        if(sw.isChecked()){
            showDialogue();
        }
        db.collection("USERS").whereEqualTo("phoneNo", phoneNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        db.collection("USERS").document(doc.getId()).update("isDonating", sw.isChecked());
                        loadSwitch(sw);
                    }

                } else {
                    Toast.makeText(PlasmaActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showDialogue(){
        AlertDialog alertDialog = new AlertDialog.Builder(PlasmaActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Turning the donate feature on puts you in plasma donor list. You are doing a great job, contributing to save a life.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                return;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent options = new Intent(PlasmaActivity.this, Options.class);
        options.putExtra("PhoneNo", phoneNo);
        finish();
        startActivity(options);
    }
}