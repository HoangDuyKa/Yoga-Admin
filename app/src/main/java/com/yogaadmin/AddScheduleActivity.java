package com.yogaadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yogaadmin.Model.ScheduleModel;
import com.yogaadmin.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText edtTeacher, edtComment, edtDate;
    private String selectedDate;
    private String courseId; // Passed from the previous activity
    private String postedBy; // To populate the teacher's name
    private String courseDayOfWeek; // Day of the week for validation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        edtTeacher = findViewById(R.id.edtTeacher);
        edtComment = findViewById(R.id.edtDuration);
        edtDate = findViewById(R.id.calendarView);

        // Get data passed from the previous activity
        postedBy = getIntent().getStringExtra("postedBy");
        courseId = getIntent().getStringExtra("courseId");
        courseDayOfWeek = getIntent().getStringExtra("dayOfWeek");

        // Fetch and display the teacher's name
        fetchTeacherName(postedBy);

        edtDate.setText("Select Date on "+courseDayOfWeek);

        // Show DatePickerDialog when clicking on the date field
        edtDate.setOnClickListener(v -> showDatePickerDialog());

        // Handle adding a schedule
        findViewById(R.id.btnUpdateCourse).setOnClickListener(view -> addSchedule());
    }

    private void fetchTeacherName(String postedBy) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_details").child(postedBy);
        userRef.get().addOnSuccessListener(snapshot -> {
            Log.d("AddScheduleActivity", "postedBy: " + postedBy);
            if (snapshot.exists()) {
                String teacherName = snapshot.child("name").getValue(String.class);
                edtTeacher.setText(teacherName); // Set teacher's name to the EditText
                edtTeacher.setFocusable(false); // Make the field read-only
            } else {
                Toast.makeText(this, "Failed to fetch teacher's name.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching teacher's name.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    // Get the day of the week for the selected date
                    String selectedDayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault())
                            .format(selectedCalendar.getTime());

                    // Validate the day of the week
                    if (selectedDayOfWeek.equalsIgnoreCase(courseDayOfWeek)) {
                        selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        edtDate.setText(selectedDate); // Display the selected date
                    } else {
                        edtDate.setText("Select Date on "+courseDayOfWeek);
                        selectedDate= null;
                        Toast.makeText(this, "You can only select " + courseDayOfWeek + ".", Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void addSchedule() {
        if (selectedDate == null ) {
            Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule");
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("course"); // Reference to courses
        String scheduleId = scheduleRef.push().getKey(); // Generate a unique ID for the schedule

        String comment = edtComment.getText().toString().isEmpty() ? "" : edtComment.getText().toString();

        ScheduleModel schedule = new ScheduleModel(
                scheduleId,
                courseId,
                edtTeacher.getText().toString(),
                selectedDate,
                comment
        );

        scheduleRef.child(scheduleId).setValue(schedule).addOnSuccessListener(aVoid -> {
            // After successfully adding the schedule, update enable to true for the course
            courseRef.child(courseId).child("enable").setValue("true").addOnSuccessListener(aVoid1 -> {
                Toast.makeText(this, "Schedule added and course enabled successfully.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Schedule added but failed to enable the course.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to add schedule.", Toast.LENGTH_SHORT).show();
        });
    }
}
