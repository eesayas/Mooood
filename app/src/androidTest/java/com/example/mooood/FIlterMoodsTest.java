package com.example.mooood;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "maaz");
        solo.waitForText("maaz1",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
       SearchView userSearchView= (SearchView)solo.getView(R.id.userSearchView);
       userSearchView.setIconified(false);
        solo.clickOnView(solo.getView(R.id.userSearchView));
        SearchView searchView = (SearchView) solo.getView(R.id.userSearchView);
        int id= searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText= searchView.findViewById(id);
        solo.enterText(editText, "happy");
        solo.sendKey(KeyEvent.KEYCODE_ENTER);

       ArrayList<String> emotions= new ArrayList<>();
       emotions.add("HAPPY");
       emotions.add("SAD");
       emotions.add("LAUGHING");
       emotions.add("IN LOVE");
       emotions.add("ANGRY");
       emotions.add("SICK");
       emotions.add("AFRAID");

       HashMap<String, Integer> emoticonData = new HashMap<>();
       emoticonData.put("HAPPY", R.drawable.happy_cow_v2);
       emoticonData.put("SAD", R.drawable.sad_cow_v2);
       emoticonData.put("LAUGHING", R.drawable.laughing_cow_v2);
       emoticonData.put("IN LOVE", R.drawable.inlove_cow_v2);
       emoticonData.put("ANGRY", R.drawable.angry_cow_v2);
       emoticonData.put("SICK", R.drawable.sick_cow_v2);
       emoticonData.put("AFRAID", R.drawable.afraid_cow_v2);


       final ListView list = (ListView) solo.getView(R.id.posts_list);


        solo.clickInList(0);
        solo.waitForActivity(ShowEventActivity.class);

        ImageView image= (ImageView)solo.getView(R.id.emoticon);
        TextView moodAuthor = (TextView)solo.getView(R.id.author);
        //assertEquals(e, emoticonData.get("HAPPY"));

       assertEquals(moodAuthor.getText().toString(),"maaz");
       }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

