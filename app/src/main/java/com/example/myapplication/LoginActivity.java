package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.input_username_2);
        etPassword = findViewById(R.id.input_password_2);
        btnLogin = findViewById(R.id.login2);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Cek apakah pengguna sudah login
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String username = sharedPreferences.getString("username", "");
            navigateToMenuActivity(username);
        }

        // Tombol Login
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(username, password);
            }
        });
    }

    private void performLogin(String username, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Db_Contract.UrlLogin,
                response -> {
                    try {
                        // Log respons JSON yang diterima
                        Log.d("LoginActivity", "Response: " + response);

                        JSONObject jsonResponse = new JSONObject(response);

                        // Pastikan statusnya success
                        if (jsonResponse.getString("status").equalsIgnoreCase("success")) {
                            // Ambil user_id, username, dan email dari respons
                            int userId = jsonResponse.getInt("user_id");
                            String usernameResponse = jsonResponse.getString("username");
                            String emailResponse = jsonResponse.getString("email"); // Ambil email

                            // Simpan di SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putInt("user_id", userId);
                            editor.putString("username", usernameResponse);
                            editor.putString("email", emailResponse); // Simpan email
                            editor.apply();

                            // Navigasi ke MenuActivity
                            Toast.makeText(getApplicationContext(), "Welcome, " + usernameResponse + "!", Toast.LENGTH_SHORT).show();
                            navigateToMenuActivity(usernameResponse);
                        } else {
                            // Tangani jika login gagal
                            Toast.makeText(getApplicationContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // Tangani kesalahan JSON
                        Log.e("LoginActivity", "JSON Parsing error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "JSON Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Tangani kesalahan koneksi
                    Log.e("LoginActivity", "Error: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                // Kirimkan data username dan password ke server
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Tambahkan request ke queue Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }



    private void getUserId(String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Db_Contract.UrlGetUserId,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            int userId = jsonResponse.getInt("user_id");

                            // Simpan user_id dan username di SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("username", username);
                            editor.putInt("user_id", userId);
                            editor.apply();

                            // Navigasi ke MenuActivity
                            Toast.makeText(getApplicationContext(), "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                            navigateToMenuActivity(username);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }



    private void navigateToMenuActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.putExtra("username", username);
        // Jika ingin menambahkan email
        String email = sharedPreferences.getString("email", ""); // Ambil email dari SharedPreferences
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

}
