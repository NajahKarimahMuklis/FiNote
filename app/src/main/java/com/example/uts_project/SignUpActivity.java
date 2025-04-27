package com.example.uts_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uts_project.moduls.UserDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText namapengguna, email, katasandi, konfirkatasandi;
    Button btnSignUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        namapengguna = findViewById(R.id.namapengguna);
        email = findViewById(R.id.email);
        katasandi = findViewById(R.id.katasandi);
        konfirkatasandi = findViewById(R.id.konfirkatasandi);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(view -> {
            String username = namapengguna.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String password = katasandi.getText().toString().trim();
            String confirmPassword = konfirkatasandi.getText().toString().trim(); // ambil value

            if (TextUtils.isEmpty(username)) {
                namapengguna.setError("Masukkan nama pengguna!");
                namapengguna.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Masukkan email yang valid!");
                email.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                katasandi.setError("Masukkan kata sandi!");
                katasandi.requestFocus();
                return;
            }
            if (password.length() < 6) {
                katasandi.setError("Kata sandi minimal 6 karakter!");
                katasandi.requestFocus();
                return;
            }
            if (!password.equals(confirmPassword)) {
                konfirkatasandi.setError("Kata sandi tidak cocok!");
                konfirkatasandi.requestFocus();
                return;
            }

            registerUser(username, userEmail, password);
        });
    }

    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser fUser = mAuth.getCurrentUser();
                if (fUser != null) {
                    // ✅ Kirim email verifikasi
                    fUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                        if (verifyTask.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Email verifikasi telah dikirim. Silakan cek kotak masuk Anda!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Gagal mengirim email verifikasi. Coba lagi.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String uid = fUser.getUid();
                    UserDetails userDetails = new UserDetails(uid, username, email, password);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).setValue(userDetails).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("FirebaseDB", "Data pengguna berhasil disimpan.");
                        } else {
                            Log.e("FirebaseDB", "Gagal menyimpan data pengguna.", task1.getException());
                        }
                    });

                }
            } else {
                Toast.makeText(SignUpActivity.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}