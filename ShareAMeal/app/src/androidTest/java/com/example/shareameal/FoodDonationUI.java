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
public class FoodDonationUI {
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
        onView(withId(R.id.food)).perform(click());
    }

    // Check that all necessary components are shown and functional to the donor
    @Test
    public void foodListingPage() {
        rest();
        onView(withId(R.id.fabAddFood)).check(matches(isDisplayed()));
        onView(withId(R.id.fabAddFood)).check(matches(isClickable()));
        onView(withId(R.id.recycleViewFood)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    // Testing the following features: Add Food Listing -> Change Quantity -> Edit Food Listing
    // -> Delete Food Listing
    @Test
    public void foodListingFeature() {
        rest();

        // Add Food Listing
        onView(withId(R.id.fabAddFood)).perform(click());
        rest();
        onView(withId(R.id.foodNameEdt)).perform(typeText("Burger"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEdt)).perform(scrollTo())
                .perform(typeText("Burger with beef and cheese"), closeSoftKeyboard());
        onView(withId(R.id.addFoodItemBtn)).perform(scrollTo(), click());
        rest();

        // Checking if the text displayed on the food listing in the recyclerview is correct
        onView(new RecyclerViewMatcher(R.id.recycleViewFood).atPositionOnView(0, R.id.foodName))
                .check(matches(withText("Burger")));
        onView(new RecyclerViewMatcher(R.id.recycleViewFood).atPositionOnView(0, R.id.foodDescription))
                .check(matches(withText("Burger with beef and cheese")));
        onView(new RecyclerViewMatcher(R.id.recycleViewFood).atPositionOnView(0, R.id.foodQuantity))
                .check(matches(withText("Quantity: 0")));

        // Clicking on the recently added food listing, and checking if the components are visible
        onView(withId(R.id.recycleViewFood)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.foodImage)).check(matches(isDisplayed()));
        onView(withId(R.id.foodNameTxt)).check(matches(isDisplayed()));
        onView(withId(R.id.foodNameTxt)).check(matches(withText("Burger")));
        onView(withId(R.id.foodDescriptionTxt)).check(matches(isDisplayed()));
        onView(withId(R.id.foodDescriptionTxt)).check(matches(withText("Burger with beef and cheese")));
        onView(withId(R.id.foodQuantityHeader)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.foodQuantityEdt)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.foodQuantityEdt)).perform(scrollTo()).check(matches(withText("0")));
        onView(withId(R.id.decrementBtn)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.decrementBtn)).perform(scrollTo()).check(matches(isClickable()));
        onView(withId(R.id.incrementBtn)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.incrementBtn)).perform(scrollTo()).check(matches(isClickable()));
        onView(withId(R.id.updateFoodQuantityBtn)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.editFoodListingBtn)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.deleteFoodListingBtn)).perform(scrollTo()).check(matches(isDisplayed()));

        // Changing quantity
        onView(withId(R.id.incrementBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.foodQuantityEdt)).perform(scrollTo()).check(matches(withText("1")));
        onView(withId(R.id.updateFoodQuantityBtn)).perform(scrollTo()).perform(click());
        rest();
        onView(new RecyclerViewMatcher(R.id.recycleViewFood).atPositionOnView(0, R.id.foodQuantity))
                .check(matches(withText("Quantity: 1")));

        // Editing Food Listing
        onView(withId(R.id.recycleViewFood)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.editFoodListingBtn)).perform(scrollTo(), click());
        rest();
        onView(withId(R.id.foodNameEdt)).perform(clearText(), typeText("Good Burger"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEdt)).perform(clearText(), typeText("Good Burger with beef and cheese"), closeSoftKeyboard());
        onView(withId(R.id.editFoodItemBtn)).perform(click());
        pressBack();
        rest();
        onView(withId(R.id.foodNameTxt)).check(matches(withText("Good Burger")));
        onView(withId(R.id.foodDescriptionTxt)).check(matches(withText("Good Burger with beef and cheese")));

        // Deleting Food Listing
        onView(withId(R.id.deleteFoodListingBtn)).perform(scrollTo(), click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        rest();
        onView(withId(R.id.recycleViewFood)).check(new RecyclerViewItemCountAssertion(0));
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

