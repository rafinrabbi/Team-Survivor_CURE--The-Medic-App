package com.example.covid2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class EmergencyActivity extends AppCompatActivity {
    private ImageButton bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        bt = (ImageButton) findViewById(R.id.GAJA);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmergencyActivity.this, "fafa", Toast.LENGTH_LONG).show();
            }
        });
    }
}