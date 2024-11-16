package com.yogaadmin.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yogaadmin.Adapter.CourseAdapter;
import com.yogaadmin.Model.CourseModel;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.yogaadmin.databinding.FragmentHomeBinding;

import java.util.ArrayList;
//
//public class HomeFragment extends Fragment {
//
//    FragmentHomeBinding binding;
//
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    Dialog loadingdialog;
//
//    ArrayList<CourseModel> list;
//    ArrayList<CourseModel> filteredList;
//    CourseAdapter adapter;
//
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        loadingdialog = new Dialog(getContext());
//        loadingdialog.setContentView(R.layout.loading_dialog);
//
//        if(loadingdialog.getWindow()!=null){
//            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingdialog.setCancelable(false);
//        }
//
//        loadingdialog.show();
//        list = new ArrayList<>();
//
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
//        binding.rvCourse.setLayoutManager(layoutManager);
//
//        adapter = new CourseAdapter(list,getContext());
//        binding.rvCourse.setAdapter(adapter);
//
//        database.getReference().child("course")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            list.clear();
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                CourseModel model = dataSnapshot.getValue(CourseModel.class);
//                                model.setPostId(dataSnapshot.getKey());
//
//                                list.add(model);
//                            }
//                            adapter.notifyDataSetChanged();
//                            loadingdialog.dismiss();
//                        }
//                        else{
//                            loadingdialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                        loadingdialog.dismiss();
//                    }
//                });
//        return binding.getRoot();
//    }
//
//}
public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingdialog;
    ArrayList<CourseModel> list;
    ArrayList<CourseModel> filteredList;
    CourseAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingdialog = new Dialog(getContext());
        loadingdialog.setContentView(R.layout.loading_dialog);
        if (loadingdialog.getWindow() != null) {
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }
        loadingdialog.show();

        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new CourseAdapter(filteredList, getContext());

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvCourse.setLayoutManager(layoutManager);
        binding.rvCourse.setAdapter(adapter);

        // Load all courses from Firebase
        loadCourses();

        // Set up the SearchView listener
        setupSearchView();

        return binding.getRoot();
    }

    private void loadCourses() {
        database.getReference().child("course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CourseModel model = dataSnapshot.getValue(CourseModel.class);
                        if (model != null) {
                            model.setPostId(dataSnapshot.getKey());
                            list.add(model);
                        }
                    }
                    filteredList.clear();
                    filteredList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
                loadingdialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingdialog.dismiss();
            }
        });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCoursesByInstructorName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCoursesByInstructorName(newText);
                return false;
            }
        });
    }

    private void filterCoursesByInstructorName(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(list);
        } else {
            for (CourseModel course : list) {
                database.getReference().child("user_details").child(course.getPostedBy())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    if (userModel != null && userModel.getName().toLowerCase().contains(query.toLowerCase())) {
                                        filteredList.add(course);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        adapter.notifyDataSetChanged();
    }
}
