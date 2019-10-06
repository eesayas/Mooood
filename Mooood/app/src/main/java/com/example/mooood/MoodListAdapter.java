package com.example.mooood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MoodListAdapter extends ArrayAdapter<Mood> {
    private Context context;
    private List<Mood> moods;

    public MoodListAdapter(Context context,List<Mood> moods) {
        super(context, 0, moods);
        this.context = context;
        this.moods = moods;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            //Inflates adapter with the mood list layout in item_mood
            view = LayoutInflater.from(context).inflate(R.layout.item_mood,parent,false);
        }

        //Retrieves the layout elements
        TextView dateTextView=view.findViewById(R.id.item_mood_tv_date);
        TextView timeTextView=view.findViewById(R.id.item_mood_tv_time);
        TextView emotionalStateTextView=view.findViewById(R.id.item_mood_tv_emotional_state);
        TextView reasonTextView= view.findViewById(R.id.item_mood_tv_reason);
        TextView socialSituationTextView=view.findViewById(R.id.item_mood_tv_social_situation);
        TextView locationTextView=view.findViewById(R.id.item_mood_tv_location);

        Mood mood=moods.get(position);

        //Sets layout elements accordingly
        dateTextView.setText(mood.getDate());
        timeTextView.setText(mood.getTime());
        emotionalStateTextView.setText(mood.getEmotionalState());
        reasonTextView.setText(mood.getReason());
        socialSituationTextView.setText(mood.getSocialSituation());
        locationTextView.setText("Sample Location");//TODO:Set an actual location

        return view;
    }

}