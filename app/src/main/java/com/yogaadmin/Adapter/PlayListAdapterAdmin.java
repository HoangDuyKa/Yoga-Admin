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
import com.yogaadmin.Model.PlayListModel;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.yogaadmin.UploadPlayListActivity;
import com.yogaadmin.databinding.RvCourseDesignBinding;
import com.yogaadmin.databinding.RvPlaylistDesignBinding;

import java.util.ArrayList;

public class PlayListAdapterAdmin extends RecyclerView.Adapter<PlayListAdapterAdmin.ViewHolder> {
    Context context;
    ArrayList<PlayListModel> list;
    public PlayListAdapterAdmin(ArrayList<PlayListModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_playlist_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayListModel model = list.get(position);

        // Set course details
        holder.binding.title.setText(model.getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvPlaylistDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvPlaylistDesignBinding.bind(itemView);
        }
    }
}
