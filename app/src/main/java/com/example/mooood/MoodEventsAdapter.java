package com.example.mooood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class MoodEventsAdapter extends ArrayAdapter<MoodEvent> {

    private ArrayList<MoodEvent> moodEvents;
    private Context context;

    public MoodEventsAdapter(ArrayList<MoodEvent> moodEvents, Context context){
        super(context, 0, moodEvents);
        this.moodEvents = moodEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_user_feed, parent,false);
        }

        MoodEvent moodEvent = moodEvents.get(position);

        //select TextViews
        TextView emotionalStateText = view.findViewById(R.id.emotional_state_text);

        //setText of TextViews
        emotionalStateText.setText(moodEvent.getEmotionalState());

        return view;
    }
}