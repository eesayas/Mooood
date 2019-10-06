//Main Activity
package com.example.mooood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddMoodFragment.OnFragmentInteractionListener {
    private ListView moodList;
    private ArrayAdapter<Mood> moodListAdapter;
    private Button addMoodButton;
    private Button deleteMoodButton;
    public Mood toDelete;
    public ArrayList<Mood> MoodList = new ArrayList<Mood>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moodList = findViewById(R.id.activity_main_lv_mood_list);
        deleteMoodButton=findViewById(R.id.activity_main_btn_delete);
        addMoodButton=findViewById(R.id.activity_main_btn_add);
        addMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddMoodFragment().show(getSupportFragmentManager(), "ADD_MOOD");

            }
        });
        moodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Calls AddMood fragment
                AddMoodFragment.newInstance(MoodList.get(position)).show(getSupportFragmentManager(), "EDIT_MOOD");
            }
        });

        moodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                moodList.setSelection(position);
                //Show and hide button based on:https://stackoverflow.com/questions/21899825/show-hide-button-when-focus-the-list-item-in-android-listview
                deleteMoodButton.setVisibility(View.VISIBLE);
                view.setSelected(true);
                setToDelete(MoodList.get(position));
                //Calls the delete listener when clicking the delete mood button
                DeleteListener listener= new DeleteListener(getToDelete(), moodListAdapter);
                deleteMoodButton.setOnClickListener(listener);
                return true;
            }

        });
        moodListAdapter = new MoodListAdapter(this, MoodList);
        moodList.setAdapter(moodListAdapter);

    }

    //Both are interface methods from AddMoodFragment to allow for addition and editing of moods.
    public void addMood(Mood mood){
        moodListAdapter.add(mood);
        moodListAdapter.notifyDataSetChanged();
    }

    public void editMood(Mood mood,String date,String time,String emotionalState,String reason,String socialSituation){
        mood.setTime(time);
        mood.setDate(date);
        mood.setEmotionalState(emotionalState);
        mood.setReason(reason);
        mood.setSocialSituation(socialSituation);
        moodListAdapter.notifyDataSetChanged();
    }

    //Sets mood to delete
    public void setToDelete(Mood toDelete) {
        this.toDelete = toDelete;
    }
    //Retrieves mood to delete
    public Mood getToDelete() {
        return toDelete;
    }
}
