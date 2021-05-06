package com.cs321.gmututoring;

import android.text.TextUtils;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class ProfileTest {

    private String stringToBetyped;

    // sets which page to check
    @Rule
    public ActivityTestRule<Profile> ProfileActivityRule = new ActivityTestRule<>(Profile.class);

    // initialize a string to place into each field
    @Before
    public void initValidString() {
        // Specify a valid string.
        stringToBetyped = "666";
    }
    
    // tests 'enter course number' field and add button
    @Test
    public void TestView(){
        onView(withId(R.id.course_edit))
                .perform(typeText(stringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.add_btn)).perform(click());
    }

    // checks the same field but will check the remove button
    @Test
    public void TestView2(){
        onView(withId(R.id.course_edit))
                .perform(typeText(stringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.remove_btn)).perform(click());

    }
}