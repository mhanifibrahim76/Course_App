package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseDetailActivity extends AppCompatActivity {

    private TextView titleTextView, contentTextView;
    private WebView youtubeWebView;

    private int userId; // ID user dari SharedPreferences
    private int courseId; // ID course dari Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Inisialisasi View
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        youtubeWebView = findViewById(R.id.youtubeWebView);

        // Ambil data dari Intent
        courseId = getIntent().getIntExtra("course_id", -1);
        Log.d("IntentData", "Course ID from Intent: " + courseId);

        // Ambil user_id dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        Log.d("SharedPreferences", "User ID from SharedPreferences: " + userId);

        // Periksa validitas data
        if (courseId != -1 && userId != -1) {
            fetchCourseDetails(); // Mengambil detail course
        } else {
            Toast.makeText(this, "Missing user_id or course_id", Toast.LENGTH_SHORT).show();
            Log.e("Error", "Missing user_id or course_id");
        }
    }

    private void fetchCourseDetails() {
        String url = Db_Contract.UrlGetCourseDetails;

        // Parameter untuk API
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", userId);
            params.put("course_id", courseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request untuk mendapatkan detail course
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONObject data = response.getJSONObject("data");

                            // Update UI dengan data dari respons
                            titleTextView.setText(data.getString("title"));
                            contentTextView.setText(data.getString("content"));

                            // Load video YouTube
                            String youtubeUrl = data.getString("youtube_url");
                            youtubeWebView.getSettings().setJavaScriptEnabled(true);
                            youtubeWebView.setWebViewClient(new WebViewClient());
                            youtubeWebView.loadUrl("https://www.youtube.com/embed/" + youtubeUrl);
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.e("API Response", response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(this, "Failed to fetch course details", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
