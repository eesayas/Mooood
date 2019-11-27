package com.example.mooood;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ShowMoodEventTest {
    private Solo solo;
    public static Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), rule.getActivity());
    }


    /**
     * This creates a minimal mood event and checks if ShowEventActivity displays it
     */
    @Test
    public void checkShow(){

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

        //click on submit (this newly created MoodEvent has only the REQUIRED minimum data)
        solo.clickOnView(solo.getView(R.id.submit_button));

        //device should redirect to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);

        //access the RecyclerView where the created MoodEvent should've been added
        UserFeedActivity activity = (UserFeedActivity) solo.getCurrentActivity();

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //go to the ShowEventActivity of the choosen MoodEvent
        solo.waitForActivity(ShowEventActivity.class);

        //get important TextView

        TextView author = (TextView) solo.getView(R.id.author);
        ImageView emotion = (ImageView) solo.getView(R.id.emoticon);
        TextView date = (TextView) solo.getView(R.id.date);
        TextView time = (TextView) solo.getView(R.id.time);

        //ASSERTIONS!!!
        assertEquals(author.getText().toString(), "eesayas");
        assertEquals(emotion.getTag(), new Emoticon("HAPPY", 2).getImageLink());
        assertEquals(time.getText().toString(), currentTime);
        assertEquals(date.getText().toString(), currentDate);

        solo.sleep(2000); //for visual
    }


    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}