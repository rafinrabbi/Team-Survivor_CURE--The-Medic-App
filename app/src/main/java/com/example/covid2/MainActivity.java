package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NativeActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.FirestoreGrpc;
import com.google.firestore.v1.TargetOrBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private String TAG ="fafaf";
    private FirebaseFirestore db;
    private EditText phone, pass;
    private Button signIn, signUp, guest, medical_facility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        phone = (EditText) findViewById(R.id.PHONE_NO_FIELD);
        pass = (EditText) findViewById(R.id.PASSWORD_FIELD);
        signIn = (Button) findViewById(R.id.BUTTON_SIGN_IN);
        signUp = (Button) findViewById(R.id.BUTTON_SIGN_UP);
        guest = (Button) findViewById(R.id.BUTTON_GUEST);
        medical_facility = (Button) findViewById(R.id.BUTTON_MEDICAL_FACILITY);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setEnabled(false);
                signIn.setText("Signing in...");
                signInWithPhone(phone.getText().toString(), pass.getText().toString());

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUp = new Intent(MainActivity.this, SignUpActivity.class);
                finish();
                startActivity(signUp);

            }
        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Under development.", Toast.LENGTH_LONG).show();
            }
        });

    }



    private void signInWithPhone(String phone, String password){

        if(phone.length() != 11){
            this.phone.setError("Invalid phone no.");
            signIn.setEnabled(true);
            signIn.setText("Sign in");
            return;
        }
        if(password.isEmpty()){
            pass.setError("Required.");
            signIn.setEnabled(true);
            signIn.setText("Sign in");
            return;
        }

        db.collection("USERS").whereEqualTo("phoneNo", phone).
                whereEqualTo("password", password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                signIn.setEnabled(true);
                signIn.setText("Sign in");
                if (task.isSuccessful()) {
                    int size = 0;
                    QueryDocumentSnapshot temp= null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        temp = document;
                        size++;
                    }
                    if(size == 1) {
                        Map<String, Object> data = temp.getData();
                        Toast.makeText(MainActivity.this, "Found "+data.get("fullName"), Toast.LENGTH_LONG).show();
                        Intent menu = new Intent(MainActivity.this, Options.class);

                        menu.putExtra("PhoneNo", data.get("phoneNo").toString());
                        finish();
                        startActivity(menu);
                    }else{
                        Toast.makeText(MainActivity.this, "Incorrect username or password.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}