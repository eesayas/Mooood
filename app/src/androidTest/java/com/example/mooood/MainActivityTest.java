package com.example.mooood;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.google.firebase.database.DatabaseReference.goOnline;


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
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "0");
        solo.waitForText("0",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Username or password is incorrect", 1,2000);
    }

    /**
     * Check user trying to log in without giving username or password
     */
    @Test
    public void checkEmpty(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);

        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Username or password can not be empty", 1,2000);

        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "");
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Username or password can not be empty", 1,2000);


        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));

    }

    /**
     * Check if user can sign up
     */
    @Test
    public void checkSignUp(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.activity_main_tv_signUp));

        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "aa");
        solo.waitForText("abc",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "a");
        solo.waitForText("11",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("participant").document("aa")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("testing", "DocumentSnapshot successfully deleted!");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("testing", "Error deleting document", e);
                    }
                });
//
    }

    /**
     * Check user trying to sign up with account that already exists
     */
    @Test
    public void checkSignUpFail(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.activity_main_tv_signUp));
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForText("Account already exists", 1,2000);
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
