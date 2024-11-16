package com.yogaadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yogaadmin.Model.CourseModel;
import com.yogaadmin.databinding.ActivityEditCourseBinding;

public class EditCourseActivity extends AppCompatActivity {

    private ActivityEditCourseBinding binding;
    private DatabaseReference courseRef;
    private String courseId;

    // Store original data to compare with updated input
    private String originalTitle, originalDuration, originalRating, originalDescription, originalType, originalTime, originalDayOfWeek,originalThumbnailUrl,originalIntroVideo,postedBy;
    private long originalPrice;
    private Object playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the course ID passed from the previous activity
        courseId = getIntent().getStringExtra("courseId");

        // Initialize Firebase database reference
        courseRef = FirebaseDatabase.getInstance().getReference().child("course").child(courseId);


        setupSpinners();
        // Load existing course details
        loadCourseData();

        // Set the update button listener
        binding.btnUpdateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourse();
            }
        });
    }

    private void setupSpinners() {
        // Set up Spinner for Day of Week
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this, R.array.day_of_week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDayOfWeek.setAdapter(dayAdapter);

        // Set up Spinner for Time
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.time, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTime.setAdapter(timeAdapter);

        // Set up Spinner for Type
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.types_of_yoga, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(typeAdapter);
    }

    private void loadCourseData() {
        courseRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                CourseModel course = snapshot.getValue(CourseModel.class);
                if (course != null) {
                    // Populate fields with existing data
                    binding.edtTitle.setText(course.getTitle());
                    binding.edtPrice.setText(String.valueOf(course.getPrice()));
                    binding.edtDuration.setText(course.getDuration());
                    binding.edtRating.setText(course.getRating());
                    binding.edtDescription.setText(course.getDescription());

                    // Load thumbnail and intro video
                    Picasso.get().load(course.getThumbnail()).placeholder(R.drawable.upload_img).into(binding.uploadThumb);
                    Picasso.get().load(course.getIntroVideo()).placeholder(R.drawable.upload_v).into(binding.uploadIntroV);

                    // Set Spinner selections
                    setSpinnerSelection(binding.spinnerDayOfWeek, course.getDayOfWeek());
                    setSpinnerSelection(binding.spinnerTime, course.getTime());
                    setSpinnerSelection(binding.spinnerType, course.getType());

                    // Store original values for comparison
                    originalTitle = course.getTitle();
                    originalDuration = course.getDuration();
                    originalRating = course.getRating();
                    originalDescription = course.getDescription();
                    originalType = course.getType();
                    originalTime = course.getTime();
                    originalDayOfWeek = course.getDayOfWeek();
                    originalPrice = course.getPrice();
                    originalThumbnailUrl = course.getThumbnail();
                    originalIntroVideo = course.getIntroVideo();
                    postedBy = course.getPostedBy();
                    playlist = course.getPlaylist();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load course data", Toast.LENGTH_SHORT).show();
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (value != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }

    private void updateCourse() {
        // Get updated data from UI
        String title = binding.edtTitle.getText().toString();
        long price = Long.parseLong(binding.edtPrice.getText().toString());
        String duration = binding.edtDuration.getText().toString();
        String rating = binding.edtRating.getText().toString();
        String description = binding.edtDescription.getText().toString();
        String dayOfWeek = binding.spinnerDayOfWeek.getSelectedItem().toString();
        String time = binding.spinnerTime.getSelectedItem().toString();
        String type = binding.spinnerType.getSelectedItem().toString();

        // Check each field and update only if changed
        if (!title.equals(originalTitle)) {
            courseRef.child("title").setValue(title);
        }
        if (price != originalPrice) {
            courseRef.child("price").setValue(price);
        }
        if (!duration.equals(originalDuration)) {
            courseRef.child("duration").setValue(duration);
        }
        if (!rating.equals(originalRating)) {
            courseRef.child("rating").setValue(rating);
        }
        if (!description.equals(originalDescription)) {
            courseRef.child("description").setValue(description);
        }
        if (!type.equals(originalType)) {
            courseRef.child("type").setValue(type);
        }
        if (!time.equals(originalTime)) {
            courseRef.child("time").setValue(time);
        }
        if (!dayOfWeek.equals(originalDayOfWeek)) {
            courseRef.child("dayOfWeek").setValue(dayOfWeek);
        }

        // Use original values if fields are unchanged, otherwise use updated values
        String updatedTitle = !title.equals(originalTitle) ? title : originalTitle;
        long updatedPrice = price != originalPrice ? price : originalPrice;
        String updatedDuration = !duration.equals(originalDuration) ? duration : originalDuration;
        String updatedRating = !rating.equals(originalRating) ? rating : originalRating;
        String updatedDescription = !description.equals(originalDescription) ? description : originalDescription;
        String updatedType = !type.equals(originalType) ? type : originalType;
        String updatedTime = !time.equals(originalTime) ? time : originalTime;
        String updatedDayOfWeek = !dayOfWeek.equals(originalDayOfWeek) ? dayOfWeek : originalDayOfWeek;

        // Create a fully updated course model with required fields
        CourseModel updatedCourse = new CourseModel(
                updatedTitle,
                updatedDuration,
                updatedRating,
                updatedDescription,
                updatedPrice,
                originalThumbnailUrl, // Assuming image hasn't changed; update if you have a new URL
                originalIntroVideo, // IntroVideo URL if needed (keep original if unchanged)
                courseId,
                postedBy, // Assume postedBy remains unchanged
                "false", // Assuming course is enabled; change if needed
                updatedType,
                updatedTime,
                updatedDayOfWeek,
                playlist
        );

        // Update entire course in Firebase
        courseRef.setValue(updatedCourse).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditCourseActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }).addOnFailureListener(e -> {
            Toast.makeText(EditCourseActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
        });
    }
}
