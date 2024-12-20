package com.yogaadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yogaadmin.Model.CourseModel;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.yogaadmin.UploadPlayListActivity;
import com.yogaadmin.databinding.RvCourseDesignBinding;

import java.util.ArrayList;

public class CourseAdapterAdmin extends RecyclerView.Adapter<CourseAdapterAdmin.ViewHolder> {
    Context context;
    ArrayList<CourseModel> list;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public CourseAdapterAdmin(ArrayList<CourseModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseModel model = list.get(position);

        // Load course thumbnail
        Picasso.get().load(model.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.courseImage);

        // Set course details
        holder.binding.courseTitle.setText(model.getTitle());
        holder.binding.coursePrice.setText(model.getPrice() + "$");

        // Fetch and display admin details
        database.getReference().child("user_details").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        Picasso.get().load(userModel.getProfile())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.postedByProfile);
                        holder.binding.postedByName.setText(userModel.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("CourseAdapter", "Error fetching admin details", error.toException());
            }
        });

        // On clicking a course, open UploadPlayListActivity
        holder.itemView.setOnClickListener(view -> {
            String postId = model.getPostId();
            String postedBy = model.getPostedBy();
            String dayOfWeek = model.getDayOfWeek();
            Log.d("RecyclerView", "Post ID: " + postId);

            if (postId != null) {
                Intent intent = new Intent(context, UploadPlayListActivity.class);
                intent.putExtra("postId", postId);
                intent.putExtra("postedBy", postedBy);
                intent.putExtra("dayOfWeek", dayOfWeek);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Post ID is missing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvCourseDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvCourseDesignBinding.bind(itemView);
        }
    }
}
