package com.example.shareameal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.SystemClock;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

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
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FoodClaimingUI {
    static String donorEmail = "espressoDonor@gmail.com";
    static String donorPassword = "espressoD";
    static String recipientEmail = "espressoRecipient@gmail.com";
    static String recipientPassword = "espressoR";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule
            = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    // Before the tests, create a new time slot and a new food listing as a donor first
    // Then, sign in as a recipient
    @Before
    public void createSlotAndListing() {
        // Signing in as a donor
        onView(withId(R.id.emailEdt)).perform(typeText(donorEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(typeText(donorPassword), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());
        rest();

        // Creating a new time slot
        onView(withId(R.id.schedule)).perform(click());
        rest();
        onView(withId(R.id.btnAddTimeSlot)).perform(click());
        rest();

        // Select date
        onView(withId(R.id.btnDatePicker)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 8, 21));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Select start time
        onView(withId(R.id.btnStartPicker)).perform(click());
        setTimeValue(onView(withId(R.id.hourPicker)), 8);
        setTimeValue(onView(withId(R.id.minutePicker)), 0);
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Select end time
        onView(withId(R.id.btnEndPicker)).perform(click());
        setTimeValue(onView(withId(R.id.hourPicker)), 8);
        setTimeValue(onView(withId(R.id.minutePicker)), 30);
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Create Time Slot: Aug 21 2021, 08:00 - 08:30
        onView(withId(R.id.btnCreateSlot)).perform(click());
        rest();

        // Creating a new food listing: Burger
        onView(withId(R.id.food)).perform(click());
        rest();
        onView(withId(R.id.fabAddFood)).perform(click());
        rest();
        onView(withId(R.id.foodNameEdt)).perform(typeText("Burger"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEdt)).perform(scrollTo())
                .perform(typeText("Burger with beef and cheese"), closeSoftKeyboard());
        onView(withId(R.id.addFoodItemBtn)).perform(scrollTo(), click());
        rest();
        onView(withId(R.id.recycleViewFood)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.incrementBtn)).perform(scrollTo()).perform(click());
        onView(withId(R.id.updateFoodQuantityBtn)).perform(scrollTo()).perform(click());
        rest();
        pressBack();

        rest();
        onView(withId(R.id.profile)).perform(click());
        rest();
        onView(withId(R.id.logoutTxt)).perform(click());

        rest();
        onView(withId(R.id.emailEdt)).perform(typeText(recipientEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(typeText(recipientPassword), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());
        rest();
        onView(withId(R.id.claimFood)).perform(click());
        rest();
    }

    // Check if recipients are able to claim a food order
    @Test
    public void claimFood() {
        onView(withId(R.id.rv)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText("espressoDonor")), click()
        ));
        rest();
        onView(withId(R.id.rv)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText("Burger")), click()
        ));
        rest();
        onView(withId(R.id.rv)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.incrementBtn)).perform(scrollTo(), click());
        onView(withId(R.id.btnConfirmOrder)).perform(scrollTo(), click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        rest();

        // Check if the order is reflected correctly in the "Schedule" page
        onView(withId(R.id.schedule)).perform(click());
        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_food))
                .check(matches(withText("Burger")));
        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_food_quantity))
                .check(matches(withText("Order quantity: 1")));
        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_date_time))
                .check(matches(withText("Order scheduled at 08:00 - 08:30 on Aug 21 2021")));
        onView(withId(R.id.rv)).perform(actionOnItemAtPosition(0, click()));
        rest();

        onView(withId(R.id.txtDonor)).perform(scrollTo()).check(matches(withText("espressoDonor")));
        onView(withId(R.id.btnCancelOrder)).perform(scrollTo(), click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        rest();
        onView(withId(R.id.profile)).perform(click());
        rest();
        onView(withId(R.id.logoutTxt)).perform(click());
    }


    @After
    public void cleanUpSlotAndListing() {
        onView(withId(R.id.emailEdt)).perform(typeText(donorEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEdt)).perform(typeText(donorPassword), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());
        rest();

        // Delete Time Slot
        onView(withId(R.id.schedule)).perform(click());
        onView(withId(R.id.rv)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.deleteSlot)).perform(click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        rest();

        // Deleting Food Listing
        onView(withId(R.id.food)).perform(click());
        rest();
        onView(withId(R.id.recycleViewFood)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.deleteFoodListingBtn)).perform(scrollTo(), click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

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

    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);
            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }

    public static void setTimeValue(ViewInteraction viewInteraction, int value) {
        viewInteraction.perform(setNumber(value));

        viewInteraction.perform(new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER));
        SystemClock.sleep(50);
        viewInteraction.perform(new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER, Press.FINGER));
        SystemClock.sleep(50);
        if (value == 0) {
            viewInteraction.perform(new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER));
            SystemClock.sleep(50);
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
