package com.example.uts_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_project.moduls.UserDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    TextView txtNama, txtEmail;
    Button btnEditProfile;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://uts-project-9950e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        txtNama = findViewById(R.id.txtNama);
        txtEmail = findViewById(R.id.txtEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                // Sudah di profile, gak perlu startActivity lagi
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(ProfileActivity.this, SettingActivity.class));
                return true;
            }
            return false;
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserDetails user = snapshot.getValue(UserDetails.class);
                        txtNama.setText(user.getUsername());
                        txtEmail.setText(user.getUserEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    txtNama.setText("Error Loading Data");
                }
            });
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}