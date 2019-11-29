package com.example.mooood;

import android.app.Activity;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * This test case is for
 *
 * US 01.01.01
 * As a participant, I want to add a mood event to my mood history, each event with the current date
 * and time, a required emotional state, optional reason, and optional social situation.
 *
 * US 01.03.01
 * As a participant, I want to view a given mood event and all its available details.
 *
 * US 02.01.01
 * As a participant, I want to express the reason why for a mood event using a brief textual
 * explanation (no more than 20 characters or 3 words).
 *
 * US 02.02.01
 * As a participant, I want to express the reason why for a mood event using a photograph.
 *
 * US 02.03.01
 * As a participant, I want to specify the social situation for a mood event to be one of: alone,
 * with one other person, with two to several people, or with a crowd.
 *
 */

@RunWith(AndroidJUnit4.class)
public class CreateAndShowMoodEventTest {

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
     * This creates a MoodEvent with all details filled out (except GPS)
     * Then checks if the MoodEvent is in the UserFeedActvity
     * and click on it to ShowEventActivity which displays all details
     *
     *  for US 01.01.01 & US 01.03.01 & US 02.02.01 & US 02.03.01
     */
    @Test
    public void checkCreateAndShow(){
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
        assertEquals(reasonTxt.getTag().toString(), "reason");
        assertEquals(reasonImg.getTag().toString(), "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");

        solo.sleep(2000); //for visual
    }

    /**
     * This will check if reason will only be accepted when it is no more than 20 characters or 3 words
     *
     *  for US 02.01.01
     */
    @Test
    public void checkReason(){
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

        //selects the first emoticon (default: "HAPPY")
        solo.clickOnView(solo.getView(R.id.select_emoticon_btn));

        //more than 20 characters
        solo.enterText((EditText) solo.getView(R.id.reason), "supercalifragilisticexpialidocious");
        solo.waitForText("supercalifragilisticexpialidocious", 1, 2000);

        solo.assertCurrentActivity("Wrong Activity", CreateEventActivity.class); //no redirect

        //more than 3 characters
        solo.clearEditText((EditText) solo.getView(R.id.reason));
        solo.enterText((EditText) solo.getView(R.id.reason), "a b c d");
        solo.waitForText("a b c d", 1, 2000);

        solo.assertCurrentActivity("Wrong Activity", CreateEventActivity.class); //no redirect

        //proper input
        solo.clearEditText((EditText) solo.getView(R.id.reason));
        solo.enterText((EditText) solo.getView(R.id.reason), "correct");
        solo.waitForText("correct", 1, 2000);

        //submit
        solo.clickOnView(solo.getView(R.id.submit_button));

        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class); //no redirect

        solo.sleep(2000); //for visual

    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}