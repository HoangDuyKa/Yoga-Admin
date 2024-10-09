package com.yogaadmin;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yogaadmin.Adapter.PlayListAdapter;
import com.yogaadmin.Model.PlayListModel;
import com.yogaadmin.databinding.ActivityPlayListBinding;

import java.util.ArrayList;
import java.util.Collections;

public class PlayListActivity extends AppCompatActivity {

    ActivityPlayListBinding binding;
    private String postId, postedByName, introUrl,title ,rating,duration, description;
    private long price;
    private SimpleExoPlayer simpleExoPlayer;

    ArrayList<PlayListModel> list;

    PlayListAdapter adapter;

    private Dialog loadingdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        list = new ArrayList<>();

        loadingdialog = new Dialog(PlayListActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        if (loadingdialog.getWindow() != null) {
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }

        postId = getIntent().getStringExtra("postId");
        postedByName = getIntent().getStringExtra("name");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getLongExtra("price",0);
        rating = getIntent().getStringExtra("rate");
        duration = getIntent().getStringExtra("duration");
        description = getIntent().getStringExtra("desc");

        binding.title.setText(title);
        binding.createdBy.setText(postedByName);
        binding.rating.setText(rating);
        binding.duration.setText(duration);
        binding.price.setText(price+"$");
        binding.description.setText(description);

        try {
            simpleExoPlayer = new SimpleExoPlayer.Builder(PlayListActivity.this).build();
            binding.exoplayer2.setPlayer(simpleExoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(introUrl);
            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
            simpleExoPlayer.prepare();
            simpleExoPlayer.play();
            binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        binding.txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rvPlayList.setVisibility(View.GONE);
                binding.description.setVisibility(View.VISIBLE);
            }
        });

        binding.btnPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.description.setVisibility(View.GONE);
                binding.rvPlayList.setVisibility(View.VISIBLE);

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListAdapter(this, list, new PlayListAdapter.videoListener() {
            @Override
            public void onClick(int position, String key, String videoUrl, int size) {
                playVideo(videoUrl);
            }
        });
        binding.rvPlayList.setAdapter(adapter);
        loadPlayList();
    }

    private void playVideo(String videoUrl) {
        try {
            simpleExoPlayer = new SimpleExoPlayer.Builder(PlayListActivity.this).build();
            binding.exoplayer2.setPlayer(simpleExoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
            simpleExoPlayer.prepare();
            simpleExoPlayer.play();
            binding.exoplayer2.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPlayList() {

        FirebaseDatabase.getInstance().getReference().child("course").child(postId).child("playlist")
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
                        Toast.makeText(PlayListActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        loadingdialog.dismiss();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        simpleExoPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.pause();

    }
}