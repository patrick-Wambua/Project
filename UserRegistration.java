package com.example.elimikalms.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elimikalms.R;
import com.example.elimikalms.admin.AdminRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


import java.util.HashMap;

public class UserRegistration extends AppCompatActivity {

    public static final String RIDER_USERS = "RidersUser";
    EditText et_email,et_password,et_confirmPassword,et_username;
    Button btn_register;
    TextView tv_loginBtn;


    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        et_confirmPassword=findViewById(R.id.et_confirmPassword);
        et_username=findViewById(R.id.et_username);
        btn_register=findViewById(R.id.btn_register);
        tv_loginBtn=findViewById(R.id.tv_loginButton);

        progressDialog =new ProgressDialog(this);

        mAuth =FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        tv_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserRegistration.this,UserLogin.class));

            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { PerformAuth();  }
        });
    }
    private  void PerformAuth() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmEmail = et_confirmPassword.getText().toString().trim();
        String username = et_username.getText().toString().trim();

        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Provide a valid Email");
            et_email.requestFocus();
            return;
        } else if (password.isEmpty()) {
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;
        } else if (password.length() < 6) {
            et_password.setError("Password is too short");
            et_password.requestFocus();
            return;
        } else {


            progressDialog.setMessage("Creating your account...");
            progressDialog.setTitle("Creating");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userID = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child(AdminRegister.RIDER_USERS).child(userID);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("username", username);
                        hashMap.put("imageUrl", "default");
                        hashMap.put("search", username.toLowerCase());

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    sendUserToMainActivity();
                                }
                            }
                        });


                        Toast.makeText(UserRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UserRegistration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
        private void sendUserToMainActivity() {
            Intent intent = new Intent(UserRegistration.this, UserDashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

    }
