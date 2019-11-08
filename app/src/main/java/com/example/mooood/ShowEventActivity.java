package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


public class ShowEventActivity extends AppCompatActivity {
    private static final String TAG = "For Testing";
    public static final String MOODEVENT = "Mood Event";

    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        final MoodEvent moodEvent = intent.getParcelableExtra(MOODEVENT);

        //get all values of MoodEvent
        String author = moodEvent.getAuthor();

        String date = moodEvent.getDate();
        String time = moodEvent.getTime();
        String emotionalState = moodEvent.getEmotionalState();

        String socialSituation = moodEvent.getSocialSituation();
        String imageUrl = moodEvent.getImageUrl();
        String reason = moodEvent.getReason();

        //get TextViews and ImageViews
        TextView authorText = findViewById(R.id.author);
        ImageView emoticon = findViewById(R.id.emoticon);
        TextView dateText = findViewById(R.id.date);
        TextView timeText = findViewById(R.id.time);
        TextView socialSituationText = findViewById(R.id.social_situation);
        ImageView imageReason = findViewById(R.id.image_reason);
        TextView reasonText = findViewById(R.id.reason);

        //set values for TextViews and ImageViews
        authorText.setText(author);
        emoticon.setImageResource(new Emoticon(emotionalState, 2).getImageLink());
        dateText.setText(date);
        timeText.setText(time);
        socialSituationText.setText(socialSituation);
        Picasso.get().load(imageUrl).into(imageReason);
        reasonText.setText(reason);


        //click listener for edit post
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowEventActivity.this, EditEventActivity.class);
                intent.putExtra("Mood Event", moodEvent);
                startActivity(intent);
            }
        });

    }
}
