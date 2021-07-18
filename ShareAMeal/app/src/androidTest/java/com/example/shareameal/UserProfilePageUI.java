package com.example.shareameal;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
@LargeTest
public class UserProfilePageUI {
    public static class Recipient {
        String email = "espressoRecipient@gmail.com";
        String password = "espressoR";
        String username = "espressoRecipient";

        @Rule
        public ActivityScenarioRule<LoginActivity> activityRule
                = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

        @Before
        public void logIn() {
            onView(withId(R.id.emailEdt)).perform(typeText(email), closeSoftKeyboard());
            onView(withId(R.id.passwordEdt)).perform(typeText(password), closeSoftKeyboard());
            onView(withId(R.id.loginBtn)).perform(click());
        }

        // Check that all necessary components are shown and functional to the user
        @Test
        public void UserProfilePageTest() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.recordsIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.recordsTxt)).check(matches(withText("Records")));
            onView(withId(R.id.recordsTxt)).check(matches(isClickable()));
            onView(withId(R.id.editProfileIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.editProfileTxt)).check(matches(withText("Edit Profile")));
            onView(withId(R.id.editProfileTxt)).check(matches(isClickable()));
            onView(withId(R.id.changePasswordIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.changePasswordTxt)).check(matches(withText("Change Password")));
            onView(withId(R.id.changePasswordTxt)).check(matches(isClickable()));
            onView(withId(R.id.logoutIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.logoutTxt)).check(matches(withText("Log Out")));
            onView(withId(R.id.logoutTxt)).check(matches(isClickable()));
        }

        // Checking functionality of "Records" page
        @Test
        public void records() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.recordsTxt)).perform(click());
            rest();

            onView(withId(R.id.rv)).check(matches(isDisplayed()));

            // Pressing back should navigate user back to "User Profile" page
            pressBack();
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
        }

        // Checking functionality of the "Edit Profile" page
        @Test
        public void editProfile() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.editProfileTxt)).perform(click());
            rest();

            // Check all relevant components are shown and functional
            onView(withId(R.id.textView2)).check(matches(withText("Profile Information")));
            onView(withId(R.id.profilePicImg)).check(matches(isDisplayed()));
            onView(withId(R.id.chooseImageBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.chooseImageBtn)).check(matches(withText("choose image")));
            onView(withId(R.id.chooseImageBtn)).check(matches(isClickable()));
            onView(withId(R.id.uploadImageBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.uploadImageBtn)).check(matches(withText("confirm image choice")));
            onView(withId(R.id.uploadImageBtn)).check(matches(not(isEnabled())));
            onView(withId(R.id.usernameEdt)).check(matches(isDisplayed()));
            onView(withId(R.id.usernameEdt)).check(matches(isEnabled()));
            onView(withId(R.id.usernameEdt)).check(matches(withHint("Username")));
            onView(withId(R.id.usernameEdt)).check(matches(withText(username)));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(isEnabled()));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(withHint("Address")));
            onView(withId(R.id.updateProfileInfoBtn)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.updateProfileInfoBtn)).perform(scrollTo()).check(matches(not(isClickable())));

            // Pressing back should navigate user back to "User Profile" page
            pressBack();
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
        }

        // Check functionality of the "Change Password" Page
        @Test
        public void changePassword() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.changePasswordTxt)).perform(click());
            rest();

            // Check all relevant components are shown and functional
            onView(withId(R.id.textView2)).check(matches(withText("Password")));
            onView(withId(R.id.oldPwEdt)).check(matches(withHint("Enter Current Password")));
            onView(withId(R.id.changeOldPwVisibility)).check(matches(isDisplayed()));
            onView(withId(R.id.newPwEdt)).check(matches(withHint("Enter New Password")));
            onView(withId(R.id.changeNewPwVisibility)).check(matches(isDisplayed()));
            onView(withId(R.id.image1)).check(matches(isDisplayed()));
            onView(withId(R.id.textView3)).check(matches(withText("New password must be at least 6 characters long")));
            onView(withId(R.id.changePwBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.changePwBtn)).check(matches(not(isEnabled())));

            // Testing if user is able to toggle password visibility
            String samplePw = "abcdef";
            onView(withId(R.id.oldPwEdt)).perform(typeText(samplePw), closeSoftKeyboard());
            onView(withId(R.id.oldPwEdt)).check(matches(isPasswordHidden()));
            onView(withId(R.id.changeOldPwVisibility)).perform(click());
            onView(withId(R.id.oldPwEdt)).check(matches(not(isPasswordHidden())));
            onView(withId(R.id.changeOldPwVisibility)).perform(click());
            onView(withId(R.id.oldPwEdt)).check(matches(isPasswordHidden()));

            onView(withId(R.id.newPwEdt)).perform(typeText(samplePw), closeSoftKeyboard());
            onView(withId(R.id.newPwEdt)).check(matches(isPasswordHidden()));
            onView(withId(R.id.changeNewPwVisibility)).perform(click());
            onView(withId(R.id.newPwEdt)).check(matches(not(isPasswordHidden())));
            onView(withId(R.id.changeNewPwVisibility)).perform(click());
            onView(withId(R.id.newPwEdt)).check(matches(isPasswordHidden()));
        }

        // Checking if user is able to log out of the account
        @Test
        public void logOut() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.logoutTxt)).perform(click());
            rest();
            onView(withId(R.id.loginBtn)).check(matches(withText("SIGN IN")));
        }
    }

    public static class Donor {
        String email = "espressoDonor@gmail.com";
        String password = "espressoD";
        String username = "espressoDonor";

        @Rule
        public ActivityScenarioRule<LoginActivity> activityRule
                = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

        @Before
        public void logIn() {
            onView(withId(R.id.emailEdt)).perform(typeText(email), closeSoftKeyboard());
            onView(withId(R.id.passwordEdt)).perform(typeText(password), closeSoftKeyboard());
            onView(withId(R.id.loginBtn)).perform(click());
        }

        // Check that all necessary components are shown and functional to the user
        @Test
        public void UserProfilePageTest() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.recordsIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.recordsTxt)).check(matches(withText("Records")));
            onView(withId(R.id.recordsTxt)).check(matches(isClickable()));
            onView(withId(R.id.editProfileIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.editProfileTxt)).check(matches(withText("Edit Profile")));
            onView(withId(R.id.editProfileTxt)).check(matches(isClickable()));
            onView(withId(R.id.changePasswordIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.changePasswordTxt)).check(matches(withText("Change Password")));
            onView(withId(R.id.changePasswordTxt)).check(matches(isClickable()));
            onView(withId(R.id.logoutIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.logoutTxt)).check(matches(withText("Log Out")));
            onView(withId(R.id.logoutTxt)).check(matches(isClickable()));
        }

        // Checking functionality of "Records" page
        @Test
        public void records() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.recordsTxt)).perform(click());
            rest();

            onView(withId(R.id.rv)).check(matches(isDisplayed()));

            // Pressing back should navigate user back to "User Profile" page
            pressBack();
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
        }

        // Checking functionality of the "Edit Profile" page
        @Test
        public void editProfile() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.editProfileTxt)).perform(click());
            rest();

            // Check all relevant components are shown and functional
            onView(withId(R.id.textView2)).check(matches(withText("Profile Information")));
            onView(withId(R.id.profilePicImg)).check(matches(isDisplayed()));
            onView(withId(R.id.chooseImageBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.chooseImageBtn)).check(matches(withText("choose image")));
            onView(withId(R.id.chooseImageBtn)).check(matches(isClickable()));
            onView(withId(R.id.uploadImageBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.uploadImageBtn)).check(matches(withText("confirm image choice")));
            onView(withId(R.id.uploadImageBtn)).check(matches(not(isEnabled())));
            onView(withId(R.id.usernameEdt)).check(matches(isDisplayed()));
            onView(withId(R.id.usernameEdt)).check(matches(isEnabled()));
            onView(withId(R.id.usernameEdt)).check(matches(withHint("Username")));
            onView(withId(R.id.usernameEdt)).check(matches(withText(username)));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(isEnabled()));
            onView(withId(R.id.addressEdt)).perform(scrollTo()).check(matches(withHint("Address")));
            onView(withId(R.id.restaurantEdt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.restaurantEdt)).perform(scrollTo()).check(matches(isEnabled()));
            onView(withId(R.id.restaurantEdt)).perform(scrollTo()).check(matches(withHint("Food service")));
            onView(withId(R.id.updateProfileInfoBtn)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.updateProfileInfoBtn)).perform(scrollTo()).check(matches(not(isClickable())));

            // Pressing back should navigate user back to "User Profile" page
            pressBack();
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
        }

        // Check functionality of the "Change Password" Page
        @Test
        public void changePassword() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.changePasswordTxt)).perform(click());
            rest();

            // Check all relevant components are shown and functional
            onView(withId(R.id.textView2)).check(matches(withText("Password")));
            onView(withId(R.id.oldPwEdt)).check(matches(withHint("Enter Current Password")));
            onView(withId(R.id.changeOldPwVisibility)).check(matches(isDisplayed()));
            onView(withId(R.id.newPwEdt)).check(matches(withHint("Enter New Password")));
            onView(withId(R.id.changeNewPwVisibility)).check(matches(isDisplayed()));
            onView(withId(R.id.image1)).check(matches(isDisplayed()));
            onView(withId(R.id.textView3)).check(matches(withText("New password must be at least 6 characters long")));
            onView(withId(R.id.changePwBtn)).check(matches(isDisplayed()));
            onView(withId(R.id.changePwBtn)).check(matches(not(isEnabled())));

            // Testing if user is able to toggle password visibility
            String samplePw = "abcdef";
            onView(withId(R.id.oldPwEdt)).perform(typeText(samplePw), closeSoftKeyboard());
            onView(withId(R.id.oldPwEdt)).check(matches(isPasswordHidden()));
            onView(withId(R.id.changeOldPwVisibility)).perform(click());
            onView(withId(R.id.oldPwEdt)).check(matches(not(isPasswordHidden())));
            onView(withId(R.id.changeOldPwVisibility)).perform(click());
            onView(withId(R.id.oldPwEdt)).check(matches(isPasswordHidden()));

            onView(withId(R.id.newPwEdt)).perform(typeText(samplePw), closeSoftKeyboard());
            onView(withId(R.id.newPwEdt)).check(matches(isPasswordHidden()));
            onView(withId(R.id.changeNewPwVisibility)).perform(click());
            onView(withId(R.id.newPwEdt)).check(matches(not(isPasswordHidden())));
            onView(withId(R.id.changeNewPwVisibility)).perform(click());
            onView(withId(R.id.newPwEdt)).check(matches(isPasswordHidden()));
        }

        // Checking if user is able to log out of the account
        @Test
        public void logOut() {
            rest();
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.logoutTxt)).perform(click());
            rest();
            onView(withId(R.id.loginBtn)).check(matches(withText("SIGN IN")));
        }
    }

    private static Matcher<View> isPasswordHidden() {
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
