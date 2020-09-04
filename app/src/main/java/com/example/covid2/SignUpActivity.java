package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String TAG ="fafaf";
    private EditText fullName, phoneNo, email, password;
    private Button signUp, giveLocation;
    private Spinner blood_group, division;
    private CheckBox isRecovered;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private double lati, lonti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        db = FirebaseFirestore.getInstance();
        fullName = (EditText) findViewById(R.id.SIGN_UP_NAME);
        phoneNo = (EditText) findViewById(R.id.SIGN_UP_PHONE);
        email = (EditText) findViewById(R.id.SIGN_UP_MAIL);
        password = (EditText) findViewById(R.id.SIGN_UP_PASSWORD);
        signUp = (Button) findViewById(R.id.BUTTON_CREATE_ACCOUNT);
        blood_group = (Spinner) findViewById(R.id.SPINNER_BLOOD_GROUP);
        division = (Spinner) findViewById(R.id.SPINNER_DIVISION);
        isRecovered = (CheckBox) findViewById(R.id.CHECKBOX_IS_RECOVERED);
        giveLocation = (Button) findViewById(R.id.BUTTON_LOCATION);

        List<String> list = new ArrayList<String>();
        list.add("Select blood group");
        list.add("A+");
        list.add("A-");
        list.add("B+");
        list.add("B-");
        list.add("O+");
        list.add("O-");
        list.add("AB+");
        list.add("AB-");

        ArrayAdapter<String> bgadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        bgadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blood_group.setAdapter(bgadapter);


        blood_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SignUpActivity.this, "YOUR SELECTION IS : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        List<String> divisions = new ArrayList<String>();
        divisions.add("Select division");
        divisions.add("Dhaka");
        divisions.add("Rajshahi");
        divisions.add("Sylhet");
        divisions.add("Chittagong");
        divisions.add("Barisal");
        divisions.add("Khulna");
        divisions.add("Rangpur");

        ArrayAdapter<String> divisionadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, divisions);
        divisionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        division.setAdapter(divisionadapter);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setEnabled(false);
                signUp.setText("Creating account...");
                isValid(phoneNo.getText().toString());
            }
        });

        giveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent options = new Intent(SignUpActivity.this, MapViewActivity.class);

                startActivityForResult(options, LAUNCH_SECOND_ACTIVITY);

            }
        });
    }

    private  void isValid(String phone){

        if(fullName.getText().toString().isEmpty()){
            fullName.setError("Required");
            signUp.setEnabled(true);
            signUp.setText("Create account");
            return;
        }
        if(phone.length() != 11){
            phoneNo.setError("Invalid phone no");
            signUp.setEnabled(true);
            signUp.setText("Create account");
            return;
        }

        if(password.getText().toString().length() < 6){
            password.setError("At least 6 digit password needed.");
            signUp.setEnabled(true);
            signUp.setText("Create account");
            return;
        }
        if(blood_group.getSelectedItem() == "Select blood group"){
            Toast.makeText(SignUpActivity.this, "You need to select your blood group", Toast.LENGTH_LONG).show();
            signUp.setEnabled(true);
            signUp.setText("Create account");
            return;
        }
        if(division.getSelectedItem() == "Select division"){
            Toast.makeText(SignUpActivity.this, "You need to select your division", Toast.LENGTH_LONG).show();
            signUp.setEnabled(true);
            signUp.setText("Create account");
            return;
        }
        db.collection("USERS").whereEqualTo("phoneNo", phone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    signUp.setEnabled(true);
                    signUp.setText("Create account");
                    int size = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        size++;
                    }
                    if(size == 1) {
                        Toast.makeText(SignUpActivity.this, "Phone no already exists !", Toast.LENGTH_LONG).show();
                    }else{
                        signUpWithPhone(phoneNo.getText().toString(), password.getText().toString(), fullName.getText().toString(),
                                email.getText().toString());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void signUpWithPhone(String phone, String password, String fullName, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("phoneNo", phone);
        user.put("password", password);
        user.put("fullName", fullName);
        user.put("mail", email);
        user.put("blood_group", blood_group.getSelectedItem());
        user.put("isDonating", false);
        user.put("isRecovered", isRecovered.isChecked());
        user.put("division", division.getSelectedItem());
        user.put("latitude", lati);
        user.put("longitude", lonti);
        db.collection("USERS")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignUpActivity.this, "Account created.", Toast.LENGTH_LONG).show();
                        Intent signIn = new Intent(SignUpActivity.this, MainActivity.class);
                        finish();
                        startActivity(signIn);
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent signIn = new Intent(SignUpActivity.this, MainActivity.class);
        finish();
        startActivity(signIn);
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
                    Toast.makeText(SignUpActivity.this, "Location set up successfully.", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}