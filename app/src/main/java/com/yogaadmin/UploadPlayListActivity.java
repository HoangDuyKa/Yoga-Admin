//package com.yogaadmin;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.yogaadmin.Model.CourseModel;
//import com.yogaadmin.Model.PlayListModel;
//import com.yogaadmin.databinding.ActivityUploadCourseBinding;
//import com.yogaadmin.databinding.ActivityUploadPlayListBinding;
//
//import java.util.Date;
//
//public class UploadPlayListActivity extends AppCompatActivity {
//
//    ActivityUploadPlayListBinding binding;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    FirebaseStorage storage;
//    Uri videoUri;
//    Dialog loadingdialog;
//    private String postId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        binding = ActivityUploadPlayListBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.uploadCourse), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        postId = getIntent().getStringExtra("postId");
//        if (postId == null) {
//            Toast.makeText(this, "postId is missing", Toast.LENGTH_SHORT).show();
//            finish(); // Đóng activity nếu không có postId
//        }
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        storage = FirebaseStorage.getInstance();
//
//        loadingdialog = new Dialog(UploadPlayListActivity.this);
//        loadingdialog.setContentView(R.layout.loading_dialog);
//
//        if(loadingdialog.getWindow()!=null){
//            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            loadingdialog.setCancelable(false);
//        }
//
//        binding.uploadVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent,1);
//            }
//        });
//
//        binding.uploadPlayList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String title  = binding.vTitle.getText().toString();
//                if(videoUri==null){
//                    Toast.makeText(UploadPlayListActivity.this, "please upload video", Toast.LENGTH_SHORT).show();
//                }else if(title.isEmpty()){
//                    binding.vTitle.setError("Enter title");
//                }else{
//                    uploadPlayList(title);
//                }
//            }
//        });
//    }
//
//    private void uploadPlayList(String title) {
//        loadingdialog.show();
//        StorageReference reference = storage.getReference().child("play_list").child(new Date().getTime()+"");
//        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        PlayListModel model = new PlayListModel();
//                        model.setTitle(title);
//                        model.setVideoUri(uri.toString());
//                        model.setEnable("false");
//
//                        database.getReference().child("course").child(postId).child("playlist")
//                                .push()
//                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        loadingdialog.dismiss();
//                                        Toast.makeText(UploadPlayListActivity.this, "Playlist uploaded", Toast.LENGTH_SHORT).show();
//                                        onBackPressed();
//                                    }
//                                });
//                    }
//                });
//            }
//        });
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==1) {
//            if (data != null) {
//                videoUri = data.getData();
//            }
//        }
//    }
//}

package com.yogaadmin;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yogaadmin.Adapter.PlayListAdapterAdmin;
import com.yogaadmin.Model.PlayListModel;
import com.yogaadmin.databinding.ActivityUploadPlayListBinding;

import java.util.ArrayList;
import java.util.Date;

public class UploadPlayListActivity extends AppCompatActivity {

    ActivityUploadPlayListBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri videoUri;
    Dialog loadingdialog;
    private String postId;
    private String postedBy;
    private String dayOfWeek;
    PlayListAdapterAdmin adapter;
    ArrayList<PlayListModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fix for potential null view in WindowInsets listener
        View uploadCourseView = findViewById(R.id.uploadCourse);
        if (uploadCourseView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(uploadCourseView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e("UploadPlayListActivity", "View with ID uploadCourse not found");
        }

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");
        postedBy = getIntent().getStringExtra("postedBy");
        dayOfWeek = getIntent().getStringExtra("dayOfWeek");


        if (postId == null) {
            Toast.makeText(this, "postId is missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Setup loading dialog
        loadingdialog = new Dialog(UploadPlayListActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        if (loadingdialog.getWindow() != null) {
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }

        loadingdialog.show();
        list = new ArrayList<>();

        loadPlayList();
        updateRegisteredCount(postId);


        // Video upload button
        binding.uploadVideo.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        });

        // Playlist upload button
        binding.uploadPlayList.setOnClickListener(view -> {
            String title = binding.vTitle.getText().toString();
            if (videoUri == null) {
                Toast.makeText(UploadPlayListActivity.this, "Please upload a video", Toast.LENGTH_SHORT).show();
            } else if (title.isEmpty()) {
                binding.vTitle.setError("Enter title");
            } else {
                uploadPlayList(title);
            }
        });

        // Playlist upload button
        binding.deletePlaylist.setOnClickListener(view -> {

//                deletePlayList(postId);
            deleteCourse(postId);
        });

        binding.editPlaylist.setOnClickListener(view -> {
            Intent intent = new Intent(UploadPlayListActivity.this, EditCourseActivity.class);
            intent.putExtra("courseId", postId); // Pass the course ID (postId) to EditCourseActivity
            startActivity(intent);
        });

        binding.manageSchedule.setOnClickListener(view -> {
            Intent intent = new Intent(UploadPlayListActivity.this, ManageScheduleActivity.class);
            intent.putExtra("courseId", postId); // Pass the course ID (postId) to EditCourseActivity
            intent.putExtra("postedBy", postedBy); // Pass the course ID (postId) to EditCourseActivity
            intent.putExtra("dayOfWeek", dayOfWeek); // Pass the course ID (postId) to EditCourseActivity
            startActivity(intent);
        });


    }

    private void deleteCourse(String postId) {
        // Tham chiếu tới course trong Firebase theo postId
        DatabaseReference courseRef = database.getReference("course").child(postId);

        // Xóa toàn bộ course với postId đã cho
        courseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Xóa thành công khóa học và các playlists
                    Toast.makeText(this, "Course and all related playlists deleted successfully", Toast.LENGTH_SHORT).show();
                    list.clear(); // Xóa dữ liệu course khỏi danh sách hiển thị
                    adapter.notifyDataSetChanged(); // Cập nhật giao diện
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    // Không xóa được khóa học
                    Toast.makeText(this, "Failed to delete course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DeleteCourse", "Error deleting course", e);
                });
    }

//    private void deletePlayList(String postId) {
//        // Reference the playlist under the specific course in Firebase
//        DatabaseReference playlistRef = database.getReference("course").child(postId).child("playlist");
//
//        // Remove all playlists under the specified course
//        playlistRef.removeValue()
//                .addOnSuccessListener(aVoid -> {
//                    // Successfully deleted the playlist
//                    Toast.makeText(this, "Playlist deleted successfully", Toast.LENGTH_SHORT).show();
//                    list.clear(); // Clear the local list to reflect UI update
//                    adapter.notifyDataSetChanged(); // Notify adapter of data change
//                })
//                .addOnFailureListener(e -> {
//                    // Failed to delete the playlist
//                    Toast.makeText(this, "Failed to delete playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("DeletePlaylist", "Error deleting playlist", e);
//                });
//    }

    private void loadPlayList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(UploadPlayListActivity.this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListAdapterAdmin(list,UploadPlayListActivity.this);
        binding.rvPlayList.setAdapter(adapter);

        database.getReference().child("course").child(postId).child("playlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                PlayListModel model = dataSnapshot.getValue(PlayListModel.class);
                                model.setKey(dataSnapshot.getKey());

                                list.add(model);
                            }
                            adapter.notifyDataSetChanged();
                            loadingdialog.dismiss();
                        }
                        else{
                            loadingdialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UploadPlayListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        loadingdialog.dismiss();
                    }
                });
    }

    private void updateRegisteredCount(String postId) {
        DatabaseReference enrolledUsersRef = database.getReference("course").child(postId).child("enrolled_users");

        enrolledUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Count the number of enrolled users
                long registeredCount = snapshot.getChildrenCount();

                // Update the TextView with the registered count
                TextView tvRegisteredCount = findViewById(R.id.tvRegisteredCount);
                tvRegisteredCount.setText(registeredCount + " people have registered for the course");
                System.out.println(tvRegisteredCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchRegisteredCount", "Error fetching registered users", error.toException());
                Toast.makeText(UploadPlayListActivity.this, "Failed to fetch registered count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Upload the playlist
    private void uploadPlayList(String title) {
        loadingdialog.show();
        StorageReference reference = storage.getReference().child("play_list").child(new Date().getTime() + "");
        reference.putFile(videoUri).addOnSuccessListener(taskSnapshot ->
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    PlayListModel model = new PlayListModel();
                    model.setTitle(title);
                    model.setVideoUri(uri.toString());
                    model.setEnable("false");

                    database.getReference().child("course").child(postId).child("playlist")
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(unused -> {
                                loadingdialog.dismiss();
                                Toast.makeText(UploadPlayListActivity.this, "Playlist uploaded", Toast.LENGTH_SHORT).show();
//                                onBackPressed();
                                binding.vTitle.setText(""); //3:01:09
                            })
                            .addOnFailureListener(e -> {
                                loadingdialog.dismiss();
                                Toast.makeText(UploadPlayListActivity.this, "Failed to upload playlist", Toast.LENGTH_SHORT).show();
//                                Log.e("UploadPlayListActivity", "Error uploading playlist", e);
                            });
                })
        ).addOnFailureListener(e -> {
            loadingdialog.dismiss();
            Toast.makeText(UploadPlayListActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
//            Log.e("UploadPlayListActivity", "Video upload failed", e);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
//            Log.d("UploadPlayListActivity", "Video selected: " + videoUri.toString());
        } else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
//            Log.e("UploadPlayListActivity", "Video selection failed");
        }
    }
}
