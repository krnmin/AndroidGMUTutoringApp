package com.cs321.gmututoring;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.content.Intent;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Color;

import java.util.ArrayList;
import android.graphics.Color;

public class Profile extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userCoursesRef;
    private DatabaseReference currentUserRef;


    private ListView courseList;
    private Button buttonAdd;
    private Button buttonRemove;
    private EditText editTextCourse;
    private TextView balance;
    private int token;

    private User currentUser;

    private ArrayList<String> courses = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token = dataSnapshot.child("tokens").getValue(Integer.class).intValue();
                balance.setText("Balance: "+token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        courseList = findViewById(R.id.courseListView);


        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        // initializing widgets
        editTextCourse = findViewById(R.id.course_edit);
        buttonAdd = findViewById(R.id.add_btn);
        buttonRemove = findViewById(R.id.remove_btn);

        // getting the current user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        currentUser = new User(user.getUid(),user.getEmail());

        // setting database reference
        userCoursesRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUserId()).child("userCourses").getRef();
        currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).getRef();


        //help button should now link to help
        Button help = findViewById(R.id.help_btn);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int tokenX = dataSnapshot.child("tokens").getValue(Integer.class).intValue();
                        if (tokenX < 1) {
                            Toast.makeText(Profile.this, "Insufficient balance.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            startActivity(new Intent(Profile.this, HelpActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //Courses List View
//        courses = new ArrayList<>();
//        courses.clear();
        listCourses();
        adapter = new ArrayAdapter<>(Profile.this, R.layout.courses_view, R.id.courseItem,courses);
        courseList.setAdapter(adapter);


        balance = findViewById(R.id.token_bal);
        balance.setTextSize(20);
        balance.setTextColor(Color.WHITE);


        // add and remove onclick listeners
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                currentUser.addCourse(editTextCourse.getText().toString().trim());
                editTextCourse.setText("");
            }
        });
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //currentUser.addCourse(editTextCourse.getText().toString().trim());
                currentUser.removeCourse(editTextCourse.getText().toString().trim());
                editTextCourse.setText("");
            }
        });
        // pseudo signOut that just redirects the user to the login page
        // I don't think this will fly so be prepared to re-instantiate so that the user is actually logged out from Firebase



        Button signOut = findViewById(R.id.signOut_btn);
        ImageButton settings = findViewById(R.id.settingsButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Fire base implementation. Still need to find out if it writes to the correct user
                FirebaseAuth.getInstance().signOut();
                 // redirects user to login page again
                startActivity(new Intent(Profile.this,SignInActivity.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                toSettings();

            }
        });

    }

    private void toSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    public void listCourses() {
        userCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courses.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    courses.add("CS "+usersSnapshot.getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeAllCourses() {
        userCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    currentUser.removeCourse(usersSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

