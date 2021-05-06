package com.cs321.gmututoring;

import android.provider.Settings;

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

public class SettingsActivityTest {

    private String stringToBetyped;

    // sets which page to check
    @Rule
    public ActivityTestRule<SettingsActivity> SettingsActivityRule = new ActivityTestRule<>(SettingsActivity.class);

    // initialize a string to place into each field
    @Before
    public void initValidString() {
        // Specify a valid string.
        stringToBetyped = "Testing";
    }
    
    // tests 'enter email' text field of the Settings activity
    // will use reset email button
    @Test
    public void TestView(){
        onView(withId(R.id.editTextEmail))
                .perform(typeText(stringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.resetEmailButton)).perform(click());

        // check that the text was changed.
        onView(withId(R.id.editTextEmail))
                .check(matches(withText(stringToBetyped)));
    }

    // checks the delete account button
    @Test
    public void TestView2(){
        onView(withId(R.id.editTextEmail))
                .perform(typeText(stringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.deleteAccount)).perform(click());

        // check that the text was changed.
        onView(withId(R.id.editTextEmail))
                .check(matches(withText(stringToBetyped)));
    }


}