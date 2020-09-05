package com.example.covid2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MedicineActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String phoneNo;
    private ListView medicineView;
    private Button search;
    private AutoCompleteTextView mNam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        getSupportActionBar().setTitle("CURE");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFF6666));
        phoneNo = getIntent().getStringExtra("PhoneNo");
        db = FirebaseFirestore.getInstance();
        medicineView = (ListView) findViewById(R.id.LISTVIEW_PHARMACY);
        search = (Button) findViewById(R.id.SEARCH_MEDICINE);
        mNam = (AutoCompleteTextView) findViewById(R.id.MEDICINE_NAME);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, MEDICINES);
        mNam.setAdapter(adapter);

        ArrayList<DonorInfo> arrayOfDonors = new ArrayList<DonorInfo>();

        final DonorAdapter dAdapter = new DonorAdapter(this, arrayOfDonors);
        medicineView.setAdapter(dAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateMedicine(mNam.getText().toString(), dAdapter);
            }
        });
    }
    private void populateMedicine(String name, final DonorAdapter dAdapter){
        db.collection("PHARMACY").whereArrayContains("drugs", name)
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
                    Toast.makeText(MedicineActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent options = new Intent(MedicineActivity.this, Options.class);
        options.putExtra("PhoneNo", phoneNo);
        finish();
        startActivity(options);
    }

    private static final String[] MEDICINES = new String[] {
            "Napa", "Metfo", "Paracetamol", "Flux", "Fexo", "Fenadin 120 mg","Acetaminophen", "Ativan","Naproxen","Xanax"
    };
}