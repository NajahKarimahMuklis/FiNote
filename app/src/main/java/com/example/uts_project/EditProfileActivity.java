package com.example.uts_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_project.moduls.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editNama, editEmail;
    Button btnSave;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://uts-project-9950e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        editNama = findViewById(R.id.editNama);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserDetails user = snapshot.getValue(UserDetails.class);
                        editNama.setText(user.getUsername());
                        editEmail.setText(user.getUserEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            String newNama = editNama.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (TextUtils.isEmpty(newNama) || TextUtils.isEmpty(newEmail)) {
                Toast.makeText(EditProfileActivity.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser != null) {
                String userId = currentUser.getUid();

                Map<String, Object> updates = new HashMap<>();
                updates.put("username", newNama);
                updates.put("userEmail", newEmail);


                databaseReference.child(userId).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfileActivity.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            finish(); // Kembali ke layar sebelumnya
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfileActivity.this, "Gagal memperbarui profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}