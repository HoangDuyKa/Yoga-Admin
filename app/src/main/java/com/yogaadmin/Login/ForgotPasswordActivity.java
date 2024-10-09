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
import com.google.firebase.auth.FirebaseAuth;
import com.yogaadmin.R;
import com.yogaadmin.databinding.ActivityForgotPasswordBinding;
import com.yogaadmin.databinding.ActivitySignInBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;
    Dialog loadingdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        loadingdialog = new Dialog(ForgotPasswordActivity.this);
        loadingdialog.setContentView(R.layout.loading_dialog);

        if(loadingdialog.getWindow()!= null){
            loadingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingdialog.setCancelable(false);
        }
        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailForgot.getText().toString();
                if(email.isEmpty()){
                    binding.emailForgot.setError("Enter email");
                }else{
                    forgotPassword(email);
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void forgotPassword(String email) {
        loadingdialog.show();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            loadingdialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this,"Check your email",Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else{
                            loadingdialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}