package com.example.mooood;

import android.app.Activity;
import android.app.Instrumentation;
import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.google.firebase.database.DatabaseReference.goOffline;
import static com.google.firebase.database.DatabaseReference.goOnline;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 * used
 */
public class MainActivityTest {
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


    /**
     * Check if user can log in
     */
    @Test
    public void checkLogin(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
    }

    /**
     * Check user trying to log in without correct account
     */
    @Test
    public void checkLoginFail(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("0",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Username or password is incorrect", 1,2000);
    }

    /**
     * Check user trying to log in without correct account
     */
    @Test
    public void checkEmpty(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);

        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Username or password can not be empty", 1,2000);
    }

    /**
     * Check if user can sign up
     */
    @Test
    public void checkSignUp(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.activity_main_tv_signUp));
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "a");
        solo.waitForText("a",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "11");
        solo.waitForText("11",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("participant").document("a");
        docRef.delete();

        solo.waitForActivity(UserFeedActivity.class);

    }



    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
