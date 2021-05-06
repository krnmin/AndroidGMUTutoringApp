package com.cs321.gmututoring;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.*;
import android.text.*;

public class SignInActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == buttonSignIn) {
                signIn();
            }
            if (view == textViewSignUp) {
                toSignUp();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);


        buttonSignIn = findViewById(R.id.buttonSignIn);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(buttonListener);
        textViewSignUp.setOnClickListener(buttonListener);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        //sign in button should now link to profile
    }

    private void signIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email Required");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required");
            editTextPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Go to main page activity
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Sign-in Successful.", Toast.LENGTH_SHORT).show();
                        toProfile();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Sign-in failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void toSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void toProfile(){
        Intent intent = new Intent(this,Profile.class);
        startActivity(intent);
    }

}
