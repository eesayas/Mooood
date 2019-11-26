package com.example.mooood;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class UserProfileTest {

    private Solo solo;

    //Firebase setup
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private CollectionReference feedCollectionReference;

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
     * Logs on, clicks on Profiler then clicks on log out
     *
     * @throws Exception
     */
    @Test
    public void profileFromUser(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        solo.clickOnView(solo.getView(R.id.activity_user_feed_show_profile));
        solo.waitForActivity(UserProfile.class);
        solo.assertCurrentActivity("Wrong Activity", UserProfile.class);


        TextView userId = (TextView)solo.getView(R.id.activity_user_profile_tv_usernam);
        final TextView userDate = (TextView)solo.getView(R.id.activity_user_profile_tv_date);
        final TextView userTime = (TextView)solo.getView(R.id.activity_user_profile_time);


        assertEquals(userId.getText().toString(),"hyeon");

        db.collection("Users")
                .whereEqualTo("author", "hyeon")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                            String dbDate = (String) documentSnapshot.getData().get("date");
                            String dbTime = (String) documentSnapshot.getData().get("time");

                            assertEquals(userDate.getText().toString(),dbDate);
                            assertEquals(userTime.getText().toString(), dbTime);



                        }

                    }
                });


        solo.clickOnView(solo.getView(R.id.activity_user_profile_bt_logout));
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }


    /**
     * Logs on, clicks on Profile then clicks on log out
     *
     * @throws Exception
     */
    @Test
    public void profileFromFeed(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__username), "hyeon");
        solo.waitForText("hyeon",1,2000);
        solo.enterText((EditText)solo.getView(R.id.activity_main_et__password), "1");
        solo.waitForText("1",1,2000);
        solo.clickOnView(solo.getView(R.id.activity_main_btn_submit));
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", UserFeedActivity.class);

        solo.clickOnView(solo.getView(R.id.feedButton));
        solo.waitForActivity(UserFeedActivity.class);
        solo.assertCurrentActivity("Wrong Activity", feedActivity.class);

        solo.clickOnView(solo.getView(R.id.activity_user_feed_show_profile));
        solo.waitForActivity(UserProfile.class);
        solo.assertCurrentActivity("Wrong Activity", UserProfile.class);


        TextView userId = (TextView)solo.getView(R.id.activity_user_profile_tv_usernam);
        final TextView userDate = (TextView)solo.getView(R.id.activity_user_profile_tv_date);
        final TextView userTime = (TextView)solo.getView(R.id.activity_user_profile_time);


        assertEquals(userId.getText().toString(),"hyeon");

        db.collection("Users")
                .whereEqualTo("author", "hyeon")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                            String dbDate = (String) documentSnapshot.getData().get("date");
                            String dbTime = (String) documentSnapshot.getData().get("time");

                            assertEquals(userDate.getText().toString(),dbDate);
                            assertEquals(userTime.getText().toString(), dbTime);



                        }

                    }
                });


        solo.clickOnView(solo.getView(R.id.activity_user_profile_bt_logout));
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
