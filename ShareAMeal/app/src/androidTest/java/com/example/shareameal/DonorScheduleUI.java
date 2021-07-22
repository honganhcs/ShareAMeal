package com.example.shareameal;

import android.os.SystemClock;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    // Check that the donor is able to add, view and delete a time slot successfully
    @Test
    public void addNewTimeSlot() {
        rest();
        onView(withId(R.id.btnAddTimeSlot)).perform(click());
        rest();

        // Select date
        onView(withId(R.id.btnDatePicker)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 8, 21));
        rest();
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Select start time
        onView(withId(R.id.btnStartPicker)).perform(click());
        setTimeValue(onView(withId(R.id.hourPicker)), 8);
        setTimeValue(onView(withId(R.id.minutePicker)), 0);
        rest();
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Select end time
        onView(withId(R.id.btnEndPicker)).perform(click());
        setTimeValue(onView(withId(R.id.hourPicker)), 8);
        setTimeValue(onView(withId(R.id.minutePicker)), 30);
        rest();
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Create Time Slot
        onView(withId(R.id.btnCreateSlot)).perform(click());
        rest();

        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_date))
                .check(matches(withText("Aug 21 2021")));
        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_time))
                .check(matches(withText("08:00 to 08:30")));
        onView(new RecyclerViewMatcher(R.id.rv).atPositionOnView(0, R.id.txt_availability))
                .check(matches(withText("Not Reserved")));

        // Delete Time Slot
        onView(withId(R.id.rv)).perform(actionOnItemAtPosition(0, click()));
        rest();
        onView(withId(R.id.deleteSlot)).perform(click());
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        rest();
        onView(withId(R.id.rv)).check(new DonorScheduleUI.RecyclerViewItemCountAssertion(0));
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
}
