package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private String phoneNo;
    private String userId;
    private FirebaseFirestore db;
    private EditText name, email;
    private CheckBox isRecovered;
    private Button update, locUpdate;
    private int LAUNCH_SECOND_ACTIVITY = 5;
    private double lati, lonti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");
        db = FirebaseFirestore.getInstance();
        phoneNo = getIntent().getStringExtra("PhoneNo");
        getId();
        name = findViewById(R.id.SETTINGS_NAME);
        email = findViewById(R.id.SETTINGS_EMAIL);
        isRecovered = findViewById(R.id.SETTINGS_ISRECOVERED);
        update = (Button) findViewById(R.id.BUTTON_UPDATE_SETTINGS);
        locUpdate = (Button) findViewById(R.id.SETTINGS_LOC_UPDATE);

        getId();
        loadValues();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValues();
            }
        });
        locUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent options = new Intent(SettingsActivity.this, MapViewActivity.class);

                startActivityForResult(options, LAUNCH_SECOND_ACTIVITY);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                double lat = data.getDoubleExtra("LAT", 0);
                double lon = data.getDoubleExtra("LON", 0);
                this.lati = lat;
                this.lonti = lon;
                if(lat != 0 && lon != 0){
                    Toast.makeText(SettingsActivity.this, "Location set up successfully.", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private void getId(){
        db.collection("USERS").whereEqualTo("phoneNo", phoneNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        userId = doc.getId();
                    }

                } else {
                    Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void  loadValues(){
        db.collection("USERS").whereEqualTo("phoneNo", phoneNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Map<String, Object> data = doc.getData();
                        name.setText(data.get("fullName").toString());
                        email.setText(data.get("mail").toString());
                        isRecovered.setChecked((boolean) data.get("isRecovered"));
                    }
                    name.setEnabled(true);
                    email.setEnabled(true);
                    isRecovered.setEnabled(true);

                } else {
                    Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateValues(){
        update.setEnabled(false);
        update.setText("Updating...");
        if(name.getText().toString().isEmpty()){
            name.setError("Name required");
            update.setText("Update");
            update.setEnabled(true);
            return;
        }
        db.collection("USERS").document(userId).update("fullName", name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateMail();
            }
        });
    }
    private void updateMail(){
        db.collection("USERS").document(userId).update("mail", email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateRecoveryStatus();
            }
        });
    }
    private void updateRecoveryStatus(){
        db.collection("USERS").document(userId).update("isRecovered", isRecovered.isChecked()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(lati != 0)
                    updateLati();
                else {
                    Toast.makeText(SettingsActivity.this, "Settings updated successfully.", Toast.LENGTH_LONG).show();
                    back();
                }
            }
        });

    }
    private void updateLati(){
        db.collection("USERS").document(userId).update("latitude", lati).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateLongi();

            }
        });
    }
    private  void updateLongi(){
        db.collection("USERS").document(userId).update("longitude", lonti).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SettingsActivity.this, "Settings updated successfully.", Toast.LENGTH_LONG).show();
                back();

            }
        });
    }

    void back(){
        this.onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent options = new Intent(SettingsActivity.this, Options.class);
        options.putExtra("PhoneNo", phoneNo);
        finish();
        startActivity(options);
    }
}