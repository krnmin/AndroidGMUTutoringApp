package com.cs321.gmututoring;

import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
/*
    Redone User class
    (If this turns out to work, remove old users class and refactor this to "User"
 */
public class User {

    // Declaring user vars
    private String userId;
    private String email;
    private Integer tokens;
    private DatabaseReference usersRef;
    private DatabaseReference coursesRef;
    private DatabaseReference currentUserRef;

    /*
    Class constructor to set UserID, Email, starting tokens, and initializing ArrayList of tutor classes
     */
    public User() {

    }

    public User(String userId, String email){
        coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        this.userId = userId;
        currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId).getRef();
        /*currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                tokens = dataSnapshot.child("tokens").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        this.email = email;
        this.tokens = 5;
    }

    /*
    Getters for vars
     */

    public String getUserId() {
        return this.userId;
    }
    public String getEmail() {
        // may crash!
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return this.email;
    }
    public int getTokens() {
        // may crash!
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tokens = dataSnapshot.child("tokens").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return this.tokens;
    }

    /*
    Modifiers/Setters for vars
     */

    // Adds course to users list of courses
    // Adds user to databases list of tutors
    public void addCourse(String course) {
        usersRef.child(this.userId).child("userCourses").child(course).setValue(course);
        coursesRef.child(course).child(this.userId).setValue(this.email);
    }

    // Removes course from users list of courses they can help in
    // Removes user from databases list of tutors
    public void removeCourse(String course) {
        coursesRef.child(course).child(this.userId).removeValue();
        usersRef.child(this.userId).child("userCourses").child(course).removeValue();
    }



    public String toString() {
        return this.userId + "\n" + this.email;
    }
}
