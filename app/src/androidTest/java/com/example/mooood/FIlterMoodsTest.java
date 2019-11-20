package com.example.mooood;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class FIlterMoodsTest {
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
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "maaz1");
        solo.waitForText("maaz1",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);

        solo.clickOnView(solo.getView(R.id.userSearchView));
        solo.enterText((SearchView)solo.getView(R.id.userSearchView), "happy");
        solo.sendKey(KeyEvent.KEYCODE_ENTER);

        //
        /*final ListView list = (ListView) solo.getView(R.id.posts_list);
        solo.clickInList(0);
        solo.waitForActivity(ShowEventActivity.class);

        TextView moodAuthor = (TextView)solo.getView(R.id.author);

        assertEquals(moodAuthor.getText().toString(),"maaz1");
*/

    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

