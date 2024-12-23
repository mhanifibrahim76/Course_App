package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Cek apakah pengguna sudah login
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Jika pengguna sudah login, ambil username dari SharedPreferences
            String username = sharedPreferences.getString("username", "");

            // Arahkan langsung ke MenuActivity
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish(); // Tutup MainActivity agar tidak dapat kembali ke sini
        } else {
            // Jika belum login, tampilkan tombol login dan register
            setContentView(R.layout.activity_main);

            Button regis = findViewById(R.id.regis1);
            Button login = findViewById(R.id.login1);

            regis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Arahkan ke RegistrationActivity
                    startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Arahkan ke LoginActivity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }
}
