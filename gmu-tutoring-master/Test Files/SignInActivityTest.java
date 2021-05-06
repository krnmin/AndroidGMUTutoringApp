package com.cs321.gmututoring;

import android.view.View;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignInActivityTest {

   private String stringToBetyped;
  
   // sets which page to check
   @Rule
   public ActivityTestRule<SignInActivity> activityRule = new ActivityTestRule<>(SignInActivity.class);

   // initialize a string to place into each field
   @Before
   public void initValidString() {
      // Specify a valid string.
      stringToBetyped = "Testing";
   }

   // this test checks to see if the enter Email text field works correctly in the SignInActivity page
   @Test
   public void TestView() {
      // type text and then press the button.
      onView(withId(R.id.editTextEmail))
             .perform(typeText(stringToBetyped), closeSoftKeyboard());
      onView(withId(R.id.buttonSignIn)).perform(click());
   
   // check that the text was changed.
      onView(withId(R.id.editTextEmail))
         .check(matches(withText(stringToBetyped)));
   }

   // this test checks to see if the Password text field works correctly in the SignInActivity page
   // will also check to see if the Sign in Button works
   @Test
   public void TestView2(){
      onView(withId(R.id.editTextPassword))
             .perform(typeText(stringToBetyped), closeSoftKeyboard());
      onView(withId(R.id.buttonSignIn)).perform(click());
   
      // check that the text was changed.
      onView(withId(R.id.editTextPassword))
             .check(matches(withText(stringToBetyped)));
   }
}