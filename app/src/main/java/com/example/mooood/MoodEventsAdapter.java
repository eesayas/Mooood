package com.example.mooood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * This is an adapter for displaying MoodEvents in UserFeedActivity
 * The code is implemented to accommodate RecyclerView
 **/
public class MoodEventsAdapter extends RecyclerView.Adapter<MoodEventsAdapter.MoodEventViewHolder> {

    private List<MoodEvent> moodEventList;

    //constructor
    MoodEventsAdapter(List<MoodEvent> moodEventList){
        this.moodEventList = moodEventList;
    }

    //create view holder with content of user feed layout
    @NonNull
    @Override
    public MoodEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_user_feed, parent, false);
        return new MoodEventViewHolder(itemView);
    }

    //bind values to view
    @Override
    public void onBindViewHolder(MoodEventViewHolder holder, int position){
        MoodEvent moodEvent = moodEventList.get(position);

        holder.emoticon.setImageResource(new Emoticon(moodEvent.getEmotionalState(), 1).getImageLink());
        holder.author.setText(moodEvent.getAuthor());
        holder.relativeTime.setText(moodEvent.getDate());
    }

    //get size of list of MoodEvents
    @Override
    public int getItemCount(){
        return moodEventList.size();
    }

    //View holder for a MoodEvent
    public class MoodEventViewHolder extends RecyclerView.ViewHolder{
        private ImageView emoticon;
        private TextView author, relativeTime;

        private MoodEventViewHolder(View view){
            super(view);

            author = view.findViewById(R.id.author);
            emoticon = view.findViewById(R.id.emoticon) ;
            relativeTime = view.findViewById(R.id.date_and_time);

        }

    }
}