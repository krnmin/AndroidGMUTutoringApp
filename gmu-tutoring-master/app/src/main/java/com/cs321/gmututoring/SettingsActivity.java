package com.cs321.gmututoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference usersRef;
    private DatabaseReference userCoursesRef;
    private DatabaseReference coursesRef;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    private EditText editTextEmail;
    private Button buttonEmail;
    private Button buttonDelete;
    private User cUser;

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == buttonEmail) {
                String email = editTextEmail.getText().toString().trim();
                String domain = email.substring(email.lastIndexOf('@') + 1);

                if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches()) || TextUtils.isEmpty(email) || ((!domain.equals("gmu.edu")) && (!domain.equals("masonlive.gmu.edu")))) {
                    editTextEmail.setError("Please enter a valid GMU email address.");
                    editTextEmail.requestFocus();
                }
                else {
                    resetEmail(editTextEmail.getText().toString().trim());
                }
            }
            if (view == buttonDelete) {
                deleteDialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toProfile();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        userCoursesRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("userCourses").getRef();
        cUser = new User(currentUser.getUid(), currentUser.getEmail());

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setHint(currentUser.getEmail());
        buttonEmail = findViewById(R.id.resetEmailButton);
        buttonDelete = findViewById(R.id.deleteAccount);

        buttonEmail.setOnClickListener(buttonListener);
        buttonDelete.setOnClickListener(buttonListener);
    }

    private void toProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);

    }

    // Setting email (in-case of email change)
    // Replaces all instances of user in the tutor list with the new email
    private void resetEmail(final String newEmail) {
        usersRef.child(currentUser.getUid()).child("email").setValue(newEmail);
        currentUser.updateEmail(newEmail);

        userCoursesRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("userCourses").getRef();
        userCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot coursesSnapshot : dataSnapshot.getChildren()) {
                    coursesRef.child(coursesSnapshot.getValue(String.class)).child(currentUser.getUid()).setValue(newEmail);
                }
                alertDialog("Email successfully changed to " + newEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alertDialog(String s) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(s);
        dialog.setTitle("Settings");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void deleteDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to delete your account?");
        dialog.setTitle("Settings");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        deleteAccount();
                    }
                });
        dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void removeAllCourses() {
        userCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(usersSnapshot.getValue(String.class));
                    cUser.removeCourse(usersSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteAccount() {
        removeAllCourses();
        usersRef.child(currentUser.getUid()).removeValue();
        currentUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Account deleted.", Toast.LENGTH_SHORT).show();
                            toSignUp();
                        }
                    }
                });

    }


    private void toSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
