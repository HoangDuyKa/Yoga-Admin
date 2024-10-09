package com.yogaadmin.Login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yogaadmin.Model.UserModel;
import com.yogaadmin.R;
import com.yogaadmin.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loadingdialog = new Dialog(SignUpActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);

        if(loadingdialog.getWindow()!= null){
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = binding.editName.getText().toString();
                String email = binding.editEmail.getText().toString();
                String password = binding.editPassword.getText().toString();
                if(name.isEmpty()){
                    binding.editName.setError("Enter your name");
                }else if(email.isEmpty()){
                    binding.editEmail.setError("Enter your email");
                }
                else if(password.isEmpty()){
                    binding.editPassword.setError("Enter your password");
                }
                else{
                    signup(name,email,password);
                }
            }
        });

        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signup(String name, String email, String password) {
        loadingdialog.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = task.getResult().getUser().getUid();
                            UserModel model = new UserModel(name,email,password,"no_profile_image");
                            database.getReference().child("user_details").child(userId)
                                    .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loadingdialog.dismiss();
                                                        Toast.makeText(SignUpActivity.this,"register successfully, please verify your email id",Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                    }
                                                });
                                            }
                                            else{
                                                loadingdialog.dismiss();
                                                Toast.makeText(SignUpActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        }
                    }
                });
    }
}