package com.example.mooood;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QuerySnapshot;


        import org.w3c.dom.Text;

        import java.time.LocalDateTime;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.time.format.DateTimeFormatter;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;


public class followerActivity extends AppCompatActivity {
    Button followButton;
    Button backButton;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    TextView setDate;
    TextView setTime;
    TextView setAuthor;
    String loginName;
    String toFollow;

    /**
     * This implements all methods below accordingly
     * Will also check to see if user has already sent a request to change text of button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_profile);

        setDate=findViewById(R.id.date);
        setTime = findViewById(R.id.time);
        setAuthor= findViewById(R.id.author);
        collectionReference=db.collection("MoodEvents");
        Intent intent = getIntent();
        toFollow = intent.getStringExtra("accountMood");
        loginName= intent.getStringExtra("loginName");
        final MoodEvent moodEvent = intent.getParcelableExtra("mood");

        setDate.setText(moodEvent.getDate());
        setTime.setText(moodEvent.getTime());
        setAuthor.setText(moodEvent.getAuthor());


        checkButtonContent();
        followUser();
        backToFeed();
    }//End of onCreate

    /**
     * After clicking follow button, will send a request to the account while adding request collection to database
     * If already following, cannot send another request
     */
    private void followUser() {
        followButton = findViewById(R.id.follow_button);
        Date currentTime = Calendar.getInstance().getTime();
        //LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat requestDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm a");
        final String date = requestDateFormat.format(currentTime);

        final Map<String, Object> request = new HashMap<>();
        collectionReference.document(toFollow).collection("Request").document(loginName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Follow", "Already following");
                    } else {
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                request.put("Username",loginName);
                                request.put("Request", "Sent");
                                request.put("Request Time", date);
                                collectionReference.document(toFollow).collection("Request").document(loginName).set(request);
                                followButton.setText("REQUEST SENT");
                                Log.d("SENT", "request sent");
                            }
                        });
                    }
                } else {
                    Log.d("checking", "Failed with: ", task.getException());
                }
            }
        });

    }

    /**
     * Will take user back to Feed Activity by clicking on the back button
     */
    private void backToFeed(){
        backButton= findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Will check to see content of the button, if already sent a request then Button will set to "request sent" if not it will be "follow"
     */
    private void checkButtonContent(){
        collectionReference.document(toFollow).collection("Request").document(loginName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        followButton.setText("REQUEST SENT");
                    } else {
                        followButton.setText("FOLLOW");
                    }
                } else {
                    Log.d("checking", "Failed with: ", task.getException());
                }
            }
        });
    }



}

