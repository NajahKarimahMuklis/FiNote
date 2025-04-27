package com.example.uts_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    CheckBox Checkboxes;
    Button btLogin;
    TextView forgetPass, signUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Checkboxes = findViewById(R.id.checkboxes);
        btLogin = findViewById(R.id.btnLogin);
        forgetPass = findViewById(R.id.forgetpass);
        signUp = findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        btLogin.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (emailStr.isEmpty()) {
                email.setError("Enter Email");
                email.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                password.setError("Enter Password");
                password.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailStr, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Log.d("LoginActivity", "Intent to HomeActivity started");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Gagal: Periksa kembali email atau kata sandi.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }
}