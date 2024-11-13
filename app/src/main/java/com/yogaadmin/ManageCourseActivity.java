//package com.yogaadmin;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.yogaadmin.Adapter.CourseAdapterAdmin;
//import com.yogaadmin.Model.CourseModel;
//import com.yogaadmin.databinding.ActivityMainBinding;
//import com.yogaadmin.databinding.ActivityManageCourseBinding;
//
//import java.util.ArrayList;
//
//public class ManageCourseActivity extends AppCompatActivity {
//    ActivityManageCourseBinding binding;
//
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    Dialog loadingdialog;
//
//    ArrayList<CourseModel> list;
//    ArrayList<CourseModel> filteredList; // New filtered list
//    CourseAdapterAdmin adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        binding = ActivityManageCourseBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.uploadCourse), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        loadingdialog = new Dialog(ManageCourseActivity.this);
//        loadingdialog.setContentView(R.layout.loading_dialog);
//
//        if(loadingdialog.getWindow()!=null){
//            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingdialog.setCancelable(false);
//        }
//
//        loadingdialog.show();
//        list = new ArrayList<>();
//        filteredList = new ArrayList<>(); // Initialize the filtered list
//
//        GridLayoutManager layoutManager = new GridLayoutManager(ManageCourseActivity.this,2);
//        binding.rvCourse.setLayoutManager(layoutManager);
//
//        adapter = new CourseAdapterAdmin(list,ManageCourseActivity.this);
//        binding.rvCourse.setAdapter(adapter);
//
//        loadCourses();
//
//        // Set up the SearchView
//        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Perform filtering when the user submits the query
//                filterCourses(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Perform filtering as the user types
//                filterCourses(newText);
//                return true;
//            }
//        });
//
//
//
//        binding.uploadCourse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ManageCourseActivity.this, UploadCourseActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//    private void loadCourses() {
//        database.getReference().child("course").orderByChild("postedBy").equalTo(auth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            list.clear();
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                CourseModel model = dataSnapshot.getValue(CourseModel.class);
//                                model.setPostId(dataSnapshot.getKey());
//                                list.add(model);
//                            }
//                            filteredList.clear();
//                            filteredList.addAll(list); // Initially display all courses
//                            adapter.notifyDataSetChanged();
//                            loadingdialog.dismiss();
//                        } else {
//                            loadingdialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(ManageCourseActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                        loadingdialog.dismiss();
//                    }
//                });
//    }
//
//    private void filterCourses(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            filteredList.addAll(list); // Show all courses if query is empty
//        } else {
//            for (CourseModel course : list) {
//                if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(course); // Add courses matching the query
//                }
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
//}
package com.yogaadmin;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yogaadmin.Adapter.CourseAdapterAdmin;
import com.yogaadmin.Model.CourseModel;
import com.yogaadmin.R;
import com.yogaadmin.UploadCourseActivity;
import com.yogaadmin.databinding.ActivityManageCourseBinding;

import java.util.ArrayList;

public class ManageCourseActivity extends AppCompatActivity {
    ActivityManageCourseBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingdialog;

    ArrayList<CourseModel> list;
    ArrayList<CourseModel> filteredList; // Filtered list for search results
    CourseAdapterAdmin adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingdialog = new Dialog(ManageCourseActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        if (loadingdialog.getWindow() != null) {
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }
        loadingdialog.show();

        list = new ArrayList<>();
        filteredList = new ArrayList<>(); // Initialize filtered list

        GridLayoutManager layoutManager = new GridLayoutManager(ManageCourseActivity.this, 2);
        binding.rvCourse.setLayoutManager(layoutManager);

        adapter = new CourseAdapterAdmin(filteredList, ManageCourseActivity.this);
        binding.rvCourse.setAdapter(adapter);

        // Load courses from Firebase
        loadCourses();

        // Set up the SearchView
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform filtering when the user submits the query
                filterCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform filtering as the user types
                filterCourses(newText);
                return true;
            }
        });

        // Navigate to UploadCourseActivity
        binding.uploadCourse.setOnClickListener(view -> {
            Intent intent = new Intent(ManageCourseActivity.this, UploadCourseActivity.class);
            startActivity(intent);
        });
    }

    private void loadCourses() {
        database.getReference().child("course").orderByChild("postedBy").equalTo(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CourseModel model = dataSnapshot.getValue(CourseModel.class);
                                model.setPostId(dataSnapshot.getKey());
                                list.add(model);
                            }
                            filteredList.clear();
                            filteredList.addAll(list); // Initially display all courses
                            adapter.notifyDataSetChanged();
                            loadingdialog.dismiss();
                        } else {
                            loadingdialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ManageCourseActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingdialog.dismiss();
                    }
                });
    }

    private void filterCourses(String query) {
        filteredList.clear(); // Clear the filtered list before adding new results
        if (query.isEmpty()) {
            filteredList.addAll(list); // Show all items if the query is empty
        } else {
            for (CourseModel course : list) {
                if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(course); // Add items that match the query
                }
            }
        }
        adapter.notifyDataSetChanged(); // Notify the adapter of data changes
    }
}
