package com.example.tdsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnSignin;
    private TextView txtSignup;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        btnSignin = findViewById(R.id.btnSignin);
        txtSignup = findViewById(R.id.txtSignup);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        mDialog = new ProgressDialog(this);

        //To Sign In
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();


                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)){
                    email.setError("Required Field");
                    return;
                }

                mDialog.setMessage("Processsing>>>");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0,0);
                        }
                    }
                });
            }
        });

        // To Sign Up
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }
}
