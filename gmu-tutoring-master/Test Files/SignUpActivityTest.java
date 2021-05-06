package com.cs321.gmututoring;

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

public class SignUpActivityTest {

   private String stringToBetyped;

   // sets which page to check
   @Rule
   public ActivityTestRule<SignUpActivity> SignUpActivityRule = new ActivityTestRule<>(SignUpActivity.class);

   @Before
   public void initValidString() {
      // Specify a valid string.
      stringToBetyped = "Testing";
   }
   
   // tests 'enter email' text field
   @Test
   public void TestView(){
      onView(withId(R.id.editTextEmail))
             .perform(typeText(stringToBetyped), closeSoftKeyboard());
      onView(withId(R.id.buttonRegister)).perform(click());
   
      // Check that the text was changed.
      onView(withId(R.id.editTextEmail))
             .check(matches(withText(stringToBetyped)));
   }

   // tests 'enter password' text field
   @Test
   public void TestView2(){
      onView(withId(R.id.editTextPassword))
             .perform(typeText(stringToBetyped), closeSoftKeyboard());
      onView(withId(R.id.buttonRegister)).perform(click());
   
      // Check that the text was changed.
      onView(withId(R.id.editTextPassword))
             .check(matches(withText(stringToBetyped)));
   }

   // tests the 'confirm password' field
   @Test
   public void TestView3(){
      onView(withId(R.id.editTextCheckPassword))
             .perform(typeText(stringToBetyped), closeSoftKeyboard());
      onView(withId(R.id.buttonRegister)).perform(click());
   
      // Check that the text was changed.
      onView(withId(R.id.editTextCheckPassword))
             .check(matches(withText(stringToBetyped)));
   }
}