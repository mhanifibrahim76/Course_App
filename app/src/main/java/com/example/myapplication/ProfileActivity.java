package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, emailTextView, usernameTextViewProfile;
    private Button editProfileButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi view
        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextViewProfile = findViewById(R.id.usernameTextViewProfile); // Tambahkan ini
        emailTextView = findViewById(R.id.emailTextView); // Inisialisasi untuk email
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Ambil data user dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", ""); // Ambil email dari SharedPreferences

        if (!username.isEmpty() && !email.isEmpty()) {
            usernameTextView.setText(username);
            usernameTextViewProfile.setText("Username: " + username);  // Set data untuk TextView kedua
            emailTextView.setText("Email: " + email);
        } else {
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
        }

        // Listener untuk tombol Edit Profile
        editProfileButton.setOnClickListener(v -> {
            // Navigasi ke EditProfileActivity (buat activity baru jika belum ada)
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Listener untuk tombol Logout
        logoutButton.setOnClickListener(v -> {
            // Hapus data login dari SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Arahkan kembali ke MainActivity
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup ProfileActivity
        });
    }
}
