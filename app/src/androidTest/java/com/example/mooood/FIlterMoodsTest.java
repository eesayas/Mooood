package com.example.mooood;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import java.util.List;

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
       solo.waitForText("happy",1,20000);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);


        solo.clickInList(0);
        solo.waitForActivity(ShowEventActivity.class);

       List<Emoticon> moodImages= new ArrayList<>();
       moodImages.add(new Emoticon("HAPPY", 2));
       moodImages.add(new Emoticon("SAD", 2));
       moodImages.add(new Emoticon("LAUGHING", 2));
       moodImages.add(new Emoticon("IN LOVE", 2));
       moodImages.add(new Emoticon("ANGRY", 2));
       moodImages.add(new Emoticon("SICK", 2));
       moodImages.add(new Emoticon("AFRAID", 2));

       ImageView imageView = (ImageView)solo.getView(R.id.emoticon);
       int drawable = R.drawable.happy_cow_v2;
       Log.d("draw", String.valueOf(drawable));
       Drawable idImage= imageView.getDrawable();
       int index= moodImages.indexOf(imageView);
       Log.d("printout", moodImages.get(index).getEmotionalState());
       //assertEquals(moodImages.get(index).getEmotionalState(), "HAPPY");

       }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

