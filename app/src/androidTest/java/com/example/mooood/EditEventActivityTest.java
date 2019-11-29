package com.example.mooood;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLDisplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * This is entire test case is for
 *
 * US 01.04.01
 * As a participant, I want to edit the details of a given mood event of mine.
 */

public class EditEventActivityTest {
    private Solo solo;
    public static Calendar calendar;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), rule.getActivity());
    }

    /**
     * This simply creates a MoodEvent that will be edited in checkEdit() test (below)
     */
    @Test
    public void checkCreate(){
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
        solo.waitForText("Alone", 1, 2000);

        //turn on image upload switch
        solo.clickOnView(solo.getView(R.id.toggle_image_preview));

        //append image
        CreateEventActivity createEventActivity = (CreateEventActivity) solo.getCurrentActivity();
        MoodEvent moodEvent = createEventActivity.moodEvent;
        moodEvent.setImageUrl("https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");

        //click on submit
        solo.clickOnView(solo.getView(R.id.submit_button));

        //device should redirect to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);

        solo.sleep(5000);

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //go to the ShowEventActivity of the choosen MoodEvent
        solo.waitForActivity(ShowEventActivity.class);
        solo.scrollDown();

        //get important TextView
        TextView author = (TextView) solo.getView(R.id.author);
        ImageView emotion = (ImageView) solo.getView(R.id.emoticon);
        TextView date = (TextView) solo.getView(R.id.date);
        TextView time = (TextView) solo.getView(R.id.time);

        TextView socialSituation = (TextView) solo.getView(R.id.social_situation);
        TextView reasonTxt = (TextView) solo.getView(R.id.reason);
        ImageView reasonImg = (ImageView) solo.getView(R.id.image_reason);


        //ASSERTIONS!!!
        assertEquals(author.getText().toString(), "eesayas");
        assertEquals(emotion.getTag(), new Emoticon("HAPPY", 2).getImageLink());
        assertEquals(time.getText().toString(), currentTime);
        assertEquals(date.getText().toString(), currentDate);

        assertEquals(socialSituation.getText().toString(), "Alone");
        assertEquals(reasonTxt.getText().toString(), "reason");
        assertEquals(reasonImg.getTag().toString(), "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");

        solo.sleep(2000); //for visual
    }



    @Test
    public void checkEdit(){
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

        solo.sleep(5000);

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //go to the ShowEventActivity of the choosen MoodEvent
        solo.waitForActivity(ShowEventActivity.class);
        solo.scrollDown();

        //click to edit
        solo.clickOnView(solo.getView(R.id.edit_button));
        solo.waitForActivity(EditEventActivity.class);

        //change reason
        solo.clearEditText((EditText) solo.getView(R.id.reason));
        solo.enterText((EditText) solo.getView(R.id.reason), "new reason");
        solo.waitForText("new reason", 1, 2000);

        //change social situation
        solo.clickOnView(solo.getView(R.id.social_situation));
        solo.clickOnText("With Someone");
        solo.waitForText("With Someone", 1, 2000);

        //change image
        EditEventActivity editEventActivity = (EditEventActivity) solo.getCurrentActivity();
        MoodEvent moodEvent = editEventActivity.moodEvent;
        moodEvent.setImageUrl("https://ohsheglows.com/gs_images/2019/03/Simple-Meal-Prep-Power-Bowls-00923-5-256x256.jpg");

        //click on submit
        solo.clickOnView(solo.getView(R.id.submit_button));

        //device should redirect to UserFeedActivity
        solo.waitForActivity(UserFeedActivity.class);

        solo.sleep(5000);

        //click on first item
        onView(withId(R.id.posts_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //go to the ShowEventActivity of the choosen MoodEvent
        solo.waitForActivity(ShowEventActivity.class);
        solo.scrollDown();

        //get important TextView
        TextView author = (TextView) solo.getView(R.id.author);
        TextView socialSituation = (TextView) solo.getView(R.id.social_situation);
        TextView reasonTxt = (TextView) solo.getView(R.id.reason);
        ImageView reasonImg = (ImageView) solo.getView(R.id.image_reason);

        //ASSERTIONS!!!
        assertEquals(author.getText().toString(), "eesayas");
        assertEquals(socialSituation.getText().toString(), "With Someone");
        assertEquals(reasonTxt.getText().toString(), "new reason");
        assertEquals(reasonImg.getTag().toString(), "https://ohsheglows.com/gs_images/2019/03/Simple-Meal-Prep-Power-Bowls-00923-5-256x256.jpg");

        solo.sleep(2000); //for visual
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}

