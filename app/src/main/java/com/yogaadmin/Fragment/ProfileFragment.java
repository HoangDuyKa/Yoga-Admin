package com.yogaadmin.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yogaadmin.Login.SignInActivity;
import com.yogaadmin.ManageCourseActivity;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.yogaadmin.databinding.FragmentProfileBinding;

import java.util.HashMap;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Dialog loadingdialog;
    Uri profileUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        loadingdialog = new Dialog(getContext());
        loadingdialog.setContentView(R.layout.loading_dialog);

        if(loadingdialog.getWindow()!= null){
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }
        
        loadProfileImage();

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        binding.manageCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ManageCourseActivity.class);
                startActivity(intent);
            }
        });

        binding.cardTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/")));
            }
        });

        binding.cardRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getContext().getPackageName())));
            }
        });

        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        return binding.getRoot();
    }

    private void loadProfileImage() {
        database.getReference().child("admin_details").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    binding.userName.setText(model.getName());
                    binding.userEmail.setText(model.getEmail());

                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.user_profile)
                            .into(binding.profileImage);
                }
//                if(snapshot.exists()){
//                    UserModel model = snapshot.getValue(UserModel.class);
//                    if (model != null) {
//                        if (model.getName() != null) {
//                            binding.userName.setText(model.getName());
//                        }
//                        if (model.getEmail() != null) {
//                            binding.userEmail.setText(model.getEmail());
//                        }
//                        if (model.getProfile() != null) {
//                            Picasso.get()
//                                    .load(model.getProfile())
//                                    .placeholder(R.drawable.user_profile)
//                                    .into(binding.profileImage);
//                        }
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            if(data!=null){
                updateProfile(data.getData());
            }
        }
    }

    private void updateProfile(Uri uri) {
        loadingdialog.show();
        final StorageReference reference = storage.getReference().child("profile_image")
                .child(auth.getCurrentUser().getUid());

        reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("profile",uri.toString());
                                        database.getReference().child("admin_details").child(auth.getUid())
                                                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        loadingdialog.dismiss();
                                                        Toast.makeText(getContext(),"Profile image updated",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }
}