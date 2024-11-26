package com.yogaadmin.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<UserModel> userList;
    private Context context;

    public UserAdapter(ArrayList<UserModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        // Load profile image
        if (user.getProfile() != null && !user.getProfile().isEmpty()) {
            Picasso.get().load(user.getProfile()).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.user_profile);
        }

        // Delete button click event
        holder.btnDeleteUser.setOnClickListener(v -> showDeleteDialog(user, position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void showDeleteDialog(UserModel user, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete " + user.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(user, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser(UserModel user, int position) {
        // Define the reference for deleting the user
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("user_details")
                .child(user.getUserId());

        // Attempt to delete the user in Firebase
        userRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // On successful deletion from Firebase, remove from local list and update UI
                    userList.remove(position);
                    notifyDataSetChanged();
                    notifyItemRemoved(position);
                    Toast.makeText(context, "User deleted successfully.", Toast.LENGTH_SHORT).show();

//                    // Navigate back to the previous screen
//                    ((Activity) context).finish();
                })
                .addOnFailureListener(e -> {
                    // If deletion fails, log the error and show a toast message
                    Toast.makeText(context, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DeleteUser", "Failed to delete user", e);
                });
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageView profileImage, btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvUserName);
            email = itemView.findViewById(R.id.tvUserEmail);
            profileImage = itemView.findViewById(R.id.ivUserProfile);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
