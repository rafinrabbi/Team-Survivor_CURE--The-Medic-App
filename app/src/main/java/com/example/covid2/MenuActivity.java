package com.example.covid2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private String TAG ="fafaf";
    private AppBarConfiguration mAppBarConfiguration;
    private String phoneNo;
    private TextView phoneText, userNameText;
    private FirebaseFirestore db;
    private Map<String, Object> userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        db = FirebaseFirestore.getInstance();
        phoneNo = getIntent().getStringExtra("PhoneNo");
        getUserData(phoneNo);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        phoneText = (TextView) headerView.findViewById(R.id.NAV_PHONE_NO);
        userNameText = (TextView) headerView.findViewById(R.id.NAV_USERNAME);
        phoneText.setText(phoneNo);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getUserData(String phoneNo){
        final QueryDocumentSnapshot[] qs = {null};
        db.collection("USERS").whereEqualTo("phoneNo", phoneNo).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int size = 0;
                    QueryDocumentSnapshot temp= null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        temp = document;
                        size++;
                    }
                    if(size == 1) {
                        ready(temp);

                    }else{
                        Toast.makeText(MenuActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(MenuActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    private void ready(QueryDocumentSnapshot qs){
        userData = qs.getData();
        userNameText.setText(userData.get("fullName").toString());
    }
}