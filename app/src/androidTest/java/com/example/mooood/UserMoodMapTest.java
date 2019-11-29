package com.example.mooood;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *This test case is for
 * US 06.02.01
 *As a participant, I want to see a map of the mood events (showing their emotional states)
 * from my mood history list (that have locations).
 * So the participant can see where the participant was feeling all the mood events.
 */

@RunWith(AndroidJUnit4.class)
public class UserMoodMapTest {

    private Solo solo;
    private Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * This creates a MoodEvent with all details filled out including location
     * Then checks if the MoodEvent contains a latitude and longitude both in the UserFeed
     * and in the maps activity where it's called.
     *
     * Tackles:
     *  - US 06.02.01
     */
    @Test
    public void checkAdd(){
        //go to Login
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //enter username and password
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "eesayas");
        solo.waitForText("eesayas",1,2000);

        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "lol");
        solo.waitForText("lol",1,2000);

        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));

        //go to UserFeedActivity (MoodEvent history)
        solo.waitForActivity(UserFeedActivity.class);

        //go to CreateEventActivity
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);

        //get current date/time for comparison later
        calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");

        String currentTime = timeFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());

        //selects the first emoticon (default: "HAPPY")
        solo.clickOnView(solo.getView(R.id.select_emoticon_btn));

        //input reason
        solo.enterText((EditText) solo.getView(R.id.reason), "reason");
        solo.waitForText("reason", 1, 2000);

        //input social situation
        solo.clickOnView(solo.getView(R.id.social_situation));
        solo.clickOnText("Alone");

        //get location
        solo.clickOnView(solo.getView(R.id.toggle_gps_preview));

        //click on submit
        solo.clickOnView(solo.getView(R.id.submit_button));

        //device should redirect to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);

        //access the RecyclerView where the created MoodEvent should've been added
        UserFeedActivity activity = (UserFeedActivity) solo.getCurrentActivity();

        ArrayList<MoodEvent> userFeedMoods = activity.getPostDataList();
        solo.clickOnView(solo.getView(R.id.map_user_feed_button));
        solo.clickOnImageButton(0);
        solo.waitForActivity(MoodsMapActivity.class);
        MoodsMapActivity mapActivity = (MoodsMapActivity) solo.getCurrentActivity();
        ArrayList<MoodEvent> mapMoods = mapActivity.getMoodsList();

        assertEquals(mapMoods.get(0).getLatitude(),userFeedMoods.get(0).getLatitude());

        solo.sleep(2000); //for visual
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
