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
public class SignUpUI {
    @Rule
    public ActivityScenarioRule<SignupActivity> activityRule
            = new ActivityScenarioRule<SignupActivity>(SignupActivity.class);

    // Check that all necessary components are shown and functional to the user
    @Test
    public void SignUpPageTest() {
        rest();
        onView(withId(R.id.emailEdt)).check(matches(withHint("Email")));
        onView(withId(R.id.passwordEdt)).check(matches(withHint("Password (Min. 6 characters)")));
        onView(withId(R.id.changePwVisibility)).check(matches(isDisplayed()));
        onView(withId(R.id.changePwVisibility)).check(matches(isClickable()));
        onView(withId(R.id.signupBtn)).check(matches(withText("SIGN UP")));
        onView(withId(R.id.signupBtn)).check(matches(isClickable()));
        onView(withId(R.id.txtSignIn)).check(matches(withText("Sign in.")));
        onView(withId(R.id.txtSignIn)).check(matches(isClickable()));
        onView(withId(R.id.adminSignUpTxt)).check(matches(withText("Click here to sign up as an admin")));
        onView(withId(R.id.adminSignUpTxt)).check(matches(isClickable()));
    }

    // Testing if user is able to toggle password visibility.
    @Test
    public void changePwVisibilityTest() {
        rest();

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
        rest();

        // Empty password and email test
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Empty password test
        String sampleEmail = "abcdef@gmail.com";
        onView(withId(R.id.emailEdt)).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));

        // Empty email test
        String samplePw = "abcdef";
        onView(withId(R.id.emailEdt)).perform(clearText());
        onView(withId(R.id.passwordEdt)).perform(typeText(samplePw), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(click());
        onView(withId(R.id.signupBtn)).check(matches(isDisplayed()));
    }

    // Testing if user is able to navigate to the log in page from the sign up page
    @Test
    public void navigateToLogIn() {
        onView(withId(R.id.txtSignIn)).perform(click());
        onView(withId(R.id.loginBtn)).check(matches(isDisplayed()));
    }

    // Testing if user is able to navigate to the admin sign up page from this page
    @Test
    public void navigateToAdminSignUp() {
        onView(withId(R.id.adminSignUpTxt)).perform(click());
        onView(withId(R.id.adminSignUpKeyEdt)).check(matches(isDisplayed()));
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

    private void rest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
