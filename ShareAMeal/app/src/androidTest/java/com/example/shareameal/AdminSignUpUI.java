package com.example.shareameal;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminSignUpUI {
    @Rule
    public ActivityScenarioRule<AdminSignUp> activityRule
            = new ActivityScenarioRule<AdminSignUp>(AdminSignUp.class);

    // Check that all necessary components are shown and functional to the user
    @Test
    public void SignUpPageTest() {
        onView(withId(R.id.emailEdt)).check(matches(withHint("Email")));
        onView(withId(R.id.passwordEdt)).check(matches(withHint("Password (Min. 6 characters)")));
        onView(withId(R.id.usernameEdt)).check(matches(withHint("Username")));
        onView(withId(R.id.adminSignUpKeyEdt)).check(matches(withHint("Authorisation Token")));
        onView(withId(R.id.changePwVisibility)).check(matches(isDisplayed()));
        onView(withId(R.id.changePwVisibility)).check(matches(isClickable()));
        onView(withId(R.id.signupBtn)).check(matches(withText("SIGN UP")));
        onView(withId(R.id.signupBtn)).check(matches(isClickable()));
    }

    // Testing if user is able to toggle password visibility.
    @Test
    public void changePwVisibilityTest() {
        String samplePw = "abcdef";
        onView(withId(R.id.passwordEdt)).perform(typeText(samplePw), closeSoftKeyboard());

        // Password should be hidden at first
        onView(withId(R.id.passwordEdt)).check(matches(isPasswordHidden()));
        onView(withId(R.id.changePwVisibility)).perform(click());

        // Password should now be visible
        onView(withId(R.id.passwordEdt)).check(matches(not(isPasswordHidden())));
        onView(withId(R.id.changePwVisibility)).perform(click());

        // Password should be hidden again
        onView(withId(R.id.passwordEdt)).check(matches(isPasswordHidden()));
    }

    // Testing if user is able to sign up with any empty fields
    @Test
    public void emptyFieldsTest() {
        // All empty fields test
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only email provided
        String sampleEmail = "abcdef@gmail.com";
        onView(withId(R.id.emailEdt)).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only password provided
        String samplePw = "abcdef";
        onView(withId(R.id.emailEdt)).perform(clearText());
        onView(withId(R.id.passwordEdt)).perform(typeText(samplePw), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only email and password provided
        onView(withId(R.id.emailEdt)).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only username provided
        String sampleUserName = "sampleUserName";
        onView(withId(R.id.passwordEdt)).perform(clearText());
        onView(withId(R.id.emailEdt)).perform(clearText());
        onView(withId(R.id.usernameEdt)).perform(typeText(sampleUserName), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only email, password and username provided
        onView(withId(R.id.emailEdt)).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(typeText(samplePw), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Only admin authorisation key provided
        String sampleToken = "abcdef";
        onView(withId(R.id.passwordEdt)).perform(clearText());
        onView(withId(R.id.emailEdt)).perform(clearText());
        onView(withId(R.id.usernameEdt)).perform(clearText());
        onView(withId(R.id.adminSignUpKeyEdt)).perform(typeText(sampleToken), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));
    }

    private Matcher<View> isPasswordHidden() {
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Password is hidden");
            }

            @Override
            public boolean matchesSafely(EditText editText) {
                // Returns true if password is hidden
                return editText.getTransformationMethod() instanceof PasswordTransformationMethod;
            }
        };
    }
}
