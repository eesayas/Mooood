package com.example.mooood;

import android.app.Activity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class FilterMoodsTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

   @Test
    public void checkFilterMood(){
        //login
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "maaz");
        solo.waitForText("maaz",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));

       //======================================================
       // Create most recent happy event
       //======================================================

       //go to CreateEventActivity
       solo.clickOnView(solo.getView(R.id.fab));
       solo.waitForActivity(CreateEventActivity.class);

       //choose HAPPY emoticon
       solo.clickOnView(solo.getView(R.id.select_emoticon_btn));
       solo.clickOnView(solo.getView(R.id.submit_button));

       solo.waitForActivity(UserFeedActivity.class);

       //==========================================================
       // Create most recent sad event (will be on top of happy)
       //==========================================================

       //go to CreateEventActivity
       solo.clickOnView(solo.getView(R.id.fab));
       solo.waitForActivity(CreateEventActivity.class);

       //choose SAD emoticon
       onView(withId(R.id.mood_roster)).perform(swipeLeft());
       solo.clickOnView(solo.getView(R.id.select_emoticon_btn));
       solo.clickOnView(solo.getView(R.id.submit_button));

       solo.waitForActivity(UserFeedActivity.class);

       //==========================================================
       // Filter for happy
       //==========================================================
        solo.clickOnView(solo.getView(R.id.userSearchView));
        SearchView searchView = (SearchView) solo.getView(R.id.userSearchView);
        int id= searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText= searchView.findViewById(id);
        solo.enterText(editText, "happy");
        solo.waitForText("happy",1,20000);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
        solo.sleep(5000); //give time for result

       //==========================================================
       // After filter, happy should be on top!
       //==========================================================
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        solo.waitForActivity(ShowEventActivity.class);

        ImageView emotion = (ImageView) solo.getView(R.id.emoticon);
        assertEquals(emotion.getTag(), new Emoticon("HAPPY", 2).getImageLink());
     }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

