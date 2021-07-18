package com.example.shareameal;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminSignUpUI {
    @Rule
    public ActivityScenarioRule<SignupActivity> activityRule
            = new ActivityScenarioRule<SignupActivity>(SignupActivity.class);

    @Before
    public void changeToAdminSignUp() {
        onView(withId(R.id.adminSignUpTxt)).perform(click());
    }

    // Check that all necessary components are shown and functional to the user
    @Test
    public void SignUpPageTest() {
        rest();

        onView(withId(R.id.emailEdt)).check(matches(withHint("Email")));
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).check(matches(withHint("Password (Min. 6 characters)")));
        onView(withId(R.id.usernameEdt)).perform(scrollTo()).check(matches(withHint("Username")));
        onView(withId(R.id.adminSignUpKeyEdt)).perform(scrollTo()).check(matches(withHint("Authorisation Token")));
        onView(withId(R.id.changePwVisibility)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.changePwVisibility)).perform(scrollTo()).check(matches(isClickable()));
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(withText("SIGN UP")));
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isClickable()));
    }

    // Testing if user is able to toggle password visibility.
    @Test
    public void changePwVisibilityTest() {
        rest();

        String samplePw = "abcdef";
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).perform(typeText(samplePw), closeSoftKeyboard());

        // Password should be hidden at first
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).check(matches(isPasswordHidden()));
        onView(withId(R.id.changePwVisibility)).perform(scrollTo()).perform(click());

        // Password should now be visible
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).check(matches(not(isPasswordHidden())));
        onView(withId(R.id.changePwVisibility)).perform(scrollTo()).perform(click());

        // Password should be hidden again
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).check(matches(isPasswordHidden()));
    }

    // Testing if user is able to sign up with any empty fields
    @Test
    public void emptyFieldsTest() {
        rest();

        // All empty fields test
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only email provided
        String sampleEmail = "abcdef@gmail.com";
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only password provided
        String samplePw = "abcdef";
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).perform(typeText(samplePw), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only email and password provided
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only username provided
        String sampleUserName = "sampleUserName";
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.usernameEdt)).perform(scrollTo()).perform(typeText(sampleUserName), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only email, password and username provided
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(typeText(sampleEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).perform(typeText(samplePw), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Only admin authorisation key provided
        String sampleToken = "abcdef";
        onView(withId(R.id.passwordEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.emailEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.usernameEdt)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.adminSignUpKeyEdt)).perform(scrollTo()).perform(typeText(sampleToken), closeSoftKeyboard());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signupBtn)).perform(scrollTo()).check(matches(isDisplayed()));
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

    private static void rest() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
