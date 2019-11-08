package com.example.mooood;
import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import androidx.fragment.app.DialogFragment;

import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
@RunWith(AndroidJUnit4.class)
public class AddMoodTest {
    private Solo solo;
    public static Calendar calendar;

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
    public void checkAddMood(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "coolestman");
        solo.waitForText("coolestman",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "coolestman");
        solo.waitForText("coolestman",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        //Create mood event with: happy (default emoji), this is a reason, alone, current date and time, no picture for now, no location for now.
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForActivity(CreateEventActivity.class);
        solo.enterText((EditText) solo.getView(R.id.reason),"test reason");
        solo.clickOnView(solo.getView(R.id.social_situation));
        solo.clickOnText("Alone");
        solo.clickOnView(solo.getView(R.id.date_and_time));
        solo.waitForText("OK",1,2);
        solo.clickOnText("OK");
        calendar = Calendar.getInstance();
        solo.waitForText("OK",1,2);
        solo.clickOnText("OK");
        calendar = Calendar.getInstance();
        solo.clickOnView(solo.getView(R.id.submit_button));
        solo.waitForActivity(UserFeedActivity.class);

        //Formats the time correctly
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat scrolltimeFormat = new SimpleDateFormat("h:mm a");
        String scrollTime = scrolltimeFormat.format(calendar.getTime());
        String selectedTime = timeFormat.format(calendar.getTime());
        String selectedDate = dateFormat.format(calendar.getTime());
        if(solo.waitForText(scrollTime,1,100)){
            assertTrue(true);
        }
        else{
            solo.scrollDown();
        }
        //Check mood is in list
        solo.clickOnText(scrollTime);
        solo.waitForActivity(ShowEventActivity.class);
        TextView moodAuthor = (TextView)solo.getView(R.id.author);
        TextView moodDate = (TextView)solo.getView(R.id.date);
        TextView moodTime = (TextView)solo.getView(R.id.time);
        TextView moodSocialSituation = (TextView)solo.getView(R.id.social_situation);
        TextView moodReason =(TextView)solo.getView(R.id.reason);
        assertEquals(moodAuthor.getText().toString(),"coolestman");
        assertEquals(moodSocialSituation.getText().toString(),"Alone");
        assertEquals(moodReason.getText().toString(),"test reason");
        assertEquals(moodDate.getText().toString(), selectedDate);
        assertEquals(moodTime.getText().toString(), selectedTime);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
