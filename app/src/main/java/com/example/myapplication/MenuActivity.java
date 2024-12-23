package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private List<Course> filteredList;
    private TextView usernameTextView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inisialisasi
        usernameTextView = findViewById(R.id.usernameTextView);
        recyclerView = findViewById(R.id.recyclerView);
        ImageView profileImage = findViewById(R.id.imageProfile);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        filteredList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, filteredList); // Menggunakan filteredList
        recyclerView.setAdapter(courseAdapter);

        // Load courses dari server
        loadCourses();

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        usernameTextView.setText(username);

        // Tombol profil
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Setup pencarian
        setupSearchView();
    }

    private void loadCourses() {
        String url = Db_Contract.UrlGetCourses;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray courses = response.getJSONArray("data");
                            for (int i = 0; i < courses.length(); i++) {
                                JSONObject courseObj = courses.getJSONObject(i);

                                int courseId = courseObj.getInt("course_id");
                                String title = courseObj.getString("title");
                                String content = courseObj.getString("content");
                                String imageUrl = courseObj.getString("image_url");
                                String youtubeUrl = courseObj.getString("youtube_url");

                                // Tambahkan ke daftar kursus
                                courseList.add(new Course(courseId, title, content, imageUrl, youtubeUrl));
                            }
                            // Update filteredList dengan semua data awal
                            filteredList.addAll(courseList);
                            courseAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Error", e.getMessage());
                    }
                },
                error -> Log.e("Volley Error", error.getMessage()));

        requestQueue.add(request);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCourses(newText);
                return false;
            }
        });
    }

    private void filterCourses(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(courseList); // Tampilkan semua kursus jika query kosong
        } else {
            for (Course course : courseList) {
                if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(course);
                }
            }
        }
        courseAdapter.notifyDataSetChanged();
    }
}
