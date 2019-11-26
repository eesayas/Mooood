package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * This is responsible for showing all the details of a selected MoodEvent
 **/

public class ShowEventActivity extends AppCompatActivity {
    private static final String TAG = "For Testing";
    public static final String MOOD_EVENT = "Mood Event";

    String author;
    String date;
    String time;
    String emotionalState;
    String socialSituation;
    String imageUrl;
    String reason;

    TextView authorText;
    ImageView emoticon;
    TextView dateText;
    TextView timeText;
    TextView socialSituationText;
    ImageView imageReason;
    TextView reasonText;
    String edit;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOOD_EVENT);
        edit = intent.getStringExtra("bool");

        getValuesMoodEvent(moodEvent);

        getTextAndImageView();

        setTextAndImageView();

        editBtnClickListener(moodEvent);

    }

    /**
     * This gets all needed values from MoodEvent to be displayed
     *
     * @param moodEvent This is the MoodEvent object
     */
    private void getValuesMoodEvent(MoodEvent moodEvent) {
        author = moodEvent.getAuthor();

        date = moodEvent.getDate();
        time = moodEvent.getTime();
        emotionalState = moodEvent.getEmotionalState();

        socialSituation = moodEvent.getSocialSituation();
        imageUrl = moodEvent.getImageUrl();
        reason = moodEvent.getReason();
    }

    /**
     * This selects all TextView and ImageView from xml
     */
    private void getTextAndImageView() {
        authorText = findViewById(R.id.author);
        emoticon = findViewById(R.id.emoticon);
        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        socialSituationText = findViewById(R.id.social_situation);
        imageReason = findViewById(R.id.image_reason);
        reasonText = findViewById(R.id.reason);
    }

    /**
     * This sets the values of all selected TextView and ImageView from xml
     */
    private void setTextAndImageView() {
        authorText.setText(author);
        emoticon.setImageResource(new Emoticon(emotionalState, 2).getImageLink());
        dateText.setText(date);
        timeText.setText(time);
        socialSituationText.setText(socialSituation);
        Picasso.get().load(imageUrl).into(imageReason);
        reasonText.setText(reason);
    }

    /**
     * This is a click listener for edit of MoodEvent. Redirect to EditEventActivity
     */
    private void editBtnClickListener(final MoodEvent moodEvent) {
        editButton = findViewById(R.id.edit_button);
        if (edit.equals("false")) {
            editButton.setEnabled(false);
            editButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setEnabled(true);
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ShowEventActivity.this, EditEventActivity.class);
                    intent.putExtra(MOOD_EVENT, moodEvent);
                    startActivity(intent);
                }
            });
        }
    }
}