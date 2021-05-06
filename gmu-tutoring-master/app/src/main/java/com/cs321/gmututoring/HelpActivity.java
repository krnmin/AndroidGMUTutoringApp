package com.cs321.gmututoring;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;

import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.*;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Random;

public class HelpActivity extends AppCompatActivity {

    private Button buttonFindTutor;
    private EditText editTextCourse;
    private ProgressDialog progressDialog;
    private ArrayList<String> courseList;
    private ArrayList<String> userIdList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference courseRef;
    private DatabaseReference currentUserRef;
    private DatabaseReference tutorUserRef;
    private int prevCurrentUserTokens;
    private int prevTutorUserTokens;
    private int tokenCount;
    private String tutor;
    private String tutorId;
    private SharedPreferences sp;

    // Put what functions you want to be called for the buttons in here
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == buttonFindTutor) {
                findTutor();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        Toolbar toolbar = findViewById(R.id.toolbarHelp);
        toolbar.setTitle("Find a Tutor");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toProfile();
            }
        });

        buttonFindTutor = findViewById(R.id.buttonFindTutor);
        editTextCourse = findViewById(R.id.editTextCourse);

        progressDialog = new ProgressDialog(this);

        buttonFindTutor.setOnClickListener(buttonListener);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).getRef();
    }

    private void toProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);

    }

    // Function for finding tutor
    private void findTutor() {
        String course = editTextCourse.getText().toString().trim();
        courseList = new ArrayList<>(2);
        userIdList = new ArrayList<>(2);

        // Checks if it is a 3 digit string
        if (!(course.matches("[0-9][0-9][0-9]"))) {
            editTextCourse.setError("Please enter a valid GMU CS Course #");
            editTextCourse.requestFocus();
            return;
        }

        courseRef = FirebaseDatabase.getInstance().getReference("courses").child(course).getRef();

        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    courseList.add(courseSnapshot.getValue(String.class));
                    userIdList.add(courseSnapshot.getKey());
                }
                displayTutor();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        progressDialog.setMessage("Finding tutor...");
        progressDialog.show();

    }

    private void displayTutor() {
        String tutorString;
        progressDialog.dismiss();

        if (courseList.size() < 1 || ((courseList.size() == 1) && (courseList.get(0).equals(currentUser.getEmail())))) {
            tutorString = "No available tutors for the selected course.";
        }
        else {
            Random rand = new Random();
            int rng = rand.nextInt(courseList.size());
            tutor = courseList.get(rng);
            tutorId = userIdList.get(rng);
            while (tutor.equals(currentUser.getEmail())) {
                rng = rand.nextInt(courseList.size());
                tutor = courseList.get(rng);
                tutorId = userIdList.get(rng);
            }
            tutorString = "Your tutor for the selected course can be reached at, " + tutor;
            tutorUserRef = FirebaseDatabase.getInstance().getReference("users").child(tutorId).getRef();
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    prevCurrentUserTokens = dataSnapshot.child("tokens").getValue(Integer.class).intValue();
                    prevCurrentUserTokens--;
                    Integer userTok = new Integer(prevCurrentUserTokens);
                    currentUserRef.child("tokens").setValue(userTok);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            tutorUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    prevTutorUserTokens = dataSnapshot.child("tokens").getValue(Integer.class).intValue();
                    prevTutorUserTokens++;
                    Integer tutorTok = new Integer(prevTutorUserTokens);
                    tutorUserRef.child("tokens").setValue(tutorTok);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        alertDialog(tutorString);
    }

    private void alertDialog(String s) {
        AlertDialog.Builder      dialog=new AlertDialog.Builder(this);
        dialog.setMessage(s);
        dialog.setTitle("Find a Tutor");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        toProfile();
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

}
