package com.example.mareu.Controler.Fragment;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import com.example.mareu.Controler.Activity.MainActivity;
import com.example.mareu.Controler.UtilsTests.DeleteViewAction;
import com.example.mareu.Model.Reunion;
import com.example.mareu.Model.Room;
import com.example.mareu.Services.DummyReunionApiService;
import com.example.mareu.Services.ReunionApiService;
import com.example.mareu.Services.RoomsGenerator;


import android.widget.DatePicker;
import androidx.test.espresso.ViewInteraction;
import com.example.mareu.R;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;


import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import static com.example.mareu.Controler.UtilsTests.RecyclerViewItemCountAssertion.withItemCount;
import static com.example.mareu.Controler.UtilsTests.ToolbarMatcher.childAtPosition;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ListReunionsFragmentTest {

    private MainActivity mReunionListActivity;
    private static int ITEMS_COUNT = 3;

    private ReunionApiService mApiService = new DummyReunionApiService();
    private List<Reunion> mReunions = mApiService.getReunions();
    private Room[] mRooms = RoomsGenerator.getListRooms();
    private List<String> aMails = new ArrayList<>();

    // rules
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp()
    {
        mReunionListActivity = mActivityRule.getActivity();
        assertThat(mReunionListActivity, notNullValue());

        mApiService.getReunions().clear();

        aMails.add("mail1@test.fr");
        aMails.add("mail2@test.fr");

        Reunion reunionTest1 = new Reunion(aMails, mRooms[0], "Réunion Test 1", "10:00", "22/10/2020");
        Reunion reunionTest2 = new Reunion(aMails, mRooms[1], "Réunion Test 2", "15:00", "22/10/2020");
        Reunion reunionTest3 = new Reunion(aMails, mRooms[2], "Réunion Test 3", "14:00", "12/12/2020");

        mApiService.addReunion(reunionTest1);
        mApiService.addReunion(reunionTest2);
        mApiService.addReunion(reunionTest3);
    }

    @Test
    public void myReunionList_shouldNotBeEmpty()
    {
        //On the first page, we have the 3 Reu add in the SetUp
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(mReunions.size()));
    }

    @Test
    public void myReunionList_deleteAction_shouldRemoveItem()
    {
        // Given : We check that we have 3 Reunions
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT));

        // When : We perform a click on a delete button
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).perform(RecyclerViewActions.actionOnItemAtPosition(1, new DeleteViewAction()));

        // Then : The Reunions List has 2 items
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT - 1));

    }

    @Test
    public void myReunionList_clickToolbarAction_shouldDisplayFilterDate()
    {
        // Given : We check that we have 3 Reunions
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT));

        // When : We click on the toolbar and put a Date
        onView(withId(R.id.menu_button)).perform(click());
        onView(ViewMatchers.withText("Filtre par Date")).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2020, 10, 22));
        onView(withId(android.R.id.button1)).perform(click());

        // Then : We have the reunions with this date
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(2));

    }

    @Test
    public void myReunionList_clickToolbarAction_shouldDisplayFilterRoom()
    {
        // Given : We check that we have 3 Reunions
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT));

        // When : We click on the toolbar and choose a room
        onView(withId(R.id.menu_button)).perform(click());
        onView(ViewMatchers.withText("Filtre par Salle")).perform(click());

        //Create a ViewInteraction to click on the spinner
        ViewInteraction appCompatSpinner = onView(allOf(withId(R.id.dsf_spinner),
                childAtPosition(allOf(withId(R.id.dsf_layout),
                        childAtPosition(withId(android.R.id.custom), 0)), 0),
                isDisplayed()));
        appCompatSpinner.perform(click());
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(2).perform(click());

        onView(ViewMatchers.withText("FILTRER")).perform(click());

        // Then : The list has 1 reunion
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(1));


    }

    @Test
    public void myReunionList_clickToolbarAction_shouldDisplayAll()
    {
        // Given : We check that we have 3 Reunions
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT));

        // When : We click on the toolbar and put a Date to change the list
        onView(withId(R.id.menu_button)).perform(click());
        onView(ViewMatchers.withText("Filtre par Date")).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2020, 10, 22));
        onView(withId(android.R.id.button1)).perform(click());
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(2));

        // When : We click on the toolbar and choose All the Reunions
        onView(withId(R.id.menu_button)).perform(click());
        onView(ViewMatchers.withText("Toutes les Réunions")).perform(click());

        // Then : The list has all the reunions
        onView(ViewMatchers.withId(R.id.fragment_list_reunions)).check(withItemCount(ITEMS_COUNT));

    }

    @Test
    public void myReunionList_clickAction_shouldDisplayDetailsActivity()
    {
        // When : we click on the fab button
        onView(ViewMatchers.withId(R.id.fab)).perform(click());

        // Then : Go to the Add Reunion Fragment
        onView(ViewMatchers.withId(R.id.add_reunion_fragment)).check(matches(isDisplayed()));

    }


}