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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.yogaadmin.MainActivity;
import com.yogaadmin.R;
import com.yogaadmin.databinding.ActivitySignInBinding;
import com.yogaadmin.databinding.ActivitySignUpBinding;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loadingdialog = new Dialog(SignInActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);

        if(loadingdialog.getWindow()!= null){
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.editEmail.getText().toString();
                String password = binding.editPassword.getText().toString();
                if(email.isEmpty()){
                    binding.editEmail.setError("Enter your email");
                }
                else if(password.isEmpty()){
                    binding.editPassword.setError("Enter your password");
                }
                else{
                    signin(email,password);
                }
            }
        });

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

//    private void signin(String email, String password) {
//        loadingdialog.show();
//        auth.signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            if (auth.getCurrentUser().isEmailVerified()) {
//
//                                loadingdialog.dismiss();
//                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else{
//                                loadingdialog.dismiss();
//                                Toast.makeText(SignInActivity.this,"Your email id is not verified so verified first to login",Toast.LENGTH_SHORT).show();
//                            }
//                        }else
//                        {
//                            loadingdialog.dismiss();
//                            Toast.makeText(SignInActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    private void signin(String email, String password) {
        loadingdialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                // Lấy userId hiện tại từ Firebase Authentication
                                String userId = auth.getCurrentUser().getUid();

                                // Kiểm tra với bảng admin_details trong Firebase Realtime Database
                                database.getReference().child("admin_details").child(userId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> adminTask) {
                                                if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                                                    // Nếu người dùng tồn tại trong bảng admin_details
                                                    loadingdialog.dismiss();
                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    // Người dùng không tồn tại trong admin_details
                                                    loadingdialog.dismiss();
                                                    Toast.makeText(SignInActivity.this, "You are not authorized to access this app.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                loadingdialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Your email is not verified. Please verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingdialog.dismiss();
                            Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}