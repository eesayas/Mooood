package com.example.mooood;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class MoodEventsAdapter extends ArrayAdapter<MoodEvent> {

    private static final String TAG = "For Testing";
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
        ImageView emoticon = view.findViewById(R.id.emoticon);
        TextView author = view.findViewById(R.id.author);
        TextView relativeTime = view.findViewById(R.id.date_and_time);

        //setText of TextViews
        emoticon.setImageResource(new Emoticon(moodEvent.getEmotionalState(), 1).getImageLink());
        author.setText(moodEvent.getAuthor());
        relativeTime.setText(new RelativeTime(moodEvent.getDate(), moodEvent.getTime()).getRelativeTime());

        return view;
    }
}