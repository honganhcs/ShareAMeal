package com.example.shareameal;

import android.view.View;
import android.widget.EditText;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DonorScheduleUI {
    static String email = "espressoDonor@gmail.com";
    static String password = "espressoD";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule
            = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void logIn() {
        onView(withId(R.id.emailEdt)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());
        rest();
        onView(withId(R.id.schedule)).perform(click());
    }

    // Check that all necessary components are shown and functional to the donor
    @Test
    public void donorSchedulePage() {
        rest();
        onView(withId(R.id.btnAddTimeSlot)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddTimeSlot)).check(matches(isClickable()));
        onView(withId(R.id.rv)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    private static void rest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }
}
