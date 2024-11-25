package com.yogaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yogaadmin.Model.ScheduleModel;
import com.yogaadmin.Adapter.ScheduleAdapter;
import com.yogaadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ManageScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleModel> scheduleList;
    private FloatingActionButton fabAddSchedule; // Updated to use FloatingActionButton

    private String courseId; // Course ID passed from the previous activity
    private String postedBy; // PostedBy ID
    private String dayOfWeek; // DayOfWeek of the course

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schedule);

        // Set up edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve extras from the Intent
        courseId = getIntent().getStringExtra("courseId");
        postedBy = getIntent().getStringExtra("postedBy");
        dayOfWeek = getIntent().getStringExtra("dayOfWeek");

        Log.d("ManageScheduleActivity", "Course ID: " + courseId);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSchedules);
        fabAddSchedule = findViewById(R.id.fabAddSchedule); // Changed to FloatingActionButton

        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(this, scheduleList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleAdapter);

        // Fetch schedules filtered by courseId
        fetchSchedules();

        fabAddSchedule.setOnClickListener(view -> {
            // Handle the click to add a schedule
            Intent intent = new Intent(ManageScheduleActivity.this, AddScheduleActivity.class);
            intent.putExtra("courseId", courseId); // Pass courseId
            intent.putExtra("postedBy", postedBy); // Pass postedBy
            intent.putExtra("dayOfWeek", dayOfWeek); // Pass dayOfWeek
            startActivity(intent);
        });
    }

    private void fetchSchedules() {
        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule");

        scheduleRef.orderByChild("courseId").equalTo(courseId).get()
                .addOnSuccessListener(snapshot -> {
                    scheduleList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                            ScheduleModel schedule = scheduleSnapshot.getValue(ScheduleModel.class);
                            if (schedule != null) {
                                scheduleList.add(schedule);
                            }
                        }
                    }

                    if (scheduleList.isEmpty()) {
                        // No schedules found, show "No Schedules" text
                        findViewById(R.id.noSchedulesText).setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        // Schedules found, show RecyclerView
                        findViewById(R.id.noSchedulesText).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    scheduleAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch schedules", Toast.LENGTH_SHORT).show();
                    Log.e("ManageScheduleActivity", "Error fetching schedules", e);
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the schedule list when the activity resumes
        fetchSchedules();
    }
}
