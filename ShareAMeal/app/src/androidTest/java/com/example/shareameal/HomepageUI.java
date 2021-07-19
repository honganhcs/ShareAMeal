package com.example.shareameal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
@LargeTest
public class HomepageUI {
    public static class Recipient {
        String email = "espressoRecipient@gmail.com";
        String password = "espressoR";
        String username = "espressoRecipient";

        @Rule
        public ActivityScenarioRule<LoginActivity> activityRule
                = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

        // Log in as a recipient before the tests
        @Before
        public void recipientLogIn() {
            onView(withId(R.id.emailEdt)).perform(typeText(email), closeSoftKeyboard());
            onView(withId(R.id.passwordEdt)).perform(typeText(password), closeSoftKeyboard());
            onView(withId(R.id.loginBtn)).perform(click());
        }

        // Check that all necessary components are shown and functional to the user
        @Test
        public void HomepageTest() {
            rest();

            onView(withId(R.id.userNameIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Recipient")));
            onView(withId(R.id.reminderIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Checking for leaderboard (Both weekly and all-time)
            onView(withId(R.id.leaderboardTxt)).check(matches(withText("Leaderboard")));
            onView(withId(R.id.refreshTimestampTxt)).check(matches(isDisplayed()));

            onView(withId(R.id.leaderboardOneTxt)).check(matches(withText("Top Food Donors of the Week")));
            onView(withId(R.id.weeklyStatusTxt)).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFirstTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFirstQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneSecondTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneSecondQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneThirdTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneThirdQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFourthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFourthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFifthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFifthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));

            onView(withId(R.id.leaderboardTwoTxt)).perform(scrollTo()).check(matches(withText("Top Food Donors of All Time")));
            onView(withId(R.id.leaderboardTwoFirstTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFirstQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoSecondTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoSecondQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoThirdTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoThirdQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFourthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFourthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFifthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFifthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
        }

        // Check functionality of bottom navigation bar
        @Test
        public void BottomNavBar() {
            rest();

            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
            onView(withId(R.id.claimFood)).check(matches(isDisplayed()));
            onView(withId(R.id.claimFood)).check(matches(isClickable()));
            onView(withId(R.id.schedule)).check(matches(isDisplayed()));
            onView(withId(R.id.schedule)).check(matches(isClickable()));
            onView(withId(R.id.profile)).check(matches(isDisplayed()));
            onView(withId(R.id.profile)).check(matches(isClickable()));

            // Navigating to "Claim Food" page, and back
            onView(withId(R.id.claimFood)).perform(click());
            rest();
            onView(withId(R.id.rv)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Recipient")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Navigating to "View Orders" page, and back
            onView(withId(R.id.schedule)).perform(click());
            rest();
            onView(withId(R.id.rv)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Recipient")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Navigating to "User Profile" page, and back
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Recipient")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));
        }
    }

    public static class Donor {
        String email = "espressoDonor@gmail.com";
        String password = "espressoD";
        String username = "espressoDonor";

        @Rule
        public ActivityScenarioRule<LoginActivity> activityRule
                = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

        // Log in as a donor before the tests
        @Before
        public void donorLogIn() {
            onView(withId(R.id.emailEdt)).perform(typeText(email), closeSoftKeyboard());
            onView(withId(R.id.passwordEdt)).perform(typeText(password), closeSoftKeyboard());
            onView(withId(R.id.loginBtn)).perform(click());
        }

        // Check that all necessary components are shown and functional to the user
        @Test
        public void HomepageTest() {
            rest();

            onView(withId(R.id.userNameIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Donor")));
            onView(withId(R.id.reminderIcon)).check(matches(isDisplayed()));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Checking for leaderboard (Both weekly and all-time)
            onView(withId(R.id.leaderboardTxt)).check(matches(withText("Leaderboard")));
            onView(withId(R.id.refreshTimestampTxt)).check(matches(isDisplayed()));

            onView(withId(R.id.leaderboardOneTxt)).check(matches(withText("Top Food Donors of the Week")));
            onView(withId(R.id.weeklyStatusTxt)).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFirstTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFirstQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneSecondTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneSecondQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneThirdTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneThirdQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFourthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFourthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFifthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardOneFifthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));

            onView(withId(R.id.leaderboardTwoTxt)).perform(scrollTo()).check(matches(withText("Top Food Donors of All Time")));
            onView(withId(R.id.leaderboardTwoFirstTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFirstQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoSecondTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoSecondQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoThirdTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoThirdQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFourthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFourthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFifthTxt)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withId(R.id.leaderboardTwoFifthQtyTxt)).perform(scrollTo()).check(matches(isDisplayed()));
        }

        // Check functionality of bottom navigation bar
        @Test
        public void BottomNavBar() {
            rest();

            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
            onView(withId(R.id.food)).check(matches(isDisplayed()));
            onView(withId(R.id.food)).check(matches(isClickable()));
            onView(withId(R.id.schedule)).check(matches(isDisplayed()));
            onView(withId(R.id.schedule)).check(matches(isClickable()));
            onView(withId(R.id.profile)).check(matches(isDisplayed()));
            onView(withId(R.id.profile)).check(matches(isClickable()));

            // Navigating to "Donate Food" page, and back
            onView(withId(R.id.food)).perform(click());
            rest();
            onView(withId(R.id.recycleViewFood)).check(matches(isDisplayed()));
            onView(withId(R.id.fabAddFood)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Donor")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Navigating to "View Slots" page, and back
            onView(withId(R.id.schedule)).perform(click());
            rest();
            onView(withId(R.id.rv)).check(matches(isDisplayed()));
            onView(withId(R.id.btnAddTimeSlot)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Donor")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));

            // Navigating to "User Profile" page, and back
            onView(withId(R.id.profile)).perform(click());
            rest();
            onView(withId(R.id.userProfilePic)).check(matches(isDisplayed()));
            onView(withId(R.id.home)).perform(click());
            rest();
            onView(withId(R.id.userNameTxt)).check(matches(withText(username)));
            onView(withId(R.id.userRoleTxt)).check(matches(withText("Donor")));
            onView(withId(R.id.reminderTxt)).check(matches(isDisplayed()));
        }
    }

    private static void rest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
