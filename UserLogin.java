package com.example.elimikalms.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.elimikalms.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity {
    EditText et_mail,et_password;
    Button btn_login;

    TextView tv_register,tv_forgot_password;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        et_mail = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        //Resetting  forgotten Password
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLogin.this, ResetPasswordActivity.class));
            }
        });
        //Perfoming Login to an account
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
        //Registering a new user
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLogin.this, UserRegistration.class));
            }
        });
        //Login Method
    }
    private void performLogin() {
        String email = et_mail.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (email.isEmpty()) {
            et_mail.setError("Email is required!");
            et_mail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_mail.setError("PLease provide a valid email");
            et_mail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            et_password.setError("Password is required!");
            et_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Min password Length is 6 Characters!");
            et_password.requestFocus();
            return;
        } else {
            progressDialog.setMessage("Login into your account ...");
            progressDialog.setTitle("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToMainActivity();
                        Toast.makeText(UserLogin.this, "Login Is Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UserLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
    private void sendUserToMainActivity() {
        Intent intent = new Intent(UserLogin.this, UserDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}