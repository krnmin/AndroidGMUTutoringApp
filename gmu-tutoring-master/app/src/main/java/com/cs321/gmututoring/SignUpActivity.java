package com.cs321.gmututoring;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;

import android.view.View;
import android.widget.*;
import android.text.*;
import android.content.Intent;

public class SignUpActivity extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCheckPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;
    private DatabaseReference databaseRef;

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == buttonRegister) {
                registerUser();
            }
            if (view == textViewSignIn) {
                toSignIn();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is signed in (non-null) and update UI accordingly.
        setContentView(R.layout.signup_activity);

        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCheckPassword = findViewById(R.id.editTextCheckPassword);

        textViewSignIn = findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(buttonListener);
        textViewSignIn.setOnClickListener(buttonListener);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        databaseRef = FirebaseDatabase.getInstance().getReference("users");

    }
//    public void buildActionCodeSettings() {
//        // [START auth_build_action_code_settings]
//        ActionCodeSettings actionCodeSettings =
//                ActionCodeSettings.newBuilder()
//                        // URL you want to redirect back to. The domain (www.example.com) for this
//                        // URL must be whitelisted in the Firebase Console.
////                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
//                        // This must be true
//                        .setHandleCodeInApp(true)
////                        .setIOSBundleId("com.example.ios")
//                        .setAndroidPackageName(
//                                "com.cs321.gmututoring",
//                                true, /* installIfNotAvailable */
//                                "12"    /* minimumVersion */)
//                        .build();
//
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.sendSignInLinkToEmail(user.getEmail(), actionCodeSettings)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(SignUpActivity.this, "Email Verification Sent!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cPassword = editTextCheckPassword.getText().toString().trim();

        String domain = email.substring(email.lastIndexOf('@') + 1);

        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches()) || TextUtils.isEmpty(email) || ((!domain.equals("gmu.edu")) && (!domain.equals("masonlive.gmu.edu")))) {
            editTextEmail.setError("Please enter a valid GMU email address.");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required");
            editTextPassword.requestFocus();
            return;
        }
        if (!(password.matches(cPassword))) {
            editTextCheckPassword.setError("Passwords do not match");
            editTextCheckPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !(task.getResult().getSignInMethods().isEmpty());
                        if (check) {
                            Toast.makeText(SignUpActivity.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // registered and logged in successfully, start of login activity
                            user = firebaseAuth.getCurrentUser();
                            addUserToDatabase();
                            Toast.makeText(SignUpActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                            toSignIn();

                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Function to add user to realtime-database
    private void addUserToDatabase() {
        String userId = user.getUid();
        String email = user.getEmail();

        User cuser = new User(userId, email);

        databaseRef.child(userId).setValue(cuser);
    }

    // Function to take user to sign in page
    private void toSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

}
