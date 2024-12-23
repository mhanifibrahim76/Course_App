package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private Button btnSave, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inisialisasi view
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnSave = findViewById(R.id.btnSave);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Set listener untuk tombol save
        btnSave.setOnClickListener(v -> saveChanges());

        // Set listener untuk tombol delete account
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void saveChanges() {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String currentUsername = preferences.getString("username", "");

        if (currentUsername.isEmpty()) {
            Toast.makeText(this, "No logged-in user found!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newUsername = editUsername.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(this, "No changes made!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Db_Contract.UrlUpdateAccount;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(SettingsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            // Update username di SharedPreferences jika diubah
                            if (!newUsername.isEmpty()) {
                                preferences.edit().putString("username", newUsername).apply();
                            }

                            // Pindahkan ke MenuActivity
                            Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SettingsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SettingsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SettingsActivity.this, "Failed to connect to the server.", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", currentUsername);
                if (!newUsername.isEmpty()) {
                    params.put("new_username", newUsername);
                }
                if (!newPassword.isEmpty()) {
                    params.put("new_password", newPassword);
                }
                return params;
            }

        };

        queue.add(stringRequest);
    }


    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (username.isEmpty()) {
            Toast.makeText(SettingsActivity.this, "No logged-in user found to delete!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Db_Contract.UrlDeleteAccount,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(SettingsActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();

                            // Hapus session dan arahkan ke halaman login
                            sharedPreferences.edit().clear().apply();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SettingsActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SettingsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SettingsActivity.this, "Failed to connect to the server.", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SettingsActivity.this);
        requestQueue.add(stringRequest);
    }
}
